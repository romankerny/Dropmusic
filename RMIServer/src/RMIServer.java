import java.io.IOException;
import java.net.*;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {


    private ConcurrentHashMap<String, RMIClientInterface> clients = new ConcurrentHashMap<String, RMIClientInterface>();
    private static final long serialVersionUID = 1L;
    private static String MULTICAST_ADDRESS = "224.3.2.1";
    private static int SEND_PORT = 5213, RCV_PORT = 5214;
    static RMIServer rmiServer;
    public static ArrayList<String> multicastHashes = new ArrayList<>();
    public static int globalCounter = 0;


    public RMIServer() throws RemoteException {
        super();
    }

    public void sendUDPDatagram(String resp) {


        if (globalCounter == multicastHashes.size()) globalCounter = 0;
        if(multicastHashes.size() > 0)
            resp += "hash|" + multicastHashes.get(globalCounter++);


        try {

            MulticastSocket socket = new MulticastSocket();

            byte[] buffer = resp.getBytes();
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, SEND_PORT);
            socket.send(packet);

            //socket.leaveGroup(group);
            //socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendUDPDatagramGeneral(String resp) {

        // only to send connection packets

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

    public String receiveUDPDatagram(String rspToRetry) {

        String message = null;
        boolean exit = false;
        while(!exit) {
            try {
                MulticastSocket socket = new MulticastSocket(RCV_PORT);  // create socket and bind it
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                socket.joinGroup(group);

                socket.setSoTimeout(5000);


                byte[] buffer = new byte[65536];

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message: " + message);
                exit = true;

            } catch (SocketTimeoutException te) {
                System.out.println("Server not responding retrying...");
                sendUDPDatagram(rspToRetry);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    public String receiveUDPDatagram() {

        String message = null;

        try {
            MulticastSocket socket = new MulticastSocket(RCV_PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);


            byte[] buffer = new byte[65536];

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
            String rsp = receiveUDPDatagram(msg);
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
        // Response -> flag | r; type | privilege; result | (y/n); user1 | username; user2 | username;

        String msg = "flag|s;type|privilege;user1|" + editor + ";user2|" + regular + ";";
        boolean exit = false;
        String rspToClient = "Failed to cast " + regular + " to editor";

        sendUDPDatagram(msg);

        while(!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if(cleanMessage.get(0)[1].equals("r") && cleanMessage.get(1)[1].equals("privilege") && cleanMessage.get(2)[1].equals("y") && cleanMessage.get(3)[1].equals(editor)
                && cleanMessage.get(4)[1].equals(regular) ) {
                System.out.println("aqui");
                rspToClient = regular + " casted to Editor with success";

                try {
                    clients.get(regular).printOnClient("You got promoted to Editor by " + editor);
                } catch (RemoteException re) {
                    System.out.println("Client is off");
                    sendUDPDatagram("flag|s;type|notifyfail;email|"+regular+";message|"+"You got promoted to Editor by "+editor+";");
                } catch (NullPointerException npe) {
                    System.out.println("Client is off");
                    sendUDPDatagram("flag|s;type|notifyfail;email|"+regular+";message|"+"You got promoted to Editor by "+editor+";");
                }
                exit = true;

            }
        }
        return rspToClient;
    }

    public int uploadMusic(String title, String uploader) {
        // Request  -> flag | s; type | requestTCPConnection; operation | upload; title | tttt; email | eeee;
        // Response -> flag | r; type | requestTCPConnection; operation | upload; email | eeee; result | y; port | pppp;
        String msg = "flag|s;type|requestTCPConnection;operation|upload;title|"+title+";email|"+uploader+";";
        sendUDPDatagram(msg);

        boolean exit = false;
        while(!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);
            if(cleanMessage.get(1)[1].equals("requestTCPConnection") && cleanMessage.get(2)[1].equals("upload") &&
            cleanMessage.get(3)[1].equals(uploader) && cleanMessage.get(4)[1].equals("y"))
            {
                System.out.println("Recebi datagram no uploadMusic vou retornar a porta");
                return Integer.parseInt(cleanMessage.get(5)[1]);
            }
        }
        return 0;

    }

    public int downloadMusic(String title, String uploader, String email) throws RemoteException {
        // Request  -> flag | s; type | requestTCPConnection; operation | download; title | tttt; uploader | uuuu; email | eeee
        // Response -> flag | r; type | requestTCPConnection; operation | download; email | eeee; result | y; port | pppp;
        String msg = "flag|s;type|requestTCPConnection;operation|download;title|"+title+";uploader|"+uploader+";email|"+email+";";
        sendUDPDatagram(msg);

        boolean exit = false;
        while(!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if(cleanMessage.get(1)[1].equals("requestTCPConnection") && cleanMessage.get(2)[1].equals("download") &&
                    cleanMessage.get(3)[1].equals(email) && cleanMessage.get(4)[1].equals("y"))
            {
                System.out.println("Recebi datagram no uploadMusic vou retornar a porta");
                return Integer.parseInt(cleanMessage.get(5)[1]);
            }
        }
        return 0;
    }


    public String share(String title, String shareTo, String uploader) throws RemoteException {
        // Request  -> flag | s; type | share; title | tttt; shareTo | sssss; uploader | uuuuuu;
        // Response -> flag | r; type | share; result | (y/n): title | ttttt; shareTo | ssssss; uploader | uuuuuu;
        String msg = "flag|s;type|share;title|"+title+";shareTo|"+shareTo+";uploader|"+uploader+";";
        sendUDPDatagram(msg);

        boolean exit = false;
        String rspToClient = "Packet didn't arrive";
        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(1)[1].equals("share") && cleanMessage.get(5)[1].equals(uploader)) {
                if (cleanMessage.get(2)[1].equals("y")) {
                    rspToClient = "Successfully shared +"+title+" with "+shareTo;
                    exit = true;
                } else {
                    rspToClient = "Failed to share";
                    exit = true;
                }
            }
        }


    return rspToClient;
    }

    public void subscribe(String email, RMIClientInterface clientInterface) throws RemoteException{
        this.clients.put(email, clientInterface);
    }

    public String login(String email, String password, RMIClientInterface client) throws RemoteException {
        System.out.println("Entered login");
        // Request  -> flag | s; type | login; email | eeee; password | pppp;
        // Response -> flag | r; type | login; result | (y/n); email | eeee; password | pppp; notification_count | n; notif_x | msg; [etc...]

        String msg = "flag|s;type|login;email|"+email+";password|"+password+";";
        boolean exit = false;
        String rspToClient = "Failed to login";
        sendUDPDatagram(msg);

        while(!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals("r") && cleanMessage.get(1)[1].equals("login")) {
                if (cleanMessage.get(2)[1].equals("y")) {
                    if (cleanMessage.get(3)[1].equals(email) && cleanMessage.get(4)[1].equals(password)) {
                        int numNotifications = Integer.parseInt(cleanMessage.get(5)[1]);

                        rspToClient = "Logado com sucesso " + email + " " + password;
                        subscribe(email, client);

                        if (numNotifications > 0) {
                            rspToClient += "\nMissed notifications:\n";
                            for (int i = 0; i < numNotifications; i++) {
                                rspToClient += cleanMessage.get(6+i)[1] + "\n";
                            }
                        }
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
            String rsp = receiveUDPDatagram(msg);
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
            String rsp = receiveUDPDatagram(msg);
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

    public String search(String param, String keyword) throws RemoteException {
        // Request  -> flag | s; type | search; param | (art, gen, tit); keyword | kkkk;
        // Response -> flag | r; type | search; result | (y/n); param | (art, gen, tit); keyword | kkkk; item_count | n; iten_x_name | name; [...

        String msg = "flag|s;type|details;param|"+param+";keyword|"+keyword+";";
        boolean exit = false;
        String rspToClient = "";

        sendUDPDatagram(msg);

        while(!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if(cleanMessage.get(0)[1].equals("r") && cleanMessage.get(1)[1].equals("details")) {
                if (cleanMessage.get(2)[1].equals("y")) {
                    int numItems = Integer.parseInt(cleanMessage.get(5)[1]);
                    for (int i = 0; i < numItems; i++) {
                        rspToClient += cleanMessage.get(6+i)[1];
                    }
                } else {
                    rspToClient = "Search failed";
                }
                exit = true;
            }
        }
        return rspToClient;
    }


    public void printOnServer(String s) throws RemoteException {
        System.out.println("> " + s);
    }

    public boolean isAlive() throws RemoteException {
        return true;
    }

    public int sizeHashMap() throws RemoteException {
        return clients.size();
    }

    public ConcurrentHashMap<String, RMIClientInterface> getHashMap() throws RemoteException {
        return this.clients;
    }


    public RMIServer getServerObject() throws RemoteException {
        return this.rmiServer;
    }

    // =========================================================
    public static void main(String args[]) throws NotBoundException, AlreadyBoundException, IOException {
        String msg;
        String q = null;

        MulticastSocket socket = new MulticastSocket(RCV_PORT);  // create socket and bind it
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        socket.joinGroup(group);

        /*System.setProperty("java.security.policy","policy.all");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }*/

        try {
            int sizeHashMap = 0, failCount = 0;
            boolean failedLastTime = false;
            boolean takeOver = false;


            rmiServer = new RMIServer();
            Registry r = null;

            try {

                r = LocateRegistry.createRegistry(1099);
                r.bind("rmiserver", rmiServer);

            } catch (ExportException ree) {
               RMIServerInterface mainServerInterface;
                System.out.println("Starting RMIBackup");

                while (!takeOver) {
                    try {

                        try {

                            mainServerInterface = (RMIServerInterface) LocateRegistry.getRegistry(1099).lookup("rmiserver");
                            System.out.println("Pinging MainRMI");
                            if (mainServerInterface.isAlive()) {
                                failedLastTime = false;
                            }

                            if(!failedLastTime) {
                                failCount = 0;
                            }

                        } catch (RemoteException re) {
                            System.out.println("Failed to access MainRMI");

                            failCount++;

                            if (failCount == 5 && failedLastTime) {
                                failCount = 0;
                                takeOver = true;
                                r = LocateRegistry.createRegistry(1099);
                            }
                            failedLastTime = true;
                        }

                        sleep((long) (500));

                    } catch (InterruptedException Et) {
                        System.out.println(Et);
                    }

                }
            }


            r.rebind("rmiserver", rmiServer);
            System.out.println("Taking over RMIMain");

            q = "flag|s;type|connectionrequest;";
            rmiServer.sendUDPDatagramGeneral(q);


        } catch (RemoteException re) {
            System.out.println("Exception in RMIServer.main: " + re);
        }
        while(true) {
            // waiting for MulticastServers to group up
            // Response -> flag | r; type | ack; hash | hhhh;

            /*
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */
            byte[] buffer = new byte[65536];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message: " + message);
            ArrayList<String[]> cleanMessage = rmiServer.cleanTokens(message);

            if (cleanMessage.get(0)[1].equals("r") && cleanMessage.get(1)[1].equals("ack"))
                if (!multicastHashes.contains(cleanMessage.get(2)[1]))
                    multicastHashes.add(cleanMessage.get(2)[1]);


        }

    }
}
