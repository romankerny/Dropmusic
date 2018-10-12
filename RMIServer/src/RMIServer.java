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
import java.util.concurrent.CopyOnWriteArrayList;

public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {

    private CopyOnWriteArrayList<RMIClientInterface> clients = new CopyOnWriteArrayList<RMIClientInterface>();

    private static final long serialVersionUID = 1L;

    private String MULTICAST_ADDRESS = "224.3.2.1";
    private int PORT = 5214;

    public RMIServer() throws RemoteException {
        super();
    }

    public void subscribe(RMIClientInterface client) throws RemoteException {
        if (this.clients.contains(client))
            System.out.println("Client already subscribed");
        else
            this.clients.add(client);


        client.printOnClient("Fuck you bitch you're subscribed now!");
    }

    public void sendUDPDatagram(String resp) {

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

    public String receiveUDPDatagram() {

        String message = null;

        try {
            MulticastSocket socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);


            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);


            message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:" + message);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return message;
    }


    public String sendPila() throws RemoteException {


        sendUDPDatagram("pila");

        String resp = receiveUDPDatagram();

        if(!resp.equals("cona"))
            return "merda";
        else
            return resp;
    }

    public String register(String name, String password) throws RemoteException {

        String msg = "type | register; flag | s; username | " + name + "; password | " + password+";"; // protocol to register
        boolean sair = false;
        String rspToClient = null;

        sendUDPDatagram(msg);
        System.out.println("aqui");

        while(!sair) {
            String rsp = receiveUDPDatagram();
            String[] a_rsp =  rsp.split(" ");

            if(a_rsp[2].equals("register;") & a_rsp[5].equals("r;") & a_rsp[8].equals(name+";") & a_rsp[11].equals(password+";")) {
                rspToClient = "Registado com sucesso " + name + " " + password;
                sair = true;
            }
        }

        // type | register; flag | (s/r); username | name; password | pw; result (y/n)
        // neste caso queremos receber p aceitar
        // type | register; flag | r; username | name; password | pw; result y

        return rspToClient;
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
