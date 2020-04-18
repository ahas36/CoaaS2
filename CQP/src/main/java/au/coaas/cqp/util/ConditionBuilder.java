package au.coaas.cqp.util;


import au.coaas.cqp.parser.CdqlParser;
import au.coaas.cqp.proto.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by ali on 14/02/2017.
 */
public class ConditionBuilder {

    public static Condition buildCondition(ParseTree infix, String entityId) throws UnsupportedEncodingException {
        Condition.Builder condition = Condition.newBuilder();
        ConditionBuilder.parseCondition(infix, condition);
        condition.removeDependency(entityId);
        return condition.build();
    }

    private static final Map<String, Integer> ops = new HashMap<String, Integer>() {
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

    private static final Map<String, Integer> operationalOps = new HashMap<String, Integer>() {
        {
            put("=", 0);
            put("<", 0);
            put("<=", 0);
            put(">", 0);
            put(">=", 0);
            put("in", 0);
            put("containsAny", 0);
            put("containsAll", 0);
        }
    };

    public static void parseCondition(ParseTree infix, Condition.Builder condition) throws UnsupportedEncodingException {
        Queue<CdqlConditionToken> queue = new LinkedList<>();
        Stack<CdqlConditionToken> operators = new Stack<>();
        ConditionBuilder.traversTree(infix, queue, operators, condition);
        while (!operators.isEmpty()) {
            queue.add(operators.pop());
        }
        condition.addAllRPNCondition(queue);
    }

    public ConditionBuilder() {

    }

    private static void traversTree(ParseTree tree, Queue<CdqlConditionToken> queue, Stack<CdqlConditionToken> stack, Condition.Builder condition) throws UnsupportedEncodingException {

        if (tree.getChildCount() == 0) {
            String token = tree.getText();
            //        System.out.println(token);
            if (token.equals("(")) {
                stack.push(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Operator).setStringValue(token).build());
            } else if (token.equals(")")) {
                while (!stack.peek().getStringValue().equals("(")) {
                    queue.add(stack.pop());
                }
                stack.pop();
            } else {
                while (!stack.isEmpty() && isHigerPrec(token, stack.peek().getStringValue())) {
                    queue.add(stack.pop());
                }
                stack.push(CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Operator).setStringValue(token).build());
            }
            return;
        } else if (tree.getClass().equals(CdqlParser.Rule_OperandContext.class)) {
            CdqlConditionToken addDependentEntities = addDependentEntities(tree.getChild(0), condition);
            queue.add(addDependentEntities);
            if (tree.getChild(0) instanceof CdqlParser.Rule_AttributeContext) {
                condition.addContextAttribiutes(tree.getText());
            }
            return;
        }
        for (int i = 0; i < tree.getChildCount(); i++) {
            traversTree(tree.getChild(i), queue, stack, condition);
        }
    }

    private static CdqlConditionToken addDependentEntities(ParseTree op, Condition.Builder condition) throws UnsupportedEncodingException {
        CdqlConditionToken token;
        if (op instanceof CdqlParser.Rule_AttributeContext || op instanceof CdqlParser.Rule_EntityTitleContext) {
            String text = op.getText();
            int indexOf = text.indexOf(".");
            String attribute = null;
            String dependentEntity = null;
            if (indexOf == -1) {
                dependentEntity = text;
                token = CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Entity).setStringValue(text).build();
            } else {
                dependentEntity = text.substring(0, indexOf);
                attribute = text.substring(indexOf + 1);
                token = CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Attribute).setStringValue(text).setContextAttribute(
                        ContextAttribute.newBuilder().setAttributeName(attribute).setEntityName(dependentEntity).build()).build();
            }
            if (condition.containsDependency(dependentEntity) && attribute != null) {
                ListOfString los = condition.getDependencyMap().get(dependentEntity);
                los.toBuilder().addValue(attribute);
                condition.putDependency(dependentEntity,los);
            } else {
                List<String> attrs = new ArrayList<>();
                if (attribute != null) {
                    attrs.add(attribute);
                }
                ListOfString.Builder los = ListOfString.newBuilder().addAllValue(attrs);
                condition.putDependency(dependentEntity,los.build());
            }
        } else if (op instanceof CdqlParser.Rule_FunctionCallContext) {
            token = CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Function).setStringValue(op.getText()).setFunctionCall(
                        FunctionCallParser.visitFunctionCall((CdqlParser.Rule_FunctionCallContext) op).build()).build();
            for (int i = 0; i < op.getChildCount(); i++) {
                if (op.getChild(i) instanceof CdqlParser.Rule_call_OperandContext) {
                    addDependentEntities(op.getChild(i).getChild(0).getChild(0), condition);
                }
            }
        } else {
            String operandText = op.getText();
            CdqlConstantConditionTokenType type;
            if (op.getChild(0) instanceof CdqlParser.JsonContext) {
                type = CdqlConstantConditionTokenType.Json;
            } else {
                try {
                    Double.valueOf(operandText);
                    type = CdqlConstantConditionTokenType.Numeric;
                } catch (Exception e) {
                    type = CdqlConstantConditionTokenType.String;
                }
            }
            token = CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Constant).setStringValue(operandText).setConstantTokenType(type).build();
                    
        }
        return token;
    }

    private static boolean isHigerPrec(String op, String sub) {
        return (ops.containsKey(sub) && ops.get(sub) < ops.get(op));
    }

}
