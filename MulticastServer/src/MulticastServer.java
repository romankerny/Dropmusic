import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * MulticastServer initializes the server and database. Creates a new Thread every time a new packet is received
 * This class contains an array of users and artist
 * Multicast's address is set to "224.3.2.2" and it's receiving port is 5213
 *
 */


public class MulticastServer {
    private static String MULTICAST_ADDRESS = "224.3.2.2";
    private static int RECV_PORT = 5213;
    private static MulticastSocket socket = null;
    private static CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<Artist> artists = new CopyOnWriteArrayList<>();

    /**
     *  Creates a multicastSocket and sends a Datagram packet with
     *  some protocol instruction.
     *
     * @param resp - protocol instruction to send
     */

    public static void sendResponseMulticast(String resp) {

            // only the designated Multicast Server will respond to RMIServer
            try {
                MulticastSocket socket = new MulticastSocket();
                byte[] buffer = resp.getBytes();
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 5214);
                socket.send(packet);

            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    /**
     *  Main loop of Multicast, starts by reading users.obj and artists.obj,
     *  then proceeds to create a new MulticastSocket, joins the group and sends an `ack` to RMIServer.
     *
     *  Finally enters the loop of receiving a packet and creating a new MulticastServerResponse Thread to deal with it.
     *
     */

    public static void main(String[] args) {
        // Read from memory
        users = ObjectFiles.readUsersFromDisk();
        artists = ObjectFiles.readArtistsFromDisk();
        /*
        for (User u : users)
            System.out.println(u);

        for (Artist a : artists)
            System.out.println(a);*/

        try {
            // Sending HASH-code to Main RMI
            String code = UUID.randomUUID().toString().substring(24);
            sendResponseMulticast("flag|r;type|ack;hash|"+code+";");

            socket = new MulticastSocket(RECV_PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            System.out.println("Multicast server ready - " + code);


            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/dropmusic?useSSL=false", "admin", "dropmusic");
            DB.con = con;


            while (true) {

                byte[] buffer = new byte[65536];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                MulticastServerResponse threadToResolvePacket = new MulticastServerResponse(packet, MULTICAST_ADDRESS, users, artists, code, con);
                threadToResolvePacket.start();


            }
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

}
