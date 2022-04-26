package au.coaas.cqc.executor;

import au.coaas.base.proto.ListOfString;
import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqp.proto.*;
import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.CSIServiceGrpc;
import au.coaas.csi.proto.ContextService;
import au.coaas.csi.proto.ContextServiceInvokerRequest;
import au.coaas.grpc.client.CSIChannel;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.ContextRequest;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PushBasedExecutor {

    // This is the cache switch
    private static boolean cacheEnabled = true;

    private static Logger log = Logger.getLogger(PushBasedExecutor.class.getName());

    public static CdqlResponse executePushBaseQuery(CDQLQuery query, String queryId) {

        return null;
    }

}
