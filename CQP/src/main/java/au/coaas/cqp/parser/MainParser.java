/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.cqp.parser;

import au.coaas.base.proto.ListOfString;
import au.coaas.cqp.exception.CDQLSyntaxtErrorException;
import au.coaas.cqp.proto.CDQLConstruct;
import au.coaas.cqp.proto.CDQLQuery;
import au.coaas.cqp.proto.CDQLType;
import au.coaas.cqp.proto.ContextEntity;
import au.coaas.cqp.util.AddError;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;

/**
 *
 * @author ali
 */
public class MainParser {

    public static CDQLConstruct parse(String stringQuery) throws CDQLSyntaxtErrorException {
        CDQLConstruct.Builder cdqlConstructBuilder = CDQLConstruct.newBuilder();
        StringBuilder res = new StringBuilder();
        ANTLRInputStream input = new ANTLRInputStream(stringQuery); // create a lexer that feeds off of input CharStream
        CdqlLexer lexer = new CdqlLexer(input); // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer); // create a au.coaas.cqe.parser that feeds off the tokens buffer
        CdqlParser parser = new CdqlParser(tokens);
        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(syntaxErrorListener);
        // This creates the parse tree
        ParseTree tree = parser.rule_Cdql(); // begin parsing at init rule  

        //if there is a syntax error
        if (parser.getNumberOfSyntaxErrors() != 0) {
            String error = "";
            for (SyntaxError errorItem : syntaxErrorListener.getSyntaxErrors()) {
                error = error + errorItem.getMessage() + "\n";
            }
            throw new CDQLSyntaxtErrorException(error);
        }
        CdqlVisitorImpl stringCDQLBaseVisitor = new CdqlVisitorImpl();
        // Walk the tree created during the parse, trigger callbacks
        res.append(tree.toStringTree(parser));
        stringCDQLBaseVisitor.visit(tree);
        if (stringCDQLBaseVisitor.getQuery() != null) {
            CDQLQuery.Builder query = stringCDQLBaseVisitor.getQuery();
            //if there is a undefined entity
            MainParser.validate(query);
            String error = "";
            if (query.getErrorsCount() != 0) {
                for (Map.Entry<String, ListOfString> entry : query.getErrorsMap().entrySet()) {
                    String key = entry.getKey();
                    List<String> value = entry.getValue().getValueList();
                    error += key + " : [\n";
                    for (String string : value) {
                        error += string + "\n";
                    }
                    error += "]\n";
                }
                throw new CDQLSyntaxtErrorException(error);
            }
            return cdqlConstructBuilder.setType(CDQLType.QUERY).setQuery(query.build()).build();
        }
        else if (stringCDQLBaseVisitor.getContextFunction() != null) {
            return cdqlConstructBuilder.setType(CDQLType.FUNCTION_DEF).setFunction(stringCDQLBaseVisitor.getContextFunction().build()).build();
        }
        return null;
    }

