package svm.jenna;

import au.coaas.svm.proto.SemanticVocabRegisterationResponse;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.hp.hpl.jena.query.*;

import okhttp3.*;
import org.apache.jena.atlas.web.auth.HttpAuthenticator;
import org.apache.jena.atlas.web.auth.SimpleAuthenticator;
import org.json.JSONArray;
import org.json.JSONObject;
import svm.conifg.Config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


public class JennaManager {
    private static Logger log = Logger.getLogger(JennaManager.class.getName());

    private static final HttpAuthenticator authenticator = new SimpleAuthenticator("admin", "admin".toCharArray());

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
                    Config.FUSEKI_URL + "query", String.format(query, rangeURL, parentClasses), authenticator);

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
                Config.FUSEKI_URL + "query", String.format(query, url, url, ontologyClass, url, ontologyClass), authenticator);
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
                Config.FUSEKI_URL + "query", String.format(query, url), authenticator);
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
                    Config.FUSEKI_URL + "query", query, authenticator);
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

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/sparql-update");
            RequestBody body = RequestBody.create(mediaType, updateQuery);
            Request request = new Request.Builder()
                    .url(Config.FUSEKI_URL+"update")
                    .post(body)
                    .addHeader("Content-Type", "application/sparql-update")
                    .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
                    .build();
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful())
            {
                responseBuilder.setCode(500);
                responseBuilder.setBody(response.body().string());
                return responseBuilder.build();
            }
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


    public static SemanticVocabRegisterationResponse register(File ontology, String URL) {
        SemanticVocabRegisterationResponse.Builder responseBuilder = SemanticVocabRegisterationResponse.newBuilder();
        try {
            String updateQuery = "";

            updateQuery += "CREATE GRAPH <" + URL + ">;";

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/sparql-update");
            RequestBody body = RequestBody.create(mediaType, updateQuery);
            Request request = new Request.Builder()
                    .url(Config.FUSEKI_URL+"update")
                    .post(body)
                    .addHeader("Content-Type", "application/sparql-update")
                    .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
                    .build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful())
            {
                client = new OkHttpClient();

                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file", ontology.getName(),
                                RequestBody.create(MediaType.parse("text/turtle"), ontology))
                        .build();

                request = new Request.Builder()
                        .url(Config.FUSEKI_URL+"data?graph="+URL)
                        .post(requestBody)
                        .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
                        .build();
                Response response2 = client.newCall(request).execute();
                if (!response2.isSuccessful()){
                    log.info(response2.body().string());
                    responseBuilder.setCode(500);
                    responseBuilder.setBody(response2.body().string());
                    return responseBuilder.build();
                }
            }else {
                log.info(response.body().string());
                responseBuilder.setCode(500);
                responseBuilder.setBody(response.body().string());
                return responseBuilder.build();
            }
        } catch (Exception e) {
            e.printStackTrace();
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


    private static String readLineByLineJava8(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    public static void init(){


        try {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Config.FUSEKI_CONFG_URL+"datasets/vocab")
                .get()
                .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
                .build();


            Response response = client.newCall(request).execute();

            RequestBody body = RequestBody.create(null, new byte[]{});

            if(!response.isSuccessful()){
                Request createRequest = new Request.Builder()
                        .url(Config.FUSEKI_CONFG_URL+"datasets?dbType=tdb&dbName=vocab")
                        .post(body)
                        .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
                        .build();

                client.newCall(createRequest).execute();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }




        List<String> graphs = getGraphs();
        String schemaOrgPath = "/app/lib/ont/schmeaorg.ttl";
//        String schemaOrgPath = "/Users/ali/CoaaSv2/CoaaS/SemanticVocabManager/src/main/resources/schmeaorg.ttl";
        if(!graphs.contains("<http://schema.org>"))
        {
            JennaManager.register(new File(schemaOrgPath),"http://schema.org");
        }
        if(!graphs.contains("<http://schema.mobivoc.org>"))
        {

        }

    }
}
