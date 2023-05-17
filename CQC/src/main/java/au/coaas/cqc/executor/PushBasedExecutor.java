package au.coaas.cqc.executor;

import au.coaas.cqc.proto.EventStats;
import au.coaas.cqc.proto.RegisterState;
import au.coaas.cqp.proto.*;
import au.coaas.cqc.utils.Utilities;
import au.coaas.cre.proto.ContextEvent;
import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqc.utils.enums.HttpRequests;
import au.coaas.cqc.utils.enums.RequestDataType;

import com.google.firebase.FirebaseApp;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.Message;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import java.util.*;
import java.util.logging.Logger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

public class PushBasedExecutor {
    // This is the context push dedugging switch
    private static boolean isDebugMode = false;
    // This is the cache switch
    private static boolean cacheEnabled = true;
    private static Map<String, ScheduledFuture<?>> scheduledJobs = new HashMap<>();

    private static Logger log = Logger.getLogger(PushBasedExecutor.class.getName());

    public static CdqlResponse executePushBaseQuery(CDQLQuery query, String queryId, double complexity) {
        // TODO: Register push queries only
        return null;
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
            // TODO: Incase the callback has an aiuthication requirement, the following has to be uncommented.
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
}
