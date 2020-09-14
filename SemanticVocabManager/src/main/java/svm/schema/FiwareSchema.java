package svm.schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class FiwareSchema {
    private static Logger log = Logger.getLogger(FiwareSchema.class.getName());

    private static final String PATH = "https://raw.githubusercontent.com/smart-data-models/%s/master/%s/schema.json";

    private static final String JA_STRING = "{ \"udpatedDate\" : \"2020-04-22T17:17:52.368701\", \"officialList\" : ["
            + "{ \"repoName\": \"dataModel.User\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.User.git\", \"dataModels\" : [\"Activity\",\"UserContext\"]},\n"
            + "{ \"repoName\": \"dataModel.PointOfInteraction\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.PointOfInteraction.git\", \"dataModels\" : [\"SmartPointOfInteraction\",\"SmartSpot\"]},\n"
            + "{ \"repoName\": \"dataModel.KeyPerformanceIndicator\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.KeyPerformanceIndicator.git\", \"dataModels\" : [\"keyPerformanceIndicator\"]},\n"
            + "{ \"repoName\": \"dataModel.UrbanMobility\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.UrbanMobility.git\", \"dataModels\" : [\"ArrivalEstimation\",\"GtfsAccessPoint\",\"GtfsAgency\",\"GtfsCalendarDateRule\",\"GtfsCalendarRule\",\"GtfsFrequency\",\"GtfsRoute\",\"GtfsService\",\"GtfsShape\",\"GtfsStation\",\"GtfsStop\",\"GtfsStopTime\",\"GtfsTransferRule\",\"GtfsTrip\"]},\n"
            + "{ \"repoName\": \"dataModel.ParksAndGardens\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.ParksAndGardens.git\", \"dataModels\" : [\"FlowerBed\",\"Garden\",\"GreenspaceRecord\"]},\n"
            + "{ \"repoName\": \"dataModel.Aquaculture\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.Aquaculture.git\", \"dataModels\" : [\"BreedingOperation\",\"FishCointaiment\"]},\n" + "{ \"repoName\": \"dataModel.Agrifood\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.Agrifood.git\", \"dataModels\" : [\"AgriApp\",\"AgriCrop\",\"AgriFarm\",\"AgriGreenhouse\",\"AgriParcel\",\"AgriParcelOperation\",\"AgriParcelRecord\",\"AgriPest\",\"AgriProductType\",\"AgriSoil\",\"Animal\"]},\n"
            + "{ \"repoName\": \"dataModel.WaterNetworkManagement\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.WaterNetworkManagement.git\", \"dataModels\" : [\"Curve\",\"Junction\",\"Pattern\",\"Pipe\",\"Pump\",\"Reservoir\",\"Tank\",\"Valve\"]},\n"
            + "{ \"repoName\": \"dataModel.Parking\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.Parking.git\", \"dataModels\" : [\"OffStreetParking\",\"OnStreetParking\",\"ParkingAccess\",\"ParkingGroup\",\"ParkingSpot\"]},\n"
            + "{ \"repoName\": \"dataModel.Weather\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.Weather.git\", \"dataModels\" : [\"WeatherAlert\",\"WeatherForecast\",\"WeatherObserved\"]},\n"
            + "{ \"repoName\": \"dataModel.WasteManagement\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.WasteManagement.git\", \"dataModels\" : [\"WasteContainer\",\"WasteContainerIsle\",\"WasteContainerModel\"]},\n"
            + "{ \"repoName\": \"dataModel.Transportation\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.Transportation.git\", \"dataModels\" : [\"BikeHireDockingStation\",\"CrowdFlowObserved\",\"EVChargingStation\",\"Road\",\"RoadSegment\",\"TrafficFlowObserved\",\"Vehicle\",\"VehicleModel\"]},\n"
            + "{ \"repoName\": \"dataModel.PointOfInterest\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.PointOfInterest.git\", \"dataModels\" : [\"Beach\",\"Museum\",\"PointOfInterest\"]},\n"
            + "{ \"repoName\": \"dataModel.Device\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.Device.git\", \"dataModels\" : [\"Device\"]},\n"
            + "{ \"repoName\": \"dataModel.Streetlighting\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.Streetlighting.git\", \"dataModels\" : [\"Streetlight\",\"StreetlightControlCabinet\",\"StreetlightGroup\",\"StreetlightModel\"]},\n"
            + "{ \"repoName\": \"dataModel.IssueTracking\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.IssueTracking.git\", \"dataModels\" : [\"Open311_ServiceRequest\",\"Open311_ServiceType\"]},\n"
            + "{ \"repoName\": \"dataModel.Energy\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.Energy.git\", \"dataModels\" : [\"ThreePhaseAcMeasurement\"]},\n"
            + "{ \"repoName\": \"dataModel.Building\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.Building.git\", \"dataModels\" : [\"Building\",\"BuildingOperation\"]},\n"
            + "{ \"repoName\": \"dataModel.Environment\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.Environment.git\", \"dataModels\" : [\"AeroAllergenObserved\",\"AirQualityObserved\",\"NoiseLevelObserved\",\"WaterQualityObserved\"]},\n"
            + "{ \"repoName\": \"dataModel.Alert\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.Alert.git\", \"dataModels\" : [\"alert\"]},\n"
            + "{ \"repoName\": \"dataModel.WaterDistribution\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.WaterDistribution.git\", \"dataModels\" : []},\n"
            + "{ \"repoName\": \"dataModel.WaterQuality\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.WaterQuality.git\", \"dataModels\" : []},\n"
            + "{ \"repoName\": \"dataModel.WaterConsumption\",\"repoLink\": \"https://github.com/smart-data-models/dataModel.WaterConsumption.git\", \"dataModels\" : []}]}";

    private static Object readJson(String uri, String parent) throws IOException, Exception {

        System.out.println(uri);
        if (uri.startsWith("[")) {
            uri = uri.substring(1, uri.length() - 1);
        }
        if (uri.startsWith(".")) {
            String[] uriSplit = uri.split("/");
            List<String> path = new ArrayList<>();
            for (String string : uriSplit) {
                if (!string.startsWith(".")) {
                    path.add(string);
                }
            }

            uri = String.format("https://raw.githubusercontent.com/smart-data-models/%s/master/%s", parent, String.join("/", path));
        }

        String[] split = uri.split("#");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        Request request = new Request.Builder()
                .url(split[0])
                .method("GET", null)
                .build();

        Response response = client.newCall(request).execute();

        JSONObject result = new JSONObject(response.body().string());

        if (split.length > 1 && split[1].trim().length() > 0) {
            String[] values = split[1].split("/");
            if (split[0].contains("geojson.org")) {
                if (result.has("oneOf") && values.length == 1) {
                    JSONArray geoArray = result.getJSONArray("oneOf");
                    for (int i = 0; i < geoArray.length(); i++) {
                        JSONObject geoJson = geoArray.getJSONObject(i);
                        if (geoJson.getString("title").equalsIgnoreCase("GeoJSON " + values[0])) {
                            return geoJson;
                        }
                    }
                }
                throw new Exception("geojson but values are " + split[1] + " and result is " + result);
            } else {
                JSONObject tempRes = new JSONObject(result.toString());
                for (String value : values) {
                    if (value.trim().length() < 1) {
                        continue;
                    }
                    tempRes = tempRes.getJSONObject(value);
                }
                JSONArray resArr = new JSONArray();
                resArr.put(tempRes);
                resArr.put(result);
                return resArr;
            }

        }

        return result;
    }

    private static Object flattenSchema(Object input, JSONObject main, String repoName) throws IOException, Exception {

        if (input instanceof JSONObject) {
            JSONObject result = new JSONObject();
            JSONObject schema = (JSONObject) input;
            for (String key : schema.keySet()) {
                Object item = schema.get(key);
                if (item instanceof JSONArray) {
                    result.put(key, flattenSchema(item, main, repoName));
                } else if (item instanceof JSONObject) {
                    result.put(key, flattenSchema(item, main, repoName));
                } else {
                    if (key.equals("$ref")) {
                        try {
                            if (item.toString().trim().startsWith("#")) {
                                String[] values = item.toString().trim().substring(1).split("/");
                                JSONObject localJson = new JSONObject(main.toString());
                                for (int i = 0; i < values.length; i++) {
                                    if (values[i].trim().length() < 1) {
                                        continue;
                                    }
                                    localJson = localJson.getJSONObject(values[i]);
                                }
                                result.put(key, flattenSchema(localJson, main, repoName));
                            } else {
                                Object newItem = readJson((String) item, repoName);
                                if (newItem instanceof JSONArray) {
                                    JSONArray newJA = (JSONArray) newItem;
                                    result.put(key, flattenSchema(newJA.getJSONObject(0), newJA.getJSONObject(1), repoName));
                                } else {
                                    JSONObject newJO = (JSONObject) newItem;
                                    result.put(key, flattenSchema(newJO, newJO, repoName));
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(String.format("for repo with name %s there is a bug in key : %s for item : %s and the error message is %s", repoName, key, item.toString(), e.getMessage()));
                            result.put(key, item);
                        }
                    } else {
                        result.put(key, item);
                    }
                }
            }
            return result;
        } else if (input instanceof JSONArray) {
            JSONArray ja = (JSONArray) input;
            JSONArray result = new JSONArray();
            for (int i = 0; i < ja.length(); i++) {
                Object item = ja.get(i);
                if (item instanceof JSONArray || item instanceof JSONObject) {
                    result.put(flattenSchema(item, main, repoName));
                } else {
                    result.put(item);
                }
            }
            return result;
        }
        return null;
    }

    private static Object normalizeDocument(Object input) {

        if (input instanceof JSONObject) {
            JSONObject obj = (JSONObject) input;
            JSONObject result = new JSONObject();

            for (String key : obj.keySet()) {
                if (obj.get(key) instanceof JSONObject) {
                    JSONObject jo = obj.getJSONObject(key);
                    if (jo.has("$ref") && jo.get("$ref") instanceof JSONObject) {
                        jo = jo.getJSONObject("$ref");
                    }
                    result.put(key, normalizeDocument(jo));
                } else if (obj.get(key) instanceof JSONArray) {
                    result.put(key, normalizeDocument(obj.getJSONArray(key)));
                } else {
                    result.put(key, obj.get(key));
                }
            }

            return result;
        } else if (input instanceof JSONArray) {
            JSONArray ja = (JSONArray) input;
            JSONArray result = new JSONArray();
            for (int i = 0; i < ja.length(); i++) {
                Object item = ja.get(i);
                if (item instanceof JSONArray) {
                    result.put(normalizeDocument(item));
                } else if (item instanceof JSONObject) {
                    if (((JSONObject) item).has("$ref")) {
                        result.put(normalizeDocument(((JSONObject) item).get("$ref")));
                    } else {
                        result.put(normalizeDocument(item));
                    }
                } else {
                    result.put(item);
                }
            }
            return result;
        }
        return null;
    }

    private static Object mergeDocument(Object input) {

        if (input instanceof JSONObject) {
            JSONObject obj = (JSONObject) input;
            JSONObject result = new JSONObject();

            for (String key : obj.keySet()) {
                if (obj.get(key) instanceof JSONObject) {
                    result.put(key, mergeDocument(obj.getJSONObject(key)));
                } else if (obj.get(key) instanceof JSONArray) {
                    if (key.equals("allOf")) {
                        JSONArray ja = obj.getJSONArray(key);
                        for (int i = 0; i < ja.length(); i++) {
                            String sKeyT = "";
                            try {
                                JSONObject mergedDocument = ((JSONObject) mergeDocument(ja.get(i))).getJSONObject("properties");
                                if (result.has("properties")) {
                                    for (String sKey : mergedDocument.keySet()) {
                                        sKeyT = sKey;
                                        result.getJSONObject("properties").put(sKey, mergedDocument.get(sKey));
                                    }
                                } else {
                                    result.put("properties", mergedDocument);
                                }
                            } catch (Exception e) {
                                System.out.println("**************************** Error for : " + key + " : " + sKeyT + " ****************************");
                                e.printStackTrace();
                                System.out.println("************************************************************************************");
                            }
                        }
                    } else {
                        result.put(key, mergeDocument(obj.getJSONArray(key)));
                    }
                } else {
                    result.put(key, obj.get(key));
                }
            }

            return result;
        } else if (input instanceof JSONArray) {
            JSONArray ja = (JSONArray) input;
            JSONArray result = new JSONArray();
            for (int i = 0; i < ja.length(); i++) {
                Object item = ja.get(i);
                if (item instanceof JSONArray) {
                    result.put(mergeDocument(item));
                } else if (item instanceof JSONObject) {
                    result.put(mergeDocument(item));
                } else {
                    result.put(item);
                }
            }
            return result;
        }
        return null;

    }

    private static Object normalize2Document(Object input) {

        if (input instanceof JSONObject) {
            JSONObject obj = (JSONObject) input;
            JSONObject result = new JSONObject();

            for (String key : obj.keySet()) {
                if (obj.get(key) instanceof JSONObject) {
                    JSONObject jo = obj.getJSONObject(key);
                    if (jo.has("properties") && key.equals("properties")) {
                        jo = jo.getJSONObject("properties");
                    }
                    result.put(key, normalize2Document(jo));
                } else if (obj.get(key) instanceof JSONArray) {
                    result.put(key, normalizeDocument(obj.getJSONArray(key)));
                } else {
                    result.put(key, obj.get(key));
                }
            }

            return result;
        } else if (input instanceof JSONArray) {
            JSONArray ja = (JSONArray) input;
            JSONArray result = new JSONArray();
            for (int i = 0; i < ja.length(); i++) {
                Object item = ja.get(i);
                if (item instanceof JSONArray) {
                    result.put(normalize2Document(item));
                } else if (item instanceof JSONObject) {
                    result.put(normalize2Document(item));
                } else {
                    result.put(item);
                }
            }
            return result;
        }
        return null;
    }



    public static void register() {
        JSONArray ja = new JSONObject(JA_STRING).getJSONArray("officialList");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        for (int i = 0; i < ja.length(); i++) {
            try {
                JSONObject jo = ja.getJSONObject(i);

                JSONArray sja = jo.getJSONArray("dataModels");

                for (int j = 0; j < sja.length(); j++) {
                    Request request = new Request.Builder()
                            .url(String.format(PATH, jo.getString("repoName"), sja.getString(j)))
                            .method("GET", null)
                            .build();

                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {

                        JSONObject body = new JSONObject(response.body().string());

                        body.put("@key", sja.getString(j));

                        JSONObject preResult = ((JSONObject) normalizeDocument(flattenSchema(body, body, jo.getString("repoName"))));

                        JSONObject result = ((JSONObject) normalize2Document(mergeDocument(preResult)));

                        JsonSchemaManager.register("<http://schema.fiware.org>",result.toString(),sja.getString(j));
                    }

                }
            } catch (Exception e) {
            }
        }
    }
}
