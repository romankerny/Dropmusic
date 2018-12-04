package ws;


import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

class User {

    User(Session s, String email) {
        this.s = s;
        this.email = email;
    }

    Session s;
    String email;
}

@ServerEndpoint(value = "/ws/{email}")
public class WebSocketAnnotation {
    private static final Set<User> users = new CopyOnWriteArraySet<>();


    @OnOpen
    public void start(@PathParam("email") String email, Session session) {

        System.out.println(email);
        try {
            // Code to run when users connects to WebSocket
            User u = new User(session, email);
            users.add(u);

            session.getBasicRemote().sendText(u.email);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
