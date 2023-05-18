package au.coaas.cqc.executor;

import au.coaas.base.proto.ListOfString;
import au.coaas.cqc.proto.RegisterState;
import au.coaas.cqc.utils.SiddhiQueryGenerator;
import au.coaas.cqp.proto.*;
import au.coaas.cqc.utils.Utilities;
import au.coaas.cre.proto.CREServiceGrpc;
import au.coaas.cre.proto.CRESituation;
import au.coaas.cre.proto.ContextEvent;
import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqc.utils.enums.HttpRequests;
import au.coaas.cqc.utils.enums.RequestDataType;

import au.coaas.cre.proto.SiddhiRegister;
import au.coaas.grpc.client.CREChannel;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.RegisterPushQuery;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.SituationFunctionRequest;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.Message;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static au.coaas.cqc.utils.SiddhiQueryGenerator.generateQueries;

public class PushBasedExecutor {
    // This is the context push dedugging switch
    private static boolean isDebugMode = false;
    // This is the cache switch
    private static boolean cacheEnabled = true;
    private static Map<String, ScheduledFuture<?>> scheduledJobs = new HashMap<>();
    private static ScheduledExecutorService execService = Executors.newScheduledThreadPool(15);

    private static Logger log = Logger.getLogger(PushBasedExecutor.class.getName());

