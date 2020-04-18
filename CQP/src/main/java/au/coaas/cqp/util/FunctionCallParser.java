/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.cqp.util;


import au.coaas.cqp.parser.CdqlLexer;
import au.coaas.cqp.parser.CdqlParser;
import au.coaas.cqp.proto.*;
import com.google.protobuf.ByteString;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 *
 * @author ali
 */
public class FunctionCallParser {

    public static FunctionCall.Builder visitFunctionCall(CdqlParser.Rule_FunctionCallContext childMain) throws UnsupportedEncodingException {
        String functionTitle = childMain.getChild(0).getText();

        FunctionCall.Builder fCall = FunctionCall.newBuilder();
        fCall.setFunctionName(functionTitle);
        int indexOfLastChild = childMain.getChildCount() - 1;
        ParseTree lastChild = childMain.getChild(indexOfLastChild);
        if (lastChild instanceof CdqlParser.Rule_function_call_method_chainingContext && !lastChild.getText().trim().isEmpty()) {
            fCall.addAllSubItems(Arrays.asList(lastChild.getText().substring(1).split("\\.")));
            indexOfLastChild -= 1;
        }

        for (int i = 2; i < indexOfLastChild; i += 2) {
            ParseTree child = childMain.getChild(i).getChild(0).getChild(0);
            if (child instanceof CdqlParser.Rule_AttributeContext) {
                String[] split = child.getText().split("\\.", 2);
                ContextAttribute.Builder contextAttribute =  ContextAttribute.newBuilder();
                contextAttribute.setEntityName(split[0]);
                contextAttribute.setAttributeName(split[1]);
                Operand.Builder operand = Operand.newBuilder();
                operand.setType(OperandType.CONTEXT_ATTRIBUTE);
                operand.setContextAttribute(contextAttribute.build());
                fCall.addArguments(operand);
            } else if (child instanceof CdqlParser.Rule_EntityTitleContext) {
                ContextEntity.Builder ce = ContextEntity.newBuilder();
                ce.setEntityID(child.getText());
                Operand.Builder operand = Operand.newBuilder();
                operand.setType(OperandType.CONTEXT_ENTITY);
                operand.setContextEntity(ce.build());
                fCall.addArguments(operand);
            } else if (child instanceof CdqlParser.Rule_ContextValueContext) {
                Object payload = child.getChild(0).getPayload();
                if (payload instanceof CdqlParser.JsonContext) {
                    Operand.Builder operand = Operand.newBuilder();
                    operand.setType(OperandType.CONTEXT_VALUE_JSON);
                    operand.setStringValue(child.getText());
                    fCall.addArguments(operand);
                } else if (payload instanceof CommonToken) {
                    switch (((CommonToken) payload).getType()) {
                        case CdqlLexer.NUMBER:
                        {
                            Operand.Builder operand = Operand.newBuilder();
                            operand.setType(OperandType.CONTEXT_VALUE_NUMBER);
                            operand.setStringValue(child.getText());
                            fCall.addArguments(operand);
                            break;
                        }
                        case CdqlLexer.STRING:
                        {
                            Operand.Builder operand = Operand.newBuilder();
                            operand.setType(OperandType.CONTEXT_VALUE_NUMBER);
                            operand.setStringValue(child.getText());
                            fCall.addArguments(operand);
                            break;
                        }
                    }
                }
            } else { //function call
                FunctionCall.Builder subFunctionCall =  visitFunctionCall((CdqlParser.Rule_FunctionCallContext) child);             
                Operand.Builder operand = Operand.newBuilder();
                operand.setType(OperandType.FUNCTION_CALL);
                operand.setFunctioncall(subFunctionCall.build());
                fCall.addArguments(operand);
            }
        }
        return fCall;
    }

}
