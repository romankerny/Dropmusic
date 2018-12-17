package ws;


import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class is meant to handle notification flow in the webServer. It stores all the users and their sessions
 * in the ConcurrentHashMap.
 * Everytime a user changes jsp the notification.js script stabilises a new connection with this class, the HashMap is then
 * updated with the right session.
 * The server has and @PathParam {email} that allows imitate stablishment of the pair key-value in the HashMap.
 *
 */
@ServerEndpoint(value = "/ws/{email}")
public class WebSocketAnnotation {
    String email;
    Session session;


    private static ConcurrentHashMap<String, Session> clients = new ConcurrentHashMap<String, Session>();


    /**
     * OnOpen saves the user's session and email.
     * @param email
     * @param session
     */
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


    /**
     * This method is called in the WebServer to send notifications to the users.
     *
     * @param email
     * @param notification
     */
    public static void sendNotification(String email, String notification) {

        Session s = clients.get(email);

        try {
            s.getBasicRemote().sendText(notification);
        } catch (IOException | NullPointerException t) {
            System.out.println("User logged via Terminal, not able to send WebSocket notification");
        }

     }


    /**
     * Method used to Update ratings in the album.jsp
     * @param msg - [rating]#[albumName]#[newRating]#[reviewerEmail]#[reviewText]
     */

    public static void updateAlbumRating(String msg) {
        for (Session s : clients.values()) {
            try {
                s.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static String getUsers() {
        return clients.toString();
    }

}
