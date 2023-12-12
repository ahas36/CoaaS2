package au.coaas.websocket;

import org.json.JSONObject;

import javax.websocket.Session;
import javax.websocket.CloseReason;

import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author shakthi
 */
@ServerEndpoint("/contexts/{reciever}")
public class WebSocketServer {
    private static Map<String, Session> conApps = new ConcurrentHashMap<>();
    private static Logger log = Logger.getLogger(WebSocketServer.class.getName());

    @OnOpen
    public void onOpen(Session session, @PathParam("reciever") String reciever) {
        try {
            conApps.put(reciever, session);
            log.severe("New web socket subscription established!");
        } catch (Exception ex) {
            log.severe(ex.getMessage());
        }
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        // This is the message that the server gets and relayed back to the correct consumer.
        // This is although a quick hack for the demo.
        JSONObject json = new JSONObject(message);
        String uri = json.getString("reciever");
        String[] split = uri.split("/");
        Session session = conApps.get(split[split.length - 1]);
        if(session != null && session.isOpen()) {
            session.getBasicRemote().sendText(json.getJSONObject("message").toString());
        }
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        conApps.remove(this);
        JSONObject closingMsg = new JSONObject();
        closingMsg.put("message", "Disconnecting.");
        broadcast(closingMsg.toString());
    }

    private static void broadcast(String message) {
        conApps.values().forEach(session -> {
            synchronized (session) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    log.severe(e.getMessage());
                }
            }
        });
    }
}
