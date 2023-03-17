package au.coaas.cqc.utils;

import au.coaas.cqc.utils.enums.HttpRequests;
import au.coaas.cqc.utils.enums.MeasuredProperty;
import au.coaas.cqc.utils.enums.RequestDataType;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.ConQEngLog;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import okhttp3.*;
import org.json.JSONObject;

import java.util.Map;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.AbstractMap;
import java.util.concurrent.TimeUnit;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class Utilities {
    public static Double unitConverter(MeasuredProperty prop, String originUnit, String targetUnit, double value) {
        switch(prop){
            case DISTANCE: {
                switch (originUnit.toLowerCase()) {
                    case "km":
                        switch (targetUnit.toLowerCase()) {
                            case "m":
                                return value*1000;
                            case "cm":
                                return value*1000*100;
                            case "mm":
                                return value*1000*100*10;
                        }

                    case "m":
                        switch (targetUnit.toLowerCase()) {
                            case "km":
                                return value/1000;
                            case "cm":
                                return value*100;
                            case "mm":
                                return value*100*10;
                        }
                    case "cm":
                        switch (targetUnit.toLowerCase()) {
                            case "km":
                                return value/100000;
                            case "m":
                                return value/100;
                            case "mm":
                                return value*10;
                        }
                    case "mm":
                        switch (targetUnit.toLowerCase()) {
                            case "mm":
                                return value;
                            case "km":
                                return value / 1000000;
                            case "cm":
                                return value / 10;
                            case "m":
                                return value / 1000;
                        }
                }
            }
            case TIME: {
                switch (originUnit.toLowerCase()) {
                    case "h":
                        switch (targetUnit.toLowerCase()) {
                            case "m":
                                return value*60;
                            case "s":
                                return value*3600;
                            case "ms":
                                return value*3600*1000;
                        }

                    case "m":
                        switch (targetUnit.toLowerCase()) {
                            case "h":
                                return value/60;
                            case "s":
                                return value*60;
                            case "ms":
                                return value*60*1000;
                        }
                    case "s":
                        switch (targetUnit.toLowerCase()) {
                            case "h":
                                return value/3600;
                            case "m":
                                return value/60;
                            case "ms":
                                return value*1000;
                        }
                    case "ms":
                        switch (targetUnit.toLowerCase()) {
                            case "h":
                                return value/(3600*1000);
                            case "m":
                                return value/(60*1000);
                            case "s":
                                return value/1000;
                        }
                }
            }
        }
        return value;
    }

    public static AbstractMap.SimpleEntry getDefaultUnitCode(String unitCode) {
        switch (unitCode) {
            case "H":
            case "M":
            case "s":
            case "ms":
                return new AbstractMap.SimpleEntry("s", MeasuredProperty.TIME);
            case "mm":
            case "km":
            case "cm":
            case "m":
                return new AbstractMap.SimpleEntry("m", MeasuredProperty.DISTANCE);
        }
        return null;
    }

    public static String httpCall(String serviceUrl, HttpRequests type, RequestDataType datatype,
                                  Map<String, String> headers, String body){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();

        // This is an extra step for context security.
        client = trustAllSslClient(client);

        HttpResponseFuture fu_res = new HttpResponseFuture();

        MediaType dataType = null;
        switch (datatype){
            case JSON: dataType = MediaType.parse("application/json; charset=utf-8"); break;
            case XML: dataType = MediaType.parse("application/xml"); break;
            case TEXT: dataType = MediaType.parse("text/plain"); break;
        }

        Request.Builder request = new Request.Builder()
                .url(serviceUrl);
        switch (type){
            case GET:
                request.get();
                break;
            case POST:
                RequestBody formBody = RequestBody.create(dataType, body);
                request.post(formBody);
                break;
        }

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                request.addHeader(key, value);
            }
        }

        try{
            Call call = client.newCall(request.build());
            call.enqueue(fu_res);
            Response response = fu_res.future.get();
            if(response.isSuccessful())
                return response.body().string().trim();
            else {
                SQEMServiceGrpc.SQEMServiceFutureStub future =
                        SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                future.logConQEngCR(ConQEngLog.newBuilder()
                        .setStatus(response.code()).setCr(body)
                        .setMessage(response.body().string().trim()).build());
            }
        }
        catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("message", e.getMessage());
            StringWriter strOut = new StringWriter();
            PrintWriter writer = new PrintWriter(strOut);
            e.printStackTrace(writer);
            writer.flush();
            error.put("stack trace", strOut.toString());
        }

        return null;
    }

    private static OkHttpClient trustAllSslClient(OkHttpClient client) {
        OkHttpClient.Builder builder = client.newBuilder();
        builder.sslSocketFactory(trustAllSslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        return builder.build();
    }

    private static final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };
    private static final SSLContext trustAllSslContext;

    static {
        try {
            trustAllSslContext = SSLContext.getInstance("SSL");
            trustAllSslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    private static final SSLSocketFactory trustAllSslSocketFactory = trustAllSslContext.getSocketFactory();

    public final static String serverConfig = "{\n"
            + "  \"type\": \"service_account\",\n"
            + "  \"project_id\": \"zippy-tiger-550\",\n"
            + "  \"private_key_id\": \"e6563fcdb1d25ce432ad2eea283c5bd9f51f2a80\",\n"
            + "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCbnKvt4L4mNCP7\\n9fNa7+oJ/YDmmErKXFBLq1FSMh9tIWm4t41zHWm0hxGZ5/4grnhCv3RkN6geN5Ky\\nHEcggs1PrECf7qycaoZxb52NaMTic99wRZGRmuozSJA+H6ZYgx7vbgOiP6erR9oz\\n/GAOgSIS1F/OT6s8o7KOVTxgdLYsoLTIYFx1FnntnwTDDuyq9OV9TlfWyKq8V7ru\\n0+Zf3HZ8eI9R3l8cigwxpaGcWRu08ICUmCtFO14WEcuKnRU0KguUMY+PpFDNnlal\\niJIf5vzcr4QgyFnwS8uiF4oxOZx8uDiJLJaWecpnIE7VITWM+D5cEq5S+kJ1MuQt\\nZKDY/hlRAgMBAAECggEAAL4HdW3pJkxBIDBxCd3jXUsV6HOxRqQYV3YQoB5gV0BN\\nrW2FuZb6NDvhNf1c8Vm3teTNKLW5wfQkqWAYaEOreE3zbHfiX6gj7kpYo24so0Jn\\nflT8n+fP5nD6DCvROrKcbQq2rnlUgs0H0c/qQuNzjsiv0M+BOvuybDqknDauUEnt\\njnyRxuAF/PQipuu5BJjQCPybRrcULT/8Ny5rqRovVabtkCsiv5xb2Ld807mJe4ef\\nsvgkh2WjykBBOz8jw3vIJGiTAEH/+4nB4JFbVgEeqwQZlOOEb+CZAlq0ycaxtZeU\\n4aj4d7UqLZ0NJi/Hdu/893ii0UXgcpLT64IwcrpWgQKBgQDT7no/zcDHjE70gV7/\\n3pJMAu396XT3mSAnEeSDmREK3stIke3TSy5pKHgqDSI1dQwwYaOt8ViNZd2RmKO+\\nKm5EtDmcJywWyILPmd5OFQGhLacKrkYfoafO3lKywIaOgklK35Ai4fHA/PqI96zw\\nfMt3wK2TUKPcCvu8lr2WI+zWQQKBgQC7+DJBjF+IboSxHnnTxAcv58jZaGD+Gi3h\\nuWhMi8Hz4xDmdAfztxDzHwN2xXlDV3MfF8E1ipzAMg+YoEtsbrmGO5t+CZT/VklZ\\n8jDYKt5BrPihlNIBe1fSodBk0AKZdsQc22Bdcx4wSbdrY7Sh0+ySpFGF8lAT9Ebs\\nWq7bR9IfEQKBgG79S8Syaq2Wy5q9ThVOaOup9R2u7/TjmrUbZ8OLnCNHPssIsbag\\nfvPPn+7V5f59CB8mY2QrKycSHBGuEML80bblc+5VjX95DxjOCmB2G95fXEX5svvJ\\n22o5gLHgphdoIB44KvI5xjQ9yK+Glmlz0dMrczvdVZNdpSzE/RDuS5CBAoGAb3QE\\nAH/GKXQ2c3r5i4oBJPOPUPGmCSTmrZ+s6y43C3BIlgyYpnaWs0FhYWxHsZlJplcE\\ndcXBmehU/Q+WiS3CDYiMcxglY5z7Ez0anyIT9Ocl6VD/kGddNIRDt3LrB6m3MNEW\\n+3IFpWYgPqm48fdhn7WUrRJ3Ts2ZvA0s/tJAEbECgYBC215CXhE8Gqe3tZg+2zQ2\\nse+c9nZdYvSZpjOIx5EOHOSuelU3P/e5KjteXhKC+fS/Ji/oFo2drnzQGuz3apOj\\nCbZjq47kTf/5zNs9K0m7mZJ3PuNPvrO3FkduCL+qHNn8mmUHEs8/jc3zyRG2a3ZQ\\nZ3jpPG4CZrS61euPkPdiEg==\\n-----END PRIVATE KEY-----\\n\",\n"
            + "  \"client_email\": \"firebase-adminsdk-mpgsf@zippy-tiger-550.iam.gserviceaccount.com\",\n"
            + "  \"client_id\": \"107037747516359830276\",\n"
            + "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n"
            + "  \"token_uri\": \"https://accounts.google.com/o/oauth2/token\",\n"
            + "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n"
            + "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-mpgsf%40zippy-tiger-550.iam.gserviceaccount.com\"\n"
            + "}";
}
