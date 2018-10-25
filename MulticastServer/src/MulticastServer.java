import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.3.2.2";
    private int RECV_PORT = 5213;
    private MulticastSocket socket = null;
    private CopyOnWriteArrayList<User> users;
    private CopyOnWriteArrayList<Artist> artists;

    public MulticastServer() {
        artists = new CopyOnWriteArrayList<Artist>();
        users = new CopyOnWriteArrayList<User>();
    }

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
    }

    public void sendResponseMulticast(String resp) {

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



    public void run() {
        // Read from memory
        users = ObjectFiles.readUsersFromDisk();
        artists = ObjectFiles.readArtistsFromDisk();

        try {
            // Sending HASH-code to Main RMI
            String code = UUID.randomUUID().toString().substring(24);
            sendResponseMulticast("flag|r;type|ack;hash|"+code+";");

            socket = new MulticastSocket(RECV_PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            System.out.println("Multicast server ready - " + code);

            /*
            Code to use when DB's operational.

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/dropmusic?useSSL=false", "root", "root");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from music");
            System.out.println(rs);

            while (rs.next()) {
                System.out.println(rs.getString("title"));
                System.out.println(rs.getInt("track"));
            }
            */


            while (true) {

                byte[] buffer = new byte[65536];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                MulticastServerResponse threadToResolvePacket = new MulticastServerResponse(packet, MULTICAST_ADDRESS, users, artists, code);
                threadToResolvePacket.start();


            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

}
