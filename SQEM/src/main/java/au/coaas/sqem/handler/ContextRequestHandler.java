package au.coaas.sqem.handler;

import au.coaas.cqp.proto.*;

import au.coaas.sqem.util.MongoBlock;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.mongo.ConnectionPool;
import au.coaas.sqem.proto.ContextRequest;
import au.coaas.sqem.util.CollectionDiscovery;
import au.coaas.sqem.exception.WrongOperatorException;
import au.coaas.sqem.entity.ExtendedCdqlConditionToken;

import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextRequestHandler {

    private static final int PAGE_SIZE = 2000;

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

    private static Object tokenToString(CdqlConditionToken token, String attribute) throws WrongOperatorException {
        switch (token.getType()) {
            case Attribute:
                // What is the purpose of prefix to the attribute? Why check it?
                if (token.getContextAttribute().getPrefix() == null || token.getContextAttribute().getPrefix().isEmpty()) {
                    // Why concat the attribute with attribute?
                    return attribute +  token.getContextAttribute().getAttributeName();
                }
                // If the prefix is in the token, why concat with the attribute before it later?
                // According to line 418, the 2nd parameter 'attribute' is rather the attribute prefix.
                // So, the result is like "coaas:samples.coaas:value.temperature"
                return attribute + token.getContextAttribute().getPrefix();
            case Constant:
                switch (token.getConstantTokenType()) {
                    // This one is clear. If the token is a "constant" type, then read the value in string format
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

    // This method process logical and mathematical operations.
    private static void processSimpleLogicToken(String operator, Stack<ExtendedCdqlConditionToken> stack, Queue<CdqlConditionToken> RPNcondition, String attributePrefix) throws WrongOperatorException {
        String mongoQuery = null;
        boolean andFlag = false; //is next operation AND again?
        String tempStr;
        //how many operands shall we use?
        // Seems the number of operands of fixed to 2. What if there is more than 2? Possibly all the actions poping from the stack would go out of sync too.
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

                // The following logic makes sense when the operation is like if(var = 3) type. So, the value compared the variable is constant.
                // But what if, (var1 = var2)? then both should be an attribute (ideally, the same attribute of may be a target entity)?
                if (operand2.getCdqlConditionToken().getType() != CdqlConditionTokenType.Constant) {
                    throwWrongOpException("the second operand in the expression should be a constant value",
                            operator, operand1.getCdqlConditionToken(), operand2.getCdqlConditionToken());
                }

                String token1 = (String) tokenToString(operand1.getCdqlConditionToken(), attributePrefix);
                Bson eqDefault = Filters.eq(token1, tokenToString(operand2.getCdqlConditionToken(), attributePrefix));
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
                    // I'm not clear what the 3rd operand is about?
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
                // Compared to the AND operator, OR is clear. This doesn't have the 3rd operand.
                Bson orObject = Filters.or(operand1.getBson(), operand2.getBson());
                stack.push(new ExtendedCdqlConditionToken(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Expression).build(), orObject));
                break;
            }
            default: {
                // Comparison operators (except equality)  (less than and similar types of operators)
                // So, only '>' and '<'.
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
                // Maybe only numeric and String????
                // Same comment as the equality condition. What if the comparison is nested? e.g., var1 < (var2 +1)
                // I'm also not clear how conditions like ent.att < {'unit':'m','value': 5} OR distance() < {'unit':'km','value': 1} is handled?
                if (operand2.getCdqlConditionToken().getType() != CdqlConditionTokenType.Constant) {
                    throwWrongOpException("the second operand in the expression should be a constant value",
                            operator, operand1.getCdqlConditionToken(), operand2.getCdqlConditionToken());
                }
                String token1 = (String) tokenToString(operand1.getCdqlConditionToken(), attributePrefix);

                stack.push(new ExtendedCdqlConditionToken(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Expression).build(),
                        generateComparisonFilter(token1, tokenToString(operand2.getCdqlConditionToken(), attributePrefix), operator)));

                //less than with complex functions like distance, cost, commute time, etc.
                //is parsed separately in a function that is responsible for these parameters
                //if there is "<" after, for example, distance function, this block of code is not executed
            }
        }

    }

    // This methods compiles a BSON object to run the 'distance' aggregation function
    private static void processDistanceToken(CdqlConditionToken token, Stack<ExtendedCdqlConditionToken> stack, Queue<CdqlConditionToken> RPNcondition, String entityType, String attributePrefix) throws WrongOperatorException {

        FunctionCall fCall = token.getFunctionCall();

        //build a Mongo query
        // Gets the first context attribute in the list. But why only the first one?
        // Got it. So, the argument list is a like a indexed parameter list. The parameters by index is as follows:
        // 0 - attribute name, 1 - destination_lat, 2 - destination_long
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
        // This is one of the places where the context of a query is placed in the hierachy affects the retriveal decision.
        // This is a BSON function from mongo. Basically a filter function (to filter out entities that falls inside the sphere) defined in BSON.
        // Why the radius is divided by 6371000.0?
        Bson eqDefault = Filters.geoWithinCenterSphere(attributePrefix + location_prefix, Double.valueOf(dest_lon), Double.valueOf(dest_lat), distance / 6371000.0);
        stack.push(new ExtendedCdqlConditionToken(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Expression).build(), eqDefault));
    }

    // This methods compiles a BSON object needed to run the geo fencing in sphere aggregation function
    private static void processGeoInsideToken(CdqlConditionToken token, Stack<ExtendedCdqlConditionToken> stack, Queue<CdqlConditionToken> RPNcondition, String entityType, String attributePrefix) throws WrongOperatorException {

        FunctionCall fCall = token.getFunctionCall();

        //build a Mongo query
        //0 - attribute name, 1 - geo-type, 2 - coordinates
        ContextAttribute ca = (ContextAttribute) fCall.getArgumentsList().get(0).getContextAttribute();
        String location_prefix = ca.getAttributeName();

        if (ca.getPrefix() != null && !ca.getPrefix().isEmpty()) {
            location_prefix = ca.getPrefix();
        }

        String geoType = fCall.getArgumentsList().get(1).getStringValue().replaceAll("\"", "");
        String coordinates = fCall.getArgumentsList().get(2).getStringValue().replaceAll("\"", "");

        String inside_str = RPNcondition.poll().getStringValue();
        // I'm guessing the state makes the selection outer (entities not inside the sphere), or inner
        boolean  isNot = !Boolean.valueOf(inside_str);

        //get next comparison operator
        //only "=" supported
        String comparisonOp = RPNcondition.poll().getStringValue();
        if (!"=".equals(comparisonOp)) {
            LOG.log(Level.INFO, "{0} not supported with Distance function", comparisonOp);

            //WE NEED TO RETURN ERROR HERE
            String result = "Only = (equal) is supported with geoInside function";
            throw new WrongOperatorException(result);

        }

        Document geometry = new Document();

        geometry.put("type",geoType);
        geometry.put("coordinates", Document.parse("{\"temp\":"+coordinates+"}").get("temp"));

        Bson eqDefault = Filters.geoWithin(attributePrefix + location_prefix, geometry);
        if(isNot){
            // In the above line, the filter applies to finding the entities within the sphere.
            // Then by the following, the compliment of the filter result is taken.
            eqDefault = Filters.not(eqDefault);
        }
        stack.push(new ExtendedCdqlConditionToken(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Expression).build(), eqDefault));
    }

    // This methods compiles a BSON object needed to run the geo fencing in rectangle aggregation function
    private static void processGeoInsideBBoxToken(CdqlConditionToken token, Stack<ExtendedCdqlConditionToken> stack, Queue<CdqlConditionToken> RPNcondition, String entityType, String attributePrefix) throws WrongOperatorException {

        FunctionCall fCall = token.getFunctionCall();

        //build a Mongo query
        //0 - attribute name, 1 - left_x, 2 - left_y, 3 - right_x, 4-right_y
        ContextAttribute ca = (ContextAttribute) fCall.getArgumentsList().get(0).getContextAttribute();
        String location_prefix = ca.getAttributeName();

        if (ca.getPrefix() != null && !ca.getPrefix().isEmpty()) {
            location_prefix = ca.getPrefix();
        }

        Double left_x = Double.valueOf(fCall.getArgumentsList().get(1).getStringValue().replaceAll("\"", ""));
        Double left_y = Double.valueOf(fCall.getArgumentsList().get(2).getStringValue().replaceAll("\"", ""));
        Double right_x = Double.valueOf(fCall.getArgumentsList().get(3).getStringValue().replaceAll("\"", ""));
        Double right_y = Double.valueOf(fCall.getArgumentsList().get(4).getStringValue().replaceAll("\"", ""));

        boolean  isNot = !Boolean.valueOf(RPNcondition.poll().getStringValue());

        //get next comparison operator
        //only "=" supported
        String comparisonOp = RPNcondition.poll().getStringValue();
        if (!"=".equals(comparisonOp)) {
            LOG.log(Level.INFO, "{0} not supported with Distance function", comparisonOp);

            //WE NEED TO RETURN ERROR HERE
            String result = "Only = (equal) is supported with geoInside function";
            throw new WrongOperatorException(result);

        }

        Bson eqDefault = Filters.geoWithinBox(attributePrefix + location_prefix, left_x,left_y,right_x,right_y);

        if(isNot){
            eqDefault = Filters.not(eqDefault);
        }

        stack.push(new ExtendedCdqlConditionToken(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Expression).build(), eqDefault));
    }

    // In SimpleTokenProcessing method
    // Comparison operators (except equality)  (less than and similar types of operators)
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

    // Used in equality and other comparison operators
    private static ExtendedCdqlConditionToken processConstantValues(ExtendedCdqlConditionToken operand1, ExtendedCdqlConditionToken operand2, String operator) throws WrongOperatorException {
        // What is this? What is the field name?
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

    // This is the main method here.
    // This handles 'context requests'. In my words, this an sub-query to fetch an entity.
    public static SQEMResponse handle(ContextRequest contextRequest) {
        // Local variable definitions
        Set<String> conditionContextAttribiutes = new HashSet<>();

        String entityType = contextRequest.getEt().getType();

        Queue<CdqlConditionToken> RPNcondition = new LinkedList<>(contextRequest.getCondition().getRPNConditionList());

        Stack<ExtendedCdqlConditionToken> stack = new Stack<>();

        // What is meant by is historical query? Whether the query has been executed lately with in teh window? or previously executed at any time?
        // The proto has this time window - start and end time. What is the use?
        Boolean isHistoricalQuery = contextRequest.hasMeta() && contextRequest.getMeta().hasTimeWindow();

        // The prefix is custom set here
        String attributePrefix = isHistoricalQuery ? "coaas:samples.coaas:value." : "";

        // Constructor to the BSON object to save the context query in MongoDB
        Bson finalQuery = new BasicDBObject();
        // This condition is kind of redundant because
        // RPNCondition contains all the Context Query Tokens - attributes, operators, constants etc.
        // So, there won't be a situation where there is not tokens for a context request.
        if (!RPNcondition.isEmpty()) {
            // What's in the head?
            // What is the definition of an ExtendedCdqlConditionToken?
            // This is a constructor to ExtendedCdqlConditionToken object in SQEM
            ExtendedCdqlConditionToken extendedToken = new ExtendedCdqlConditionToken(RPNcondition.poll());
            // This value in 'token' is as same as the return value of RPNcondition.poll()
            CdqlConditionToken token = extendedToken.getCdqlConditionToken();
            try {
                // Token seems to be rather a list of tokens
                // Goes through each token and put them into individual list and queues defined locally above in the function
                while (token != null) {
                    switch (token.getType()) {
                        case Attribute:
                            conditionContextAttribiutes.add((String) tokenToString(token, attributePrefix));
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
                            // It seems that CoaaS right now hard coded to support 3 aggregations functions related to location only.
                            switch (token.getFunctionCall().getFunctionName().toLowerCase()) {
                                case "distance":
                                    processDistanceToken(token, stack, RPNcondition, entityType, attributePrefix);
                                    break;
                                case "geoinside":
                                    processGeoInsideToken(token, stack, RPNcondition, entityType, attributePrefix);
                                    break;
                                case "geoinsidebox":
                                    processGeoInsideBBoxToken(token, stack, RPNcondition, entityType, attributePrefix);
                                    break;
                            }
                            break;
                        case Operator:
                            processSimpleLogicToken(token.getStringValue(), stack, RPNcondition, attributePrefix);
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

        // Each of the entity has it's own collection in MongoDB. This is limited ny number.
        String collectionName = CollectionDiscovery.discover(contextRequest.getEt());

        // Mongo Block is a page. This way, the responses are retrieved in manageable sized blocks.
        // What impact does this have on caching?
        MongoBlock printBlock = new MongoBlock();

        if (isHistoricalQuery) {
            Long start = contextRequest.getMeta().getTimeWindow().getStart();
            Long end = contextRequest.getMeta().getTimeWindow().getEnd();
            //todo page
            queryHistoricalData(collectionName, finalQuery, conditionContextAttribiutes, start, end,printBlock, contextRequest.getPage(), contextRequest.getLimit());
        } else {
            String monogQueryString = finalQuery.toBsonDocument(org.bson.BsonDocument.class, MongoClient.getDefaultCodecRegistry()).toJson();
            // There are at least 2 mongo db databases here. 1 is 'coaas' which is used here. Let's say, this is the db that contains current values from the context providers.
            MongoDatabase db = ConnectionPool.getInstance().getMongoClient().getDatabase("coaas");
            MongoCollection<Document> entityCollection = db.getCollection(collectionName);
            // ToDo add limit and sort function
            // entityCollection.find(finalQuery).limit(10).forEach(printBlock);
            // The BSON document is used as the condition to retrieve from the mongo db. MongoDB collection is written, updated by the context streams.
            // So, running the context request (sub-query) is very straight forward. It's basically a query run on mongo db.
            // This is exactly the place where the first lookup of the cache memory or later the retrieval from the cache if item exist in lookup occur.
            entityCollection.find(finalQuery).forEach(printBlock);
        }

        // Generating a SQEM message as response
        return SQEMResponse.newBuilder().setStatus("200").setBody(printBlock.getResultString()).setMeta(printBlock.getMeta()).build();
    }

    // Retrieves historical context from providers stored in mongo db
    private static void queryHistoricalData(String collectionName, Bson finalQuery, Set<String> conditionContextAttributes, Long startDate, Long endDate, MongoBlock printBlock, int page, int limit) {
        MongoClient mongoClient = ConnectionPool.getInstance().getMongoClient();
        // The other mongo db database is the 'historical_db'. This lets say is the db that stores context from 'coaas' db, once they are expired.
        MongoDatabase db = mongoClient.getDatabase("historical_db");
        MongoCollection<Document> collection = db.getCollection(collectionName);

        List<Bson> query = new ArrayList<>();

        query.add(Aggregates.match(generateDateFilter(startDate, endDate, "coaas:day", false)));

        query.add(Aggregates.unwind("$coaas:samples"));

        query.add(Aggregates.match(generateDateFilter(startDate, endDate, "coaas:samples.coaas:time", true)));

        query.add(Aggregates.match(finalQuery));

        // query.add(Aggregates.group("$coaas:key", Accumulators.push("samples","$coaas:samples")));

        Document project = new Document();

        project.put("_id",0);
        project.put("time","$coaas:samples.coaas:time");
        project.put("value","$coaas:samples.coaas:value");

        List<Bson> countQuery = new ArrayList<>(query);

        countQuery.add(Aggregates.count("total"));

        Long total = Long.valueOf(collection.aggregate(countQuery).cursor().next().get("total").toString());

        query.add(Aggregates.project(project));

        Document sort = new Document();
        sort.put("time",-1);
        query.add(Aggregates.sort(sort));

        if(page>-1){
            query.add(Aggregates.skip(limit * page));
            query.add(Aggregates.limit(limit));
            printBlock.setLimit(limit);
            printBlock.setPage(page);
        }

        printBlock.setHistorical(true);
        printBlock.setTotalCount(total);
        collection.aggregate(query).allowDiskUse(true).forEach(printBlock);

        System.out.println(queryToString(query));
    }

    private static String queryToString(List<Bson> query) {
        String result = "[\n";
        for (Bson item : query) {
            result += item.toBsonDocument(org.bson.BsonDocument.class, MongoClient.getDefaultCodecRegistry()).toJson() + ",\n";
        }
        result += "]";
        return result;
    }

    private static Bson generateDateFilter(Long startDate, Long endDate, String attributeName, boolean isExact) {
        Bson dateFilterMatch = null;
        if (startDate != 0 && endDate != 0) {
            dateFilterMatch = Filters.and(Filters.gte(attributeName, getDateFromLong(startDate, isExact)), Filters.lte(attributeName, getDateFromLong(endDate, isExact)));
        } else {
            if (startDate != 0) {
                dateFilterMatch = Filters.gte(attributeName, getDateFromLong(startDate, isExact));
            } else if (endDate != 0) {
                dateFilterMatch = Filters.lte(attributeName, getDateFromLong(endDate, isExact));
            }
        }
        return dateFilterMatch;
    }

    // Converts time epochs to DateTime format
    private static Object getDateFromLong(Long dateLong, boolean isExact) {
        if (isExact) {
            return dateLong;
        }
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date(dateLong));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    private static SQEMResponse createErrorResponse(String errorMessage) {
        return SQEMResponse.newBuilder().setBody(errorMessage).setStatus("500").build();
    }

}