    public static CdqlResponse executePushBaseQuery(CDQLQuery query, String token, String queryId,
                                                    String criticality, double complexity) throws IOException {

        SubscribedQuery.Builder cdqlSubscription = SubscribedQuery.newBuilder();
        cdqlSubscription.setCallback(query.getCallback());
        cdqlSubscription.setCriticality(criticality);
        cdqlSubscription.setComplexity(complexity);
        cdqlSubscription.setToken(token);
        cdqlSubscription.setQuery(query);

        // Initializing variables
        Map<String, ListOfString> dependency = query.getWhen().getCondition().getDependencyMap();
        Map<String, SituationFunction> situations = new HashMap<>();
        List<FunctionCall> functionCalls = new ArrayList<>();
        Set<String> usedSiddhifunctions = new HashSet<>();

        boolean addTimer = false;
        final StringBuilder siddhiQueryBody = new StringBuilder();
        List<FunctionCall> fCalls = getFunctionCalls(
                new LinkedList<>(query.getWhen().getCondition().getRPNConditionList()));

        for (FunctionCall functionCall : fCalls) {
            String functionName = functionCall.getFunctionName();
            functionCalls.add(functionCall);
            switch(functionName) {
                case "decrease": {
                    usedSiddhifunctions.add("decrease");
                    siddhiQueryBody.append(
                            generateQueries("decrease",
                                    Double.valueOf(functionCall.getArguments(1).getStringValue()),
                                    Utilities.messageToJson(functionCall)));
                    break;
                }
                case "increase": {
                    usedSiddhifunctions.add("increase");
                    siddhiQueryBody.append(
                            generateQueries("increase",
                                    Double.valueOf(functionCall.getArguments(1).getStringValue()),
                                    Utilities.messageToJson(functionCall)));
                    break;
                }
                case "isValid": {
                    usedSiddhifunctions.add("isValid");
                    siddhiQueryBody.append(
                            generateQueries("isValid",
                                    Double.valueOf(functionCall.getArguments(1).getStringValue()),
                                    Utilities.messageToJson(functionCall)));
                    break;
                }
                case "currentTime": {
                    addTimer = true;
                    break;
                }
                default: {
                    try {
                        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
                        SituationFunction situationFunction = sqemStub.findSituationByTitle(
                                SituationFunctionRequest.newBuilder().setName(functionName).build());
                        if (situationFunction != null)
                            situations.put(functionName, situationFunction);
                    }
                    catch (Exception ex) {
                        log.severe("Not a valid function for push queries or some error occurred in" +
                                queryId + ": " + ex.getMessage());
                    }
                }
            }
        }

        Set<String> relatedEntitiesTitles = new HashSet<>(dependency.keySet());
        Set<String> tempEntitiesTitles = new HashSet<>(dependency.keySet());

        while (!tempEntitiesTitles.isEmpty()) {
            Set<String> newEntities = new HashSet<>();
            for (String entityID : new HashSet<>(tempEntitiesTitles)) {
                Optional<ContextEntity> findEntity = query.getDefine()
                        .getDefinedEntitiesList().stream()
                        .filter(ent -> ent.getEntityID().equals(entityID))
                        .findFirst();
                if(findEntity.isPresent()){
                    for (String tempEntityID : findEntity.get().getCondition().getDependencyMap().keySet()) {
                        if (!relatedEntitiesTitles.contains(tempEntityID)) {
                            newEntities.add(tempEntityID);
                        }
                    }
                }
            }
            relatedEntitiesTitles.addAll(newEntities);
            tempEntitiesTitles = new HashSet<>(newEntities);
        }

        List<ContextEntity> relateCdqlSubscriptionEntities = new ArrayList<>();

        for (String entityID : relatedEntitiesTitles) {
            Optional<ContextEntity> contextEntity = query.getDefine()
                    .getDefinedEntitiesList().stream()
                    .filter(ent -> ent.getEntityID().equals(entityID))
                    .findFirst();
            if(contextEntity.isPresent()){
                ContextEntity.Builder csEntity = ContextEntity.newBuilder();
                csEntity.setEntityID(entityID);

                ListOfString relatedAttributes = dependency.get(entityID);
                csEntity.setType(contextEntity.get().getType());

                Condition.Builder cseCondition = Condition.newBuilder();
                cseCondition.addAllRPNCondition(contextEntity.get().getCondition().getRPNConditionList());
                csEntity.setCondition(cseCondition.build());

//                Set<String> cseAttributes = new HashSet<>();
//                if (relatedAttributes != null) {
//                    cseAttributes.addAll(relatedAttributes.getValueList());
//                }
//                cseAttributes.addAll(contextEntity.get().getContextAttributesMap().keySet());
//
//                for (FunctionCall functionCall : functionCalls) {
//                    if (situations.containsKey(functionCall.getFunctionName())) {
//                        SituationFunction situ = situations.get(functionCall.getFunctionName());
//                        for (Operand argument : functionCall.getArgumentsList()) {
//                            if (argument.getType() == OperandType.CONTEXT_ENTITY && argument.getStringValue().equals(entityID)) {
//                                String entityType = contextEntity.get().getType().getType();
//                                Map.Entry<String, ContextEntityType> findEntity = situ.getRelatedEntitiesMap().entrySet().stream()
//                                        .filter(p -> p.getValue().toString().equals(entityType))
//                                        .findFirst().get();
//                                String prefix = findEntity.getKey();
//                                for (String attr : situ.getAllAttributesList()) {
//                                    if (attr.startsWith(prefix + "dot")) {
//                                        cseAttributes.add(attr.substring((prefix + "dot").length()));
//                                    }
//                                }
//                                situ.getRelatedEntitiesMap().remove(findEntity.getKey());
//                            }
//
//                        }
//                    }
//                }

//                csEntity.putAllContextAttributes(new ArrayList<>(cseAttributes));
                // Uncomment above, and comment the below line, if there is anything wrong with attribute management

                csEntity.putAllContextAttributes(contextEntity.get().getContextAttributesMap());
                relateCdqlSubscriptionEntities.add(csEntity.build());
            }
        }

        cdqlSubscription.addAllRelatedEntities(relateCdqlSubscriptionEntities);
        cdqlSubscription.addAllSituation(query.getWhen().getCondition().getRPNConditionList());

        // Registering the push query and subscriptions
        String jsonInString = Utilities.messageToJson(cdqlSubscription.build());
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        RegisterPushQuery sub_id = sqemStub.registerPushQuery(
                RegisterPushQuery.newBuilder().setMessage(jsonInString).build());

        if(!sub_id.getStatus().equals("200"))
            return CdqlResponse.newBuilder().setStatus("500")
                    .setBody("Error occured in push query registration.").build();

        // Setting event monitoring in Siddhi
        cdqlSubscription.setId(sub_id.getMessage());
        if (usedSiddhifunctions.size() > 0) {
            // Sending the request to CRE to register with Siddhi.
            CREServiceGrpc.CREServiceBlockingStub creStub
                    = CREServiceGrpc.newBlockingStub(CREChannel.getInstance().getChannel());
            CRESituation response = creStub.registerInSiddhi(SiddhiRegister.newBuilder()
                    .setJson(SiddhiQueryGenerator.registerQuery(cdqlSubscription.getId(), siddhiQueryBody))
                    .addAllUsedFunctions(usedSiddhifunctions).build());

            if(response.getStatus().equals("200")){
                log.info("Push query " + sub_id.getMessage() + " successfully registered in Siddhi.");
            }
            else {
                log.severe("Push query " + sub_id.getMessage() + " FAILED to registered in Siddhi.");
                log.severe(response.getBody());
            }
        }

        if (addTimer) {
            ScheduledFuture<?> sf = execService.scheduleAtFixedRate(() -> {
                handelSubscription(cdqlSubscription.build());
            }, 0, 60000L, TimeUnit.MILLISECONDS);
            scheduledJobs.put(cdqlSubscription.getId(), sf);
        }

        return CdqlResponse.newBuilder().setStatus("200")
                .setBody("Push query "+ sub_id.getMessage() +" successfully subscribed!").build();
    }

