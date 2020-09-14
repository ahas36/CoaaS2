package svm.finder;

import au.coaas.svm.proto.SVMService;
import org.json.JSONObject;
import svm.jenna.JennaManager;
import svm.schema.JsonSchemaManager;

import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;

public class SemanticVocabularyManger {

    public static List<String> getOntology() {
        ArrayList<String> graphs = new ArrayList<>(JennaManager.getGraphs());
        graphs.addAll(JsonSchemaManager.getGraphs());
        return graphs;
    }

    public static List<String> getClasses(String graph) {
        ArrayList<String> graphs = new ArrayList<>(JennaManager.getGraphs());
        if (graphs.contains("<"+graph+">")) {
            return JennaManager.getClasses(graph);
        } else {
            return JsonSchemaManager.getClasses(graph);
        }
    }

    public static void init() {
        JennaManager.init();
//        JsonSchemaManager.init();
    }

    public static JSONObject getTerms(String graph, String ontologyClass) {
        ArrayList<String> graphs = new ArrayList<>(JennaManager.getGraphs());
        if (graphs.contains("<"+graph+">")) {
            JSONObject terms = JennaManager.getTerms(graph,ontologyClass);
            terms.put("svmType","rdf");
            return terms;
        } else {
            JSONObject terms = JsonSchemaManager.getTerms(graph, ontologyClass);
            terms.put("svmType","json");
            return terms;
        }
    }
}
