import java.io.IOException;
import java.lang.invoke.MutableCallSite;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastServerResponse extends Thread {

    private DatagramPacket packet;
    private MulticastSocket sendSocket = null; // socket to respond to Multicast group
    private int PORT;
    private String MULTICAST_ADDRESS;

    MulticastServerResponse(DatagramPacket packet, int port, String ip) {

        this.packet = packet;
        PORT = port;
        MULTICAST_ADDRESS = ip;
    }

    public void sendResponseMulticast(String resp) {


        try {
            MulticastSocket socket = new MulticastSocket();
            byte[] buffer = resp.getBytes();

            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {

        System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println(message);

        if(message.equals("pila"))
            sendResponseMulticast("cona");


    }


}