    private static List<FunctionCall> getFunctionCalls(Queue<CdqlConditionToken> tokens) {
        List<FunctionCall> result = new ArrayList<>();
        for (CdqlConditionToken token : tokens) {
            if (token.getType() == CdqlConditionTokenType.Function) {
                FunctionCall fCall = token.getFunctionCall();
                result.add(fCall);
                result.addAll(getSubFunctionCalls(fCall));
            }
        }
        return result;
    }

    private static Gson gson = new Gson();
    private static List<FunctionCall> getSubFunctionCalls(FunctionCall fCall) {
        List<FunctionCall> result = new ArrayList<>();
        for (Operand argument : fCall.getArgumentsList()) {
            if (argument.getType() == OperandType.FUNCTION_CALL) {
                FunctionCall subFCall = gson.fromJson(gson.toJson(argument.getStringValue()), FunctionCall.class);
                result.add(subFCall);
                result.addAll(getSubFunctionCalls(subFCall));
            }
        }
        return result;
    }

    // Triggers a recursive event to CoaaS
    public static void sendEvent(ContextEvent event) throws InvalidProtocolBufferException {
        String eventURI = "http://localhost:8070/CASM-2.0.1/api/event/create";
        Utilities.httpCall(eventURI, HttpRequests.POST, RequestDataType.JSON,
                null, JsonFormat.printer().print(event));
    }

