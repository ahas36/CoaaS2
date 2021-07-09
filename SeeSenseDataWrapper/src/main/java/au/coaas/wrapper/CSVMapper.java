package au.coaas.wrapper;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author DELL
 */
public class CSVMapper {

    final static int numberOfColumnsInCSV = 24;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ParseException {

        // TODO code application logic here
        //au.coaas.wrapper.CSVMapper.Post_JSON();
        //String getJsons = au.coaas.wrapper.CSVMapper.getJson();
        readCSV();
    }

    public static void postJsonEntity(String jsonInputString) throws MalformedURLException, IOException {

        String baseUrl = "https://sit-coaas-dev.deakin.edu.au/";
//        String baseUrl = "http://localhost:8070/";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, jsonInputString);
        Request request = new Request.Builder()
                .url(baseUrl+"CASM-2.0.1/api/entity/update")
                .method("POST", body)
                .addHeader("Content-Type", "text/plain")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());


    }

    public static String getJson() throws MalformedURLException, IOException {

        String urlString = "http://localhost:8070/CASM-2.0.1/api/entity/all";
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(15000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);
        urlConnection.setDoInput(true);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    public static void readCSV() throws FileNotFoundException, IOException, ParseException {
        //parsing a CSV file into Scanner class constructor  

        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Ali\\Desktop\\extended datapoint.csv"));

        reader.readLine();
        int bucketSize = 1000;
        int counter = 0;
        JSONArray ja = new JSONArray();
        while (true) {

            String line = reader.readLine();
            if (line == null) {
                if (counter != 0) {
                    postJsonEntity(ja.toString());
                }
                break;
            }

            //if it is not the first line of csv
            String[] attributes = line.split(",");
            ja.put(buildJsonInputString(attributes));
            counter++;
            if (counter == bucketSize) {
                postJsonEntity(ja.toString());
                ja = new JSONArray();
                counter = 0;
            }
        }

    }

    final static String regexDouble = "-?\\d+(\\.\\d+)";
    final static Pattern patternDouble = Pattern.compile(regexDouble, Pattern.MULTILINE);

    final static String regexLong = "-?\\d+";
    final static Pattern patternLong = Pattern.compile(regexLong, Pattern.MULTILINE);

    public static Object parseValue(String value) {
        if (value == null) {
            return null;
        }

        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        try {
            value = value.trim().toLowerCase();

            if (value.equals("false") || value.equals("true")) {
                return Boolean.valueOf(value);
            }

            final Matcher matcherDouble = patternDouble.matcher(value);

            if (matcherDouble.matches()) {
                return Double.valueOf(value);
            }

            final Matcher matcherLong = patternLong.matcher(value);

            if (matcherLong.matches()) {
                return Long.valueOf(value);
            }


        } catch (Exception e) {

        }

        return value;
    }

    private static SimpleDateFormat localSDF = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
    {
        localSDF.setTimeZone(TimeZone.getTimeZone("Australia/Melbourne"));
    }

    private static SimpleDateFormat utcSDF = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
    static {
        utcSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static JSONObject buildJsonInputString(String[] csvLine) throws IOException, ParseException {

        JSONObject result = new JSONObject();

        JSONObject et = new JSONObject();
        et.put("namespace", "fiware-scheme.org");
        et.put("type", "cyclistPoint");
        result.put("EntityType", et);


        JSONObject attributes = new JSONObject();
        attributes.put("lightID", parseValue(csvLine[0]));

        Date d = localSDF.parse(csvLine[8]);
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Australia/Melbourne"));
        c.setTime(d);

        attributes.put("utc_time", utcSDF.format(d));

        JSONObject geo = new JSONObject();
        JSONArray coords = new JSONArray();
        coords.put(parseValue(csvLine[2]));
        coords.put(parseValue(csvLine[3]));
        geo.put("coordinates", coords);
        geo.put("type", "Point");
        attributes.put("geo", geo);



        attributes.put("lat", parseValue(csvLine[3]));
        attributes.put("lng", parseValue(csvLine[2]));
        attributes.put("speed", parseValue(csvLine[4]));
        attributes.put("x", parseValue(csvLine[5]));
        attributes.put("y", parseValue(csvLine[6]));
        attributes.put("z", parseValue(csvLine[7]));
        attributes.put("localTime", parseValue(csvLine[8]));
        attributes.put("lightProjectID", parseValue(csvLine[9]));
        attributes.put("os", parseValue(csvLine[10]));
        attributes.put("userID", parseValue(csvLine[11]));
        attributes.put("lightProjectID", parseValue(csvLine[12]));
        attributes.put("userEmail", parseValue(csvLine[13]));
        attributes.put("macAddress", parseValue(csvLine[14]));
        attributes.put("age", parseValue(csvLine[15]));
        attributes.put("gender", parseValue(csvLine[16]));
        attributes.put("bikeMake", parseValue(csvLine[17]));
        attributes.put("bikeModel", parseValue(csvLine[18]));
        attributes.put("isBikeElectric", parseValue(csvLine[19]));
        attributes.put("year", c.get(Calendar.YEAR));
        attributes.put("month", c.get(Calendar.MONTH));
        attributes.put("day", c.get(Calendar.DAY_OF_MONTH));
        attributes.put("hour", c.get(Calendar.HOUR_OF_DAY));
        attributes.put("journey_id", parseValue(csvLine[24]));

        result.put("Attributes", attributes);

        JSONArray keys = new JSONArray();
        keys.put("lightID");
        result.put("key", keys);

        try {
            result.put("observedTime", localSDF.parse(attributes.getString("localTime")).getTime());
        }catch (Exception e){

        }
        return result;

    }
}
