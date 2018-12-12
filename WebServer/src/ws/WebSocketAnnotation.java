package ws;


import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint(value = "/ws/{email}")
public class WebSocketAnnotation {
    String email;
    Session session;


    private static ConcurrentHashMap<String, Session> clients = new ConcurrentHashMap<String, Session>();

    @OnOpen
    public void start(@PathParam("email") String email, Session session) {

        try {

            // if client already has a session then we overwrite it
            // when client changes page we need to update the session
            clients.put(email, session);

            session.getBasicRemote().sendText("Notifications: ");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void sendNotification(String email, String notification) {

        Session s = clients.get(email);


        try {
            s.getBasicRemote().sendText(notification);
        } catch (IOException e) {
            System.out.println("IOExeption in WebSocketAnnotation");
        }


    }

    public static String getUsers() {
        return clients.toString();
    }

}