    // PUSH Context Information to the Context Consumers
    public static void pushContext(CDQLCallback callBack, JSONObject result, String subsID) throws IOException, InterruptedException, ExecutionException {
        switch (callBack.getCallbackMethod()) {
            case FCM:
                if (FirebaseApp.getApps().size() == 0) {
                    InputStream serviceAccount = new ByteArrayInputStream(Utilities.serverConfig.getBytes(StandardCharsets.UTF_8));
                    FirebaseOptions options = new FirebaseOptions.Builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .setDatabaseUrl("https://zippy-tiger-550.firebaseio.com")
                            .build();
                    FirebaseApp.initializeApp(options);
                }
                Message.Builder message = Message.builder()
                        .putData("body", callBack.getBody() == null ?
                                ("push from CoaaS with subID : " + subsID) : callBack.getBody())
                        .putData("results", result.toString());

                if (callBack.getFcmTopic() != null)
                    message.setTopic(callBack.getFcmTopic());
                else
                    message.setToken(callBack.getFcmID());

                FirebaseMessaging.getInstance().sendAsync(message.build()).get();
                break;
            case HTTPPOST:
                sendODFWrite(callBack.getHttpURL(), callBack.getBody(), callBack.getHeaders(), callBack);
                break;
        }

        if (isDebugMode) {
            if (FirebaseApp.getApps().size() == 0) {
                InputStream serviceAccount = new ByteArrayInputStream(Utilities.serverConfig.getBytes(StandardCharsets.UTF_8));
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://zippy-tiger-550.firebaseio.com")
                        .build();
                FirebaseApp.initializeApp(options);
            }
            Message message = Message.builder()
                    .putData("body", callBack.getBody() == null ? "push from CoaaS" : callBack.getBody())
                    .setTopic("news")
                    .build();
            FirebaseMessaging.getInstance().sendAsync(message).get();

            String odfMsg = "<omiEnvelope xmlns=\"http://www.opengroup.org/xsd/omi/1.0/\" version=\"1.0\" ttl=\"0\">\n"
                    + "  <write msgformat=\"odf\">\n"
                    + "    <msg>\n"
                    + "      <Objects xmlns=\"http://www.opengroup.org/xsd/odf/1.0/\">\n"
                    + "        <Object name = \"push log\">\n"
                    + "          <id>push_log</id>\n"
                    + "          	<Object name = \"sub" + subsID + "\">\n"
                    + "              <id>sub_" + subsID + "</id>\n"
                    + "              <InfoItem name=\"push msg\">\n"
                    + ("push from CoaaS with subID : " + subsID)
                    + "              </InfoItem>\n"
                    + "            </Object>\n"
                    + "          </Object>\n"
                    + "        </Object>\n"
                    + "      </Objects>\n"
                    + "    </msg>\n"
                    + "  </write>\n"
                    + "</omiEnvelope>";

            sendODFWrite("http://localhost:8282", odfMsg, null, callBack);
        }

        Date now = new Date();
        SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));

        log.info("Subscription with subscription ID : " + subsID + " triggred. \n"
                + "Localtime : " + now.toString() + " \n"
                + "UTC time : " + dateFormatUTC.format(now) + "\n");

        cancelJob(subsID);
    }

    private static void sendODFWrite(String URI, String msg, Map<String, String> headers, CDQLCallback callback) {
        try {
            // TODO: Incase the callback has an authentication requirement, the following has to be uncommented.
            // if (callback.getAuthorizationType() != null &&
            //         (callback.getAuthorizationType().equalsIgnoreCase("basic auth") ||
            //                 callback.getAuthorizationType().equalsIgnoreCase("basic"))) {
            //     String credential = Credentials.basic(callback.getUserName(), callback.getPassword());
            //     requestBuilder.addHeader("Authorization", credential);
            // }

            Utilities.httpCall(URI, HttpRequests.POST, RequestDataType.XML, headers, msg);
        } catch (Exception ex) {
            log.severe("Error in pushing context via HTTPS: " + ex.getMessage());
        }
    }

    public static RegisterState cancelJob(String subID) {
        try {
            if (scheduledJobs.containsKey(subID)) {
                scheduledJobs.get(subID).cancel(false);
                return RegisterState.newBuilder().setState(true).build();
            }
        } catch (Exception ex) {
            log.severe("Error occurred when canceling a scheduled push subscription.");
            log.severe(String.valueOf(ex.getStackTrace()));
        }
        return RegisterState.newBuilder().setState(false).build();
    }

    private static void handelSubscription(SubscribedQuery subscription) {
        try {
            CdqlResponse executePullBaseQuery = PullBasedExecutor.executePullBaseQuery(
                    subscription.getQuery(), subscription.getToken(),-1, -1,
                    subscription.getQueryId(), subscription.getCriticality(),
                    subscription.getComplexity());

            if(executePullBaseQuery.getStatus().equals("200")){
                JSONObject jsonObject = new JSONObject(executePullBaseQuery.getBody());
                pushContext(subscription.getCallback(), jsonObject, subscription.getId());
            }
            else log.severe("Problem with executing the query for subscription: " + subscription.getId());

        }
        catch(Exception ex){
            log.severe("Exception occured when handling periodic push query: " + subscription.getId());
        }
    }
}
