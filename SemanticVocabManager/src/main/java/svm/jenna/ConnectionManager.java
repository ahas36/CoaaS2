package svm.jenna;

import au.coaas.svm.proto.ListOfString;
import au.coaas.svm.proto.SemanticVocabRegisterationResponse;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import org.apache.jena.atlas.web.auth.HttpAuthenticator;
import org.apache.jena.atlas.web.auth.SimpleAuthenticator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionManager {
    private static Logger log = Logger.getLogger(ConnectionManager.class.getName());

    private static final HttpAuthenticator authenticator = new SimpleAuthenticator("admin", "admin".toCharArray());
    private static final String FUSEKI_URL = "http://fuseki:3030/test/";

    public static void main(String[] args) throws IOException {
        JSONObject jsonClass = properties.get("http://schema.mobivoc.org/ParkingSpace");//getClassAsJson("http://schema.org", "Car");
        log.info(jsonClass.toString());
    }

    private static LoadingCache<String, JSONObject> properties = Caffeine.newBuilder()
            .maximumSize(10000)
            .build(key -> getProperty(key));


    public static JSONObject getProperty(String propertyName) {
        JSONObject jsonClass = new JSONObject();
        try {
            String rangeURL = propertyName.substring(0, propertyName.lastIndexOf('/'));
            String ontologyClass = propertyName.substring(propertyName.lastIndexOf('/') + 1);

            String query = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "SELECT Distinct ?subject ?label ?description ?t ?range\n" +
                    "from <%s>\n" +
                    "WHERE {\n" +
                    "  ?subject ?v ?o\n" +
                    "  OPTIONAL { ?subject rdfs:label ?label}\n" +
                    "  OPTIONAL { ?subject rdfs:comment ?description}\n" +
                    "  OPTIONAL { ?subject a ?t}\n" +
                    "  OPTIONAL { ?subject ?r ?range}\n" +
                    "  FILTER (?o IN (%s))\n" +
                    "  FILTER (?v = <http://schema.org/domainIncludes>|| ?v=rdfs:domain)\n" +
                    "  FILTER (?r = <http://schema.org/rangeIncludes>|| ?r=rdfs:range)\n" +
                    "}\n";

            String parentClasses = String.join(",", getParentClasses(rangeURL, ontologyClass));

            QueryExecution qe = QueryExecutionFactory.sparqlService(
                    FUSEKI_URL + "query", String.format(query, rangeURL, parentClasses), authenticator);

            ResultSet results = qe.execSelect();
            while (results.hasNext()) {
                QuerySolution next = results.next();
                String range = next.get("?range").toString().trim();
                String subject = next.get("?subject").toString();
                log.info(propertyName + " is looking for a range from type " + range + " for subject " + subject);
                JSONObject property;

                if (jsonClass.has(subject)) {
                    property = jsonClass.getJSONObject(subject);
                    if (property.get("range") instanceof JSONArray) {
                        boolean containes = false;
                        for (int i = 0; i < property.getJSONArray("range").length(); i++) {
                            if (property.getJSONArray("range").getString(i).equals(range)) {
                                containes = true;
                                break;
                            }
                        }
                        if (!containes)
                            property.getJSONArray("range").put(range);
                    } else {
                        if (!property.getString("range").equals(range)) {
                            JSONArray tempArray = new JSONArray();
                            tempArray.put(property.get("range"));
                            tempArray.put(range);
                            property.put("range", tempArray);
                        }

                    }
                } else {
                    property = new JSONObject();
                    property.put("subject", subject);
                    property.put("range", range);
                    if (next.contains("?label") && next.get("?label") != null) {
                        property.put("label", next.get("?label").toString().trim());
                    }
                    if (next.contains("?comment") && next.get("?comment") != null) {
                        property.put("comment", next.get("?comment").toString().trim());
                    }
                    jsonClass.put(subject, property);
                }
            }

            qe.close();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        log.info("property " + propertyName + " has been registered");
        return jsonClass;
    }


    public static JSONObject getTerms(String url, String ontologyClass) {
        return properties.get(url + "/" + ontologyClass);
    }

    public static List<String> getParentClasses(String url, String ontologyClass) {
        List<String> result = new ArrayList<>();
        String query = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "SELECT DISTINCT ?class\n" +
                "from <%s>\n" +
                "WHERE {\n" +
                "?s rdfs:subClassOf* ?class.\n" +
                "  \tFilter(?s = <%s/#%s> || ?s=<%s/%s>)" +
                "}\n";
        log.info(String.format(query, url, url, ontologyClass, url, ontologyClass));
        QueryExecution qe = QueryExecutionFactory.sparqlService(
                FUSEKI_URL + "query", String.format(query, url, url, ontologyClass, url, ontologyClass), authenticator);
        ResultSet results = qe.execSelect();
        while (results.hasNext()) {
            QuerySolution next = results.next();
            result.add("<" + next.get("?class").toString() + ">");
        }
        while (results.hasNext()) {
            QuerySolution next = results.next();
            result.add("<" + next.get("?class").toString() + ">");
        }
        qe.close();
        return result;
    }

    public static List<String> getClasses(String url) {
        List<String> result = new ArrayList<>();
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "prefix owl: <http://www.w3.org/2002/07/owl#>\n" +
                "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "SELECT DISTINCT ?class ?label ?description\n" +
                "from <%s>\n" +
                "WHERE {\n" +
                "  ?class a ?p .\n" +
                "  OPTIONAL { ?class rdfs:label ?label}\n" +
                "  OPTIONAL { ?class rdfs:comment ?description}\n" +
                "  FILTER (?p = owl:Class|| ?p = rdfs:Class  )\n" +
                "}";
        log.info(String.format(query, url));
        QueryExecution qe = QueryExecutionFactory.sparqlService(
                FUSEKI_URL + "query", String.format(query, url), authenticator);
        ResultSet results = qe.execSelect();
        while (results.hasNext()) {
            QuerySolution next = results.next();
            result.add("<" + next.get("?class").toString() + ">");
        }
        qe.close();
        return result;
    }

    public static List<String> getGraphs() {
        try {
            List<String> result = new ArrayList<>();
            String query = "select distinct ?g  {graph ?g {?s ?p ?o}} ";
            QueryExecution qe = QueryExecutionFactory.sparqlService(
                    FUSEKI_URL + "query", query, authenticator);
            ResultSet results = qe.execSelect();
            while (results.hasNext()) {
                QuerySolution next = results.next();
                log.info(next.get("?g").toString());
                result.add("<" + next.get("?g").toString() + ">");
            }
            qe.close();
            return result;
        } catch (Exception e) {
            log.info(e.getMessage());
            log.info(e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static SemanticVocabRegisterationResponse register(String ontology, String URL) {
        SemanticVocabRegisterationResponse.Builder responseBuilder = SemanticVocabRegisterationResponse.newBuilder();
        try {
            String updateQuery = "";
            Pattern prefixRegex = Pattern.compile("@prefix[^>]*>\\s*\\.");
            Matcher matcher = prefixRegex.matcher(ontology);

            while (matcher.find()) {

                updateQuery += matcher.group().replace("@prefix", "prefix").substring(0, matcher.group().lastIndexOf('.') - 1) + "\n";
                ontology = ontology.replace(matcher.group(), "");
            }
            updateQuery += "CREATE GRAPH <" + URL + ">;\nINSERT DATA { \n" +
                    "    GRAPH <" + URL + "> { " + ontology.trim() + "\t}\n" +
                    "};";

            UpdateProcessor upp = UpdateExecutionFactory.createRemote(
                    UpdateFactory.create(updateQuery),
                    FUSEKI_URL + "update", authenticator);
            upp.execute();
        } catch (Exception e) {
            responseBuilder.setCode(500);
            responseBuilder.setBody(e.getMessage());
            return responseBuilder.build();
        }
        responseBuilder.setCode(200);
        JSONObject body = new JSONObject();
        body.put("graph", "<\"+URL+\">");
        responseBuilder.setBody(body.toString());
        return responseBuilder.build();
    }
}
