package au.coaas.sqem.handler;

import au.coaas.cqp.proto.*;
import au.coaas.sqem.entity.ExtendedCdqlConditionToken;
import au.coaas.sqem.exception.WrongOperatorException;
import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.ContextRequest;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.util.CollectionDiscovery;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextRequestHandler {

    private static final Logger LOG = Logger.getLogger(ContextRequestHandler.class.getName());

    private static final Map<String, Integer> OPERATION_PRIORITY_LIST = new HashMap<String, Integer>() {
        {
            put("=", 0);
            put("<", 0);
            put("<=", 0);
            put(">", 0);
            put(">=", 0);
            put("containsAny", 0);
            put("containsAll", 0);
            put("and", 2);
            put("&", 2);
            put("or", 2);
            put("|", 2);
            put("not", 1);
            put("!", 1);
            put(")", 3);
            put("(", 3);
        }
    };

    private static Object tokenToString(CdqlConditionToken token) throws WrongOperatorException {
        switch (token.getType()) {
            case Attribute:

                if (token.getContextAttribute().getPrefix() == null || token.getContextAttribute().getPrefix().isEmpty()) {
                    return token.getContextAttribute().getAttributeName();
                }
                return token.getContextAttribute().getPrefix();
            case Constant:
                switch (token.getConstantTokenType()) {
                    case Json:
                        return token.getStringValue().replaceAll("\"", "");
                    case Numeric:
                        return Double.valueOf(token.getStringValue().replaceAll("\"", ""));
                    case String:
                        return token.getStringValue().replaceAll("\"", "");
                    case Boolean:
                        return Boolean.parseBoolean(token.getStringValue());
                    default:
                        return token.getStringValue().replaceAll("\"", "");
                }
            default:
                throw new WrongOperatorException("The token " + token.getStringValue() + " with type " + token.getType().name()
                        + " is not accepted");
        }
    }

    private static void processSimpleLogicToken(String operator, Stack<ExtendedCdqlConditionToken> stack, Queue<CdqlConditionToken> RPNcondition) throws WrongOperatorException {
        String mongoQuery = null;
        boolean andFlag = false; //is next operation AND again?
        String tempStr;
        //how many operands shall we use?
        switch (operator) {
            case "=": {
                //if the token is numeric - we don't need ""
                ExtendedCdqlConditionToken operand2 = stack.pop();
                ExtendedCdqlConditionToken operand1 = stack.pop();


                if (operand1.getCdqlConditionToken().getType() == CdqlConditionTokenType.Constant && operand2.getCdqlConditionToken().getType() == CdqlConditionTokenType.Constant) {
                    stack.push(processConstantValues(operand1, operand2, operator));
                    break;
                }

                if (operand1.getCdqlConditionToken().getType() != CdqlConditionTokenType.Attribute) {
                    throwWrongOpException("the first operand in the expression should be an attribute",
                            operator, operand1.getCdqlConditionToken(), operand2.getCdqlConditionToken());
                }
                if (operand2.getCdqlConditionToken().getType() != CdqlConditionTokenType.Constant) {
                    throwWrongOpException("the second operand in the expression should be a constant value",
                            operator, operand1.getCdqlConditionToken(), operand2.getCdqlConditionToken());
                }

                String token1 = (String) tokenToString(operand1.getCdqlConditionToken());
                Bson eqDefault = Filters.eq(token1, tokenToString(operand2.getCdqlConditionToken()));
                stack.push(new ExtendedCdqlConditionToken(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Expression).build(), eqDefault));
                break;
            }

            case "and": {
                //using explicit $AND
                ExtendedCdqlConditionToken operand1 = stack.pop();
                ExtendedCdqlConditionToken operand2 = stack.pop();
                if (operand1.getCdqlConditionToken().getType() != CdqlConditionTokenType.Expression || operand2.getCdqlConditionToken().getType() != CdqlConditionTokenType.Expression) {
                    throwWrongOpException("Wrong Operand. Expression expected",
                            operator, operand1.getCdqlConditionToken(), operand2.getCdqlConditionToken());
                }
                List<Bson> bsons = new ArrayList<>();
                bsons.add(operand1.getBson());
                bsons.add(operand2.getBson());

                while (!RPNcondition.isEmpty() && "and".equals(RPNcondition.peek().getStringValue())) {
                    RPNcondition.poll();
                    ExtendedCdqlConditionToken operand3 = stack.pop();
                    if (operand3.getCdqlConditionToken().getType() != CdqlConditionTokenType.Expression) {
                        throwWrongOpException("Wrong Operand. Expression expected",
                                operator, operand2.getCdqlConditionToken(), operand3.getCdqlConditionToken());
                    }
                    bsons.add(operand3.getBson());
                }   //closing $AND clause
                Bson andObject = Filters.and(bsons);
                stack.push(new ExtendedCdqlConditionToken(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Expression).build(), andObject));
                break;
            }
            case "or": {
                ExtendedCdqlConditionToken operand1 = stack.pop();
                ExtendedCdqlConditionToken operand2 = stack.pop();
                if (operand1.getCdqlConditionToken().getType() != CdqlConditionTokenType.Expression || operand2.getCdqlConditionToken().getType() != CdqlConditionTokenType.Expression) {
                    throwWrongOpException("Wrong Operand. Expression expected",
                            operator, operand1.getCdqlConditionToken(), operand2.getCdqlConditionToken());
                }
                Bson orObject = Filters.or(operand1.getBson(), operand2.getBson());
                stack.push(new ExtendedCdqlConditionToken(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Expression).build(), orObject));
                break;
            }
            default: {
                // Comparison operators (except equality)  (less than and similar types of operators)
                ExtendedCdqlConditionToken operand2 = stack.pop();
                ExtendedCdqlConditionToken operand1 = stack.pop();

                if (operand1.getCdqlConditionToken().getType() == CdqlConditionTokenType.Constant && operand2.getCdqlConditionToken().getType() == CdqlConditionTokenType.Constant) {
                    stack.push(processConstantValues(operand1, operand2, operator));
                    break;
                }

                if (operand1.getCdqlConditionToken().getType() != CdqlConditionTokenType.Attribute) {
                    throwWrongOpException("the first operand in the expression should be an attribute",
                            operator, operand1.getCdqlConditionToken(), operand2.getCdqlConditionToken());
                }
                //Maybe only numeric and String????
                if (operand2.getCdqlConditionToken().getType() != CdqlConditionTokenType.Constant) {
                    throwWrongOpException("the second operand in the expression should be a constant value",
                            operator, operand1.getCdqlConditionToken(), operand2.getCdqlConditionToken());
                }
                String token1 = (String) tokenToString(operand1.getCdqlConditionToken());

                stack.push(new ExtendedCdqlConditionToken(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Expression).build(),
                        generateComparisonFilter(token1, tokenToString(operand2.getCdqlConditionToken()), operator)));

                //less than with complex functions like distance, cost, commute time, etc.
                //is parsed separately in a function that is responsible for these parameters
                //if there is "<" after, for example, distance function, this block of code is not executed
            }
        }

    }

    private static void processDistanceToken(CdqlConditionToken token, Stack<ExtendedCdqlConditionToken> stack, Queue<CdqlConditionToken> RPNcondition, String entityType) throws WrongOperatorException {

        String mongoQuery;

        FunctionCall fCall = token.getFunctionCall();

        //build a Mongo query
        ContextAttribute ca = (ContextAttribute) fCall.getArgumentsList().get(0).getContextAttribute();
        String location_prefix = ca.getAttributeName();

        if (ca.getPrefix() != null && !ca.getPrefix().isEmpty()) {
            location_prefix = ca.getPrefix();
        }

        String dest_lat = fCall.getArgumentsList().get(1).getStringValue().replaceAll("\"", "");
        String dest_lon = fCall.getArgumentsList().get(2).getStringValue().replaceAll("\"", "");

        String distance_str = RPNcondition.poll().getStringValue();
        double distance = Double.valueOf(distance_str);

        //get next comparison operator
        //only "<" supported by now
        String comparisonOp = RPNcondition.poll().getStringValue();
        if (!"<".equals(comparisonOp)) {
            LOG.log(Level.INFO, "{0} not supported with Distance function", comparisonOp);

            //WE NEED TO RETURN ERROR HERE
            String result = "Only < (less than) is supported with Distance function";
            throw new WrongOperatorException(result);

        }
        Bson eqDefault = Filters.geoWithinCenterSphere(location_prefix, Double.valueOf(dest_lon), Double.valueOf(dest_lat), distance / 6371000.0);
        stack.push(new ExtendedCdqlConditionToken(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Expression).build(), eqDefault));
    }

    private static Bson generateComparisonFilter(String token1, Object token2, String op) throws WrongOperatorException {

        switch (op) {
            case "!=": {
                return Filters.ne(token1, token2);
            }
            case "containsAll": {
                if (token2 instanceof String && ((String) token2).trim().startsWith("[")) {
                    String[] values = ((String) token2).trim().substring(1, ((String) token2).trim().length() - 1).split(",");
                    return Filters.all(token1, values);
                } else {
                    return Filters.all(token1, token2);
                }
            }
            case "containsAny": {
                if (token2 instanceof String && ((String) token2).trim().startsWith("[")) {
                    String[] values = ((String) token2).trim().substring(1, ((String) token2).trim().length() - 1).split(",");
                    return Filters.in(token1, values);
                } else {
                    return Filters.in(token1, token2);
                }
            }
            case ">=": {
                return Filters.gte(token1, token2);
            }
            case "<=": {
                return Filters.lte(token1, token2);
            }
            case ">": {
                return Filters.gt(token1, token2);
            }
            case "<": {
                return Filters.lt(token1, token2);
            }
        }
        throw new WrongOperatorException("operator " + op + " not recognized");
    }

    private static void throwWrongOpException(String msg, String operator, String operand1, String operand2) throws WrongOperatorException {
        throw new WrongOperatorException(msg + " in : "
                + operand1 + " " + operator + " " + operand2);
    }

    private static void throwWrongOpException(String msg, String operator, CdqlConditionToken operand1, CdqlConditionToken operand2) throws WrongOperatorException {
        throw new WrongOperatorException(msg + " in : "
                + operand1.getStringValue() + " " + operator + " " + operand2.getStringValue());
    }

    private static ExtendedCdqlConditionToken processConstantValues(ExtendedCdqlConditionToken operand1, ExtendedCdqlConditionToken operand2, String operator) throws WrongOperatorException {
        ExtendedCdqlConditionToken tokenTrue = new ExtendedCdqlConditionToken(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Expression).build(), Filters.exists("VatanamVatanamVatanamVatanam", false));
        ExtendedCdqlConditionToken tokenFalse = new ExtendedCdqlConditionToken(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Expression).build(), Filters.exists("VatanamVatanamVatanamVatanam", true));
        try {
            if (operand1.getCdqlConditionToken().getConstantTokenType() == CdqlConstantConditionTokenType.Numeric) {
                int res = Double.valueOf(operand1.getCdqlConditionToken().getStringValue()).compareTo(Double.valueOf(operand2.getCdqlConditionToken().getStringValue()));
                switch (operator) {
                    case ">":
                        return res > 0 ? tokenTrue : tokenFalse;
                    case ">=":
                        return res >= 0 ? tokenTrue : tokenFalse;
                    case "=":
                        return res == 0 ? tokenTrue : tokenFalse;
                    case "<=":
                        return res <= 0 ? tokenTrue : tokenFalse;
                    case "<":
                        return res < 0 ? tokenTrue : tokenFalse;
                    case "!=":
                        return res != 0 ? tokenTrue : tokenFalse;
                }
            } else {
                switch (operator) {
                    case "=":
                        return operand1.getCdqlConditionToken().getStringValue().equals(operand2.getCdqlConditionToken().getStringValue()) ? tokenTrue : tokenFalse;
                    case "!=":
                        return !operand1.getCdqlConditionToken().getStringValue().equals(operand2.getCdqlConditionToken().getStringValue()) ? tokenTrue : tokenFalse;
                }
            }
        } catch (Exception e) {

        }
        throwWrongOpException("oppps! Wrong op", operator, operand1.toString(), operand2.toString());
        return null;
    }

    public static SQEMResponse handle(ContextRequest contextRequest) {
        Set<String> conditionContextAttribiutes = new HashSet<>();

        String entityType = contextRequest.getEt().getType();

        Queue<CdqlConditionToken> RPNcondition = new LinkedList<>(contextRequest.getCondition().getRPNConditionList());

        Stack<ExtendedCdqlConditionToken> stack = new Stack<>();


        Bson finalQuery = new BasicDBObject();
        if (!RPNcondition.isEmpty()) {
            ExtendedCdqlConditionToken extendedToken = new ExtendedCdqlConditionToken(RPNcondition.poll());
            CdqlConditionToken token = extendedToken.getCdqlConditionToken();
            try {
                while (token != null) {
                    switch (token.getType()) {
                        case Attribute:
                            conditionContextAttribiutes.add((String) tokenToString(token));
                            stack.push(new ExtendedCdqlConditionToken(token));
                            break;
                        case Constant:
                            stack.push(extendedToken);
                            break;
                        case Function:

                            for (Operand argument : token.getFunctionCall().getArgumentsList()) {
                                if (argument.getType() == OperandType.CONTEXT_ATTRIBUTE) {
                                    ContextAttribute ca = argument.getContextAttribute();
                                    String location_prefix = ca.getAttributeName();
                                    if (ca.getPrefix() != null && !ca.getPrefix().isEmpty()) {
                                        location_prefix = ca.getPrefix();
                                    }
                                    conditionContextAttribiutes.add(location_prefix);
                                }
                            }
                            switch (token.getFunctionCall().getFunctionName().toLowerCase()) {
                                case "distance":
                                    processDistanceToken(token, stack, RPNcondition, entityType);
                                    break;
                            }
                            break;
                        case Operator:
                            processSimpleLogicToken(token.getStringValue(), stack, RPNcondition);
                            break;
                    }
                    extendedToken = new ExtendedCdqlConditionToken(RPNcondition.poll());
                    token = extendedToken.getCdqlConditionToken();
                }
            } catch (Exception e) {
                return createErrorResponse(e.getMessage());
            }

            finalQuery = stack.pop().getBson();
        }

        MongoDatabase db = ConnectionPool.getInstance().getMongoClient().getDatabase("mydb");
        MongoCollection<Document> entityCollection = db.getCollection(CollectionDiscovery.discover(contextRequest.getEt()));
        final JSONArray finalResultJsonArr = new JSONArray();
        String monogQueryString = finalQuery.toBsonDocument(org.bson.BsonDocument.class, MongoClient.getDefaultCodecRegistry()).toJson();

        // LOG.info(monogQueryString);

        Block<Document> printBlock = new Block<Document>() {
            @Override
            public void apply(final Document document) {

                //We need to filter out results, that passed initial datastore filtering, but do not comply with complex attributes
                JSONObject resultJSON = new JSONObject(document.toJson());

                //FINAL ARRAY (return to Query Engine)
                finalResultJsonArr.put(resultJSON);

            }
        };

        //ToDo add limit and sort function
//        entityCollection.find(finalQuery).limit(10).forEach(printBlock);
        entityCollection.find(finalQuery).forEach(printBlock);
        return SQEMResponse.newBuilder().setStatus("200").setBody(finalResultJsonArr.toString()).build();
    }

    private static SQEMResponse createErrorResponse(String errorMessage) {
        return SQEMResponse.newBuilder().setBody(errorMessage).setStatus("500").build();
    }
}
