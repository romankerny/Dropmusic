import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {

    private ConcurrentHashMap<String, RMIClientInterface> clients = new ConcurrentHashMap<String, RMIClientInterface>();

    private static final long serialVersionUID = 1L;
    private String MULTICAST_ADDRESS = "224.3.2.1";
    private int SEND_PORT = 5213, RCV_PORT = 5214;

    public RMIServer() throws RemoteException {
        super();
    }



    public void sendUDPDatagram(String resp) {

        try {

            MulticastSocket socket = new MulticastSocket();
            byte[] buffer = resp.getBytes();

            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, SEND_PORT);
            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String receiveUDPDatagram() {

        String message = null;

        try {
            MulticastSocket socket = new MulticastSocket(RCV_PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);


            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message: " + message);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return message;
    }

    ArrayList<String[]> cleanTokens(String msg) {

        String[] tokens = msg.split(";");
        String[] p;

        ArrayList<String[]> rtArray = new ArrayList<String[]>();

        for (int i = 0; i < tokens.length; i++) {
            //tokens[i] = tokens[i].replaceAll("\\s+", "");
            p = tokens[i].split(Pattern.quote("|"));
            rtArray.add(p);
        }
        return rtArray;
    }

    public String register(String name, String password) throws RemoteException {

        String msg = "flag|s;type|register;username|" + name + ";password|" + password+";"; // protocol to register
        boolean exit = false;
        String rspToClient = null;

        sendUDPDatagram(msg);

        while(!exit) {
            String rsp = receiveUDPDatagram();
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals("r") && cleanMessage.get(1)[1].equals("register")) {
                if (cleanMessage.get(2)[1].equals("y") && cleanMessage.get(3)[1].equals(name) && cleanMessage.get(4)[1].equals(password)) {
                    rspToClient = "User "+name+" registered successfully";

                } else {
                    rspToClient = "Failed to register";
                }
                exit = true;
            }
        }

        return rspToClient;
    }

    public String regularToEditor(String editor, String regular) throws RemoteException {
        // Request  -> flag | s; type | privilege; user1 | username; user2 | username;
        // Response -> flag | r; type | privilege; result | (y/n): user1 | username; user2; username;

        String msg = "flag|s;type|privilege;user1|" + editor + ";user2|" + regular + ";";
        boolean exit = false;
        String rspToClient = "Failed to cast " + regular + " to editor";

        sendUDPDatagram(msg);

        while(!exit) {
            String rsp = receiveUDPDatagram();
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if(cleanMessage.get(0)[1].equals("r") && cleanMessage.get(1)[1].equals("privilege") && cleanMessage.get(3)[1].equals(editor)
                && cleanMessage.get(4)[1].equals(regular) && cleanMessage.get(2)[1].equals("y")) {
                rspToClient = regular + " casted to Editor with success";
                exit = true;

            }
        }

        return rspToClient;
    }




    public String login(String email, String password, RMIClientInterface client) throws RemoteException {

        //
        // [PROTOCOL] flag | s; type | login; email | eeee; password | pppp;

        String msg = "flag|s;type|login;email|"+email+";password|"+password+";";
        boolean exit = false;
        String rspToClient = "Failed to login";

        sendUDPDatagram(msg);

        while(!exit) {
            String rsp = receiveUDPDatagram();
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals("r") && cleanMessage.get(1)[1].equals("login")) {
                if (cleanMessage.get(2)[1].equals("y")) {
                    if (cleanMessage.get(3)[1].equals(email) && cleanMessage.get(4)[1].equals(password)) {
                        rspToClient = "Logado com sucesso " + email + " " + password;

                        this.clients.put(email, client);
                        exit = true;
                    }
                } else {
                    // result | n
                    return rspToClient;
                }
            }
        }

        return rspToClient;
    }

    public String logout(String email) throws RemoteException {
        String msg = "flag|s;type|logout;email|"+email+";";
        boolean exit = false;
        String rspToClient = "Failed to logout";

        sendUDPDatagram(msg);

        while(!exit) {
            String rsp = receiveUDPDatagram();
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals("r") && cleanMessage.get(1)[1].equals("logout")) {
                if (cleanMessage.get(2)[1].equals("y") && cleanMessage.get(3)[1].equals(email)) {
                    rspToClient = "Logged out successfully";
                    exit = true;
                }

            }
        }
        return rspToClient;
    }

    public String rateAlbum(int stars, String albumName, String review, String email) throws RemoteException {
        //   Request  -> flag | s; type | critic; album | name; critic | blabla; rate | n; email | eeee;
        //   Response -> flag | r; type | critic; result | (y/n); album | name; critic | blabla; rate | n; email | eeee;
        String msg = "flag|s;type|critic;album|"+albumName+";critic|"+review+";rate|"+stars+"; email|"+email+";";
        boolean exit = false;
        String rspToClient = "Failed to rate "+albumName;

        sendUDPDatagram(msg);

        while(!exit) {
            String rsp = receiveUDPDatagram();
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals("r") && cleanMessage.get(1)[1].equals("critic")) {
                if (cleanMessage.get(2)[1].equals("y")) {
                    rspToClient = "Edited sucessfully";
                    exit = true;
                } else {
                    rspToClient = "Failed to edit";
                    exit = true;
                }
            }
        }
        System.out.println(rspToClient);
        return rspToClient;
    }


    public void printOnServer(String s) throws RemoteException {
        System.out.println("> " + s);
    }

    // =========================================================
    public static void main(String args[]) {

        String msg;

        try {
            RMIServer h = new RMIServer();
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("rmiserver", h);
            System.out.println("RMIServer ready.");


            // Handle notifications

            while(true) {
                // flag | r; type | notify; message | msg; user_count | n; user_x_email | email; [...]
                msg = h.receiveUDPDatagram();
                ArrayList<String[]> cleanMessage = h.cleanTokens(msg);

                if (cleanMessage.get(0)[1].equals("r") && cleanMessage.get(1)[1].equals("notify")) {

                    String userMsg = cleanMessage.get(2)[1];
                    int numUsers = Integer.parseInt(cleanMessage.get(3)[1]);

                    String email;
                    for (int i = 0; i < numUsers; i++) {
                        email = cleanMessage.get(4+i)[1];

                        try {
                            h.clients.get(email).printOnClient(userMsg);
                        } catch (RemoteException re){
                            System.out.println("Exception: "+ re);
                            // To be tested!
                            String failure = "flag|s;type|notify;message|Failed to printOnClient;user_count|1;user_1_email|"+email+";";
                            h.sendUDPDatagram(failure);


                        }
                    }

                }

            }


        } catch (RemoteException re) {
            System.out.println("Exception in RMIServer.main: " + re);
        }

    }
}
