package ws;


import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


@ServerEndpoint(value = "/ws/{email}")
public class WebSocketAnnotation {
    String email;
    Session session;


    private static final Set<WebSocketAnnotation> users = new CopyOnWriteArraySet<>();



    @OnOpen
    public void start(@PathParam("email") String email, Session session) {

        try {
            // Code to run when users connects to WebSocket
            this.session = session;
            this.email = email;
            users.add(this);


            session.getBasicRemote().sendText(email);
            System.out.println(users.size());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void sendNotification(String email, String notification) {

        for(WebSocketAnnotation e : users)
            if(e.email.equals(email)) {
                try {
                    e.session.getBasicRemote().sendText(notification);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

    }


}
