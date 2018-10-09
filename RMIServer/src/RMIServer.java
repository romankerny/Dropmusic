import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String MULTICAST_ADDRESS = "224.3.2.1";
    private int PORT = 5214;

    public RMIServer() throws RemoteException {
        super();
    }

    public String sendPila() throws RemoteException {
        System.out.println("Print do lado do servidor");

        try {
            String resp = "pila";
            MulticastSocket socket = new MulticastSocket();
            byte[] buffer = resp.getBytes();

            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Receber

        // wait for packets

        try {
            MulticastSocket socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);


            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);

                System.out.println(message);
                return message;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "merda";
    }


    public void printOnServer(String s) throws RemoteException {
        System.out.println("> " + s);
    }

    // =========================================================
    public static void main(String args[]) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            RMIServer h = new RMIServer();
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("rmiserver", h);
            System.out.println("RMIServer ready.");






        } catch (RemoteException re) {
            System.out.println("Exception in RMIServer.main: " + re);
        }

    }
}
