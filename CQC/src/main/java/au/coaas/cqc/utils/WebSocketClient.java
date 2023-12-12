package au.coaas.cqc.utils;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

/**
 * @author shakthi
 */
public class WebSocketClient {
    private WebSocketContainer container;
    private Session session;

    public WebSocketClient(URI endpointURI) {
        try {
            this.container = ContainerProvider.getWebSocketContainer();
            this.session = this.container.connectToServer(this.container, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) throws IOException {
        this.session.getAsyncRemote().sendText(message);
    }
}
