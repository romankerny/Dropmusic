import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastServer extends Thread {
    private String MULTICAST_ADDRESS = "224.3.2.1";
    private int PORT = 5214;
    private MulticastSocket socket = null;
    private CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<User>();
    private CopyOnWriteArrayList<Artist> artists = new CopyOnWriteArrayList<Artist>();

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
    }



    public void run() {
        // wait for packets

        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            System.out.println("Multicast server ready");

            Editor admin = new Editor("admin", "admin");
            users.add(admin);

            artists.add(new Artist("Kendrick Lamar"));




            while (true) {

                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                MulticastServerResponse threadToResolvePacket = new MulticastServerResponse(packet, PORT, MULTICAST_ADDRESS, users, artists);
                threadToResolvePacket.start();


            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

}