    private static void validate(CDQLQuery.Builder query) {
//        ExecutorService executorService = Executors.newFixedThreadPool(3);
//        // method reference introduced in Java 8
//        executorService.submit(() -> {
//            validateEntities(query);
//        });
////        executorService.submit(() -> {
////            validateSemanticVocabs(query);
////        });
//        executorService.submit(() -> {
//            computeExecutionPlan(query);
//        });
//        // close executorService
//        executorService.shutdown();
//
//        try {
//            executorService.awaitTermination(Long.MAX_VALUE, java.au.coaas.cqe.util.concurrent.TimeUnit.NANOSECONDS);
//        } catch (InterruptedException e) {
//
//        }
        MainParser.computeExecutionPlan(query);
    }

//    //check whether all the used entites are defined or not
//    private static void validateEntities(CDQLQuery.Builder query) {
//        for (Map.Entry<String, ListOfContextAttribute> entry : query.getSelect().getSelectAttrsMap().entrySet()) {
//            String key = entry.getKey();
//            List<ContextAttribute> value = entry.getValue().getValueList();
//            Optional<ContextEntity> findEntity = query.getDefine().getDefinedEntitiesList().stream().filter(ce -> ce.getEntityID().equals(key)).findAny();
//            if (!key.equals("this") && !findEntity.isPresent()) {
//                AddError.add(query, "Select", "Context Entity " + key + " is not defined");
//            } else {
//                for (ContextAttribute contextAttribute : value) {
//                    findEntity.get().toBuilder().putContextAttributes(contextAttribute.getAttributeName(), null);
//                }
//            }
//        }
//
//        for (ContextEntity definedEntity : query.getDefine().getDefinedEntitiesList()) {
//            if (definedEntity.getCondition() == null) {
//                continue;
//            }
//            for (Map.Entry<String, ListOfString> entry : definedEntity.getCondition().getDependencyMap().entrySet()) {
//                String key = entry.getKey();
//                List<String> value = entry.getValue().getValueList();
//                Optional<ContextEntity> findEntity = query.getDefine().getDefinedEntitiesList().stream().filter(ce -> ce.getEntityID().equals(key)).findAny();
//                if (!key.equals("this") && !findEntity.isPresent()) {
//                    AddError.add(query, "Select", "Context Entity " + key + " is not defined");
//                } else {
//                    for (String string : value) {
//                        findEntity.get().toBuilder().putContextAttributes(string, null);
//                    }
//                }
//            }
//
//        }
//    }


    private static void computeExecutionPlan(CDQLQuery.Builder query) {
        Map<Integer, ListOfString.Builder> executionPlan = new HashMap<>();
        List<ContextEntity> entities = new ArrayList<>(query.getDefine().getDefinedEntitiesList());
        Set<String> visited = new HashSet<>();
        int i = 1;
        executionPlan.put(0, ListOfString.newBuilder());
        for (ContextEntity entity : new ArrayList<>(entities)) {
            // Where is the dependency map defined?
            if (entity.getCondition() == null || entity.getCondition().getDependencyMap() == null || entity.getCondition().getDependencyMap().isEmpty()) {
                entities.remove(entity);
                // The entity ID is added here to execution plan, which is an indication to say what entities should be accessed from SQEM.
                // But where does this Execution plan gets executed?
                executionPlan.get(0).addValue(entity.getEntityID());
                visited.add(entity.getEntityID());
            }
        }
        boolean flag = true;
        while (flag && !entities.isEmpty()) {
            // What does this loop do by reiterating through the entities list and setting a flag?
            executionPlan.put(i, ListOfString.newBuilder());
            flag = false;
            Set<String> tempVisited = new HashSet<>();
            for (ContextEntity entity : new ArrayList<>(entities)) {
                boolean flag2 = true;
                for (Map.Entry<String, ListOfString> contextEntity : entity.getCondition().getDependencyMap().entrySet()) {
                    if (!visited.contains(contextEntity.getKey())) {
                        flag2 = false;
                    }
                }
                if (flag2) {
                    flag = true;
                    entities.remove(entity);
                    executionPlan.get(i).addValue(entity.getEntityID());
                    tempVisited.add(entity.getEntityID());
                }
            }
            visited.addAll(tempVisited);
            i++;
        }
        if (visited.size() != query.getDefine().getDefinedEntitiesCount()) {
            AddError.add(query, "execution plan",
                    "A possible loop detected, cannot resolve the dependency between the defined context entities");
        }
        Map<Integer,ListOfString> cdqlExecutionPlan = new HashMap<>();
        for (Map.Entry<Integer, ListOfString.Builder> entry : executionPlan.entrySet()) {
            Integer key = entry.getKey();
            ListOfString.Builder value = entry.getValue();
            cdqlExecutionPlan.put(key, value.build());
        }
        query.putAllExecutionPlan(cdqlExecutionPlan);
    }

}
