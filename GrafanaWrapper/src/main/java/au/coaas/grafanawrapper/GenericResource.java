/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.grafanawrapper;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author ali
 */
@Path("generic")
public class GenericResource {

    @Context
    private UriInfo context;

    private static  Map<String, JSONObject> timeseries = new HashMap<>();

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }

    @GET
    public Response test() {
        timeseries = new HashMap<>();
        return Response.ok().build();
    }
    
    

    @POST
    @Path("search")
    public Response search(String search) {
        JSONArray result = new JSONArray();

        result.put("pull");
        result.put("push");

        return Response.ok(result.toString()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("query")
    public Response query(String queryString) throws IOException {

        JSONObject query = new JSONObject(queryString);
        JSONArray targets = query.getJSONArray("targets");

        OkHttpClient client = new OkHttpClient();
        com.squareup.okhttp.MediaType mediaType = com.squareup.okhttp.MediaType.parse("text/plain");

        JSONArray result = new JSONArray();

        for (int i = 0; i < targets.length(); i++) {
            JSONObject targetObject = targets.getJSONObject(i);
            JSONObject data = targetObject.getJSONObject("data");

            RequestBody body = RequestBody.create(mediaType, data.getString("query"));
            Request request = new Request.Builder()
                    .url("http://localhost:8070/CASM-2.0.1/api/query")
                    .method("POST", body)
                    .addHeader("Content-Type", "text/plain")
                    .build();
            com.squareup.okhttp.Response response = client.newCall(request).execute();

            String[] headers = data.getString("attribute").split("\\.");
            String strResponse = response.body().string();

            if (targetObject.getString("type").equals("table") && data.getString("id").startsWith("geo")) {
                JSONObject queryResponse = new JSONObject(strResponse);
                JSONArray allData = queryResponse.getJSONObject(headers[0]).getJSONArray("results");
                Long timeStamp = System.currentTimeMillis();
                for (int j = 0; j < Math.min(100, allData.length()); j++) {
                    JSONObject dataItem = allData.getJSONObject(j);
                    JSONArray geo = dataItem.getJSONObject("geo").getJSONArray("coordinates");
                    JSONArray columns = new JSONArray();
                    JSONObject title = new JSONObject();
                    title.put("text", "time");
                    title.put("type", "long");
                    columns.put(title);

                    title = new JSONObject();
                    title.put("text", "latitude");
                    title.put("type", "double");
                    columns.put(title);

                    title = new JSONObject();
                    title.put("text", "longitude");
                    title.put("type", "double");
                    columns.put(title);

                    title = new JSONObject();
                    title.put("text", "metric");
                    title.put("type", "double");
                    columns.put(title);

                    JSONArray values = new JSONArray();
                    values.put(timeStamp);
                    values.put(geo.get(0));
                    values.put(geo.get(1));
                    values.put(75.5);
                    List<String> keySet = new ArrayList<>(dataItem.keySet());
                    Collections.sort(keySet);

                    for (String key : keySet) {
                        if (key.equals("geo") || key.equals("_id")) {
                            continue;
                        }
                        if (key.equals("additionalProperty")) {
                            title = new JSONObject();
                            title.put("text", "isOccupied");
                            title.put("type", "string");
                            columns.put(title);
                            values.put(dataItem.getJSONObject("additionalProperty").get("value"));
                        } else {
                            title = new JSONObject();
                            title.put("text", key);
                            title.put("type", "string");
                            columns.put(title);
                            values.put(dataItem.get(key).toString());
                        }
                    }

                    JSONObject item;

                    if (timeseries.containsKey(data.getString("id") + "_" + dataItem.getString("identifier"))) {
                        item = timeseries.get(data.getString("id") + "_" + dataItem.getString("identifier"));
                        item.getJSONArray("rows").put(values);
                    } else {
                        item = new JSONObject();
                        JSONArray ts = new JSONArray();
                        ts.put(values);
                        item.put("rows", ts);
                        item.put("columns", columns);
                        item.put("name", "Parking " + dataItem.getString("identifier"));

                        JSONObject tag = new JSONObject();

                        tag.put("latitude", geo.get(0));
                        tag.put("longitude", geo.get(1));
                        item.put("latitude", geo.get(0));
                        item.put("longitude", geo.get(1));
                        item.put("tags", tag);
                        item.put("LatLng", tag);
                        timeseries.put(data.getString("id") + "_" + dataItem.getString("identifier"), item);
                    }
                    result.put(item);
                }

//                JSONObject jo = new JSONObject();
//                jo.put("series", result);
                return Response.ok(result.toString()).build();
            } else {
                Object queryResponse = new JSONObject(strResponse);
                for (int j = 0; j < headers.length - 1; j++) {
                    String header = headers[j];
                    while (queryResponse instanceof JSONArray) {
                        queryResponse = ((JSONArray) queryResponse).get(0);
                    }
                    if (queryResponse instanceof JSONObject) {
                        queryResponse = ((JSONObject) queryResponse).get(header);
                    } else {
                        break;
                    }
                }
                double value = Double.valueOf(((JSONObject) queryResponse).get(headers[headers.length - 1]).toString());

                JSONArray ts = new JSONArray();
                ts.put(value);
                ts.put(System.currentTimeMillis());

                JSONObject item;

                if (!timeseries.containsKey(data.getString("id"))) {
                    item = new JSONObject();
                    item.put("target", targetObject.getString("target"));
                    JSONArray datapoints = new JSONArray();
                    datapoints.put(ts);
                    item.put("datapoints", datapoints);
                    timeseries.put(data.getString("id"), item);
                } else {
                    timeseries.get(data.getString("id")).getJSONArray("datapoints").put(ts);
                    item = timeseries.get(data.getString("id"));
                }
                result.put(item);
            }
        }
        return Response.ok(result.toString()).build();
    }

    @POST
    @Path("annotations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnnotations(String text) {
        JSONObject result = new JSONObject();
        JSONObject annotations = new JSONObject(text);
        result.put("annotation", annotations.getJSONObject("annotation"));
        result.put("time", 1000);
        result.put("title", "test");
        return Response.ok(result.toString()).build();
    }

    @POST
    @Path("tag-keys")
    public Response tagKeys(String annotations) {
        return Response.ok().build();
    }

    @POST
    @Path("tag-values")
    public Response tagValues(String annotations) {
        return Response.ok().build();
    }

}
