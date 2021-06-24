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
import java.util.Scanner;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

/**
 *
 * @author DELL
 */
public class CSVMapper {

    final static int numberOfColumnsInCSV = 24;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        // TODO code application logic here
        //au.coaas.wrapper.CSVMapper.Post_JSON();
        //String getJsons = au.coaas.wrapper.CSVMapper.getJson();
        readCSV();
    }

    public static void postJsonEntity(String jsonInputString) throws MalformedURLException, IOException {

        URL url = new URL("http://localhost:8070/CASM-2.0.1/api/entity/update");
        //URL url = new URL("https://reqres.in/api/users");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.addRequestProperty("User-Agent", "Mozilla");
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
        con.setRequestProperty("Accept", "text/plain");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }

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

    public static void readCSV() throws FileNotFoundException, IOException {
        //parsing a CSV file into Scanner class constructor  

        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Ali\\Downloads\\cyclistData.csv"));

        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }

            
            //if it is not the first line of csv
            if (!line.contains("light_id")) {
                String[] attributes = line.split(",");
                String json = buildJsonInputString(attributes);
                postJsonEntity (json);
                
            }

            

        }

    }

    public static String buildJsonInputString(String[] csvLine) throws IOException {

        
            
            return "{\"EntityType\":{\"namespace\":\"fiware-scheme.org\",\"type\":\"cyclistPoint\"},\"Attributes\":{"
                    + "\"lightID\":" + csvLine[0] + ","
                    + "\"UTCTime\":" + csvLine[1] + ","
                    + "\"lng\":" + csvLine[2] + ","
                    + "\"lat\":" + csvLine[3] + ","
                    + "\"speed\":" + csvLine[4] + ","
                    + "\"x\":" + csvLine[5] + ","
                    + "\"y\":" + csvLine[6] + ","
                    + "\"z\":" + csvLine[7] + ","
                    + "\"localTime\":" + csvLine[8] + ","
                    + "\"lightProjectID\":" + csvLine[9] + ","
                    + "\"os\":" + csvLine[10] + ","
                    + "\"userID\":" + csvLine[11] + ","
                    + "\"userProjectID\":" + csvLine[12] + ","
                    + "\"userEmail\":" + csvLine[13] + ","
                    + "\"macAddress\":" + csvLine[14] + ","
                    + "\"age\":" + csvLine[15] + ","
                    + "\"gender\":" + csvLine[16] + ","
                    + "\"bikeMake\":" + csvLine[17] + ","
                    + "\"bikeModel\":" + csvLine[18] + ","
                    + "\"isBikeElectric\":" + csvLine[19] + ","
                    + "\"year\":" + csvLine[20] + ","
                    + "\"month\":" + csvLine[21] + ","
                    + "\"day\":" + csvLine[22] + ","
                    + "\"hour\":" + csvLine[23] + "},"                    
                    
                    + "\"key\":[\"lightID\"]}";
  
    }
}
