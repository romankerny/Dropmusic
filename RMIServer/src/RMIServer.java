import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import static java.lang.Thread.sleep;

public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {


    private ConcurrentHashMap<String, RMIClientInterface> clients = new ConcurrentHashMap<String, RMIClientInterface>();
    private static final long serialVersionUID = 1L;
    private static String MULTICAST_ADDRESS = "224.3.2.2";
    private static int SEND_PORT = 5213, RCV_PORT = 5214;
    static RMIServer rmiServer;
    public ArrayList<String> multicastHashes = new ArrayList<>();
    public static int globalCounter = 0;


    public RMIServer() throws RemoteException {
        super();
    }

    public void sendUDPDatagram(String resp) {


        if (globalCounter == multicastHashes.size()) globalCounter = 0;
        if (multicastHashes.size() > 0)
            resp += "hash|" + multicastHashes.get(globalCounter++);


        sendUDPDatagramGeneral(resp);

    }



    public void sendUDPDatagramGeneral(String resp) {

        // - sends all kinds of packets when it's called by sendUDPDatagram()
        // - sends requestsTCPConnection packets / download packets when called by download() or when creating
        //   connections w/ MulticastServers

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
        while (!exit) {
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

    public String register(String name, String password) {

        // Request  -> flag | s; type | register; email | eeee; password | pppp;
        // Response -> flag | r; type | register; result | (y/n); email | eeee; password | pppp; msg | mmmmmmmm;

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));
        String msg = "flag|"+id+";type|register;username|" + name + ";password|" + password + ";"; // protocol to register
        boolean exit = false;
        String rspToClient = "Failed to register";

        sendUDPDatagram(msg);

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                if (cleanMessage.get(2)[1].equals("y")) {
                    rspToClient = "User " + name + " registered successfully";

                } else {
                    rspToClient = cleanMessage.get(5)[1]; // result n, get error message
                }
                exit = true;
            }
        }
        return rspToClient;
    }

    public void subscribe(String email, RMIClientInterface clientInterface) {
        this.clients.put(email, clientInterface);
    }

    public String logout(String email) {

        clients.remove(email);
        return "Logged out";
    }

    public String login(String email, String password, RMIClientInterface client) throws RemoteException {
        // Request  -> flag | s; type | login; email | eeee; password | pppp;
        // Response -> flag | r; type | login; result | (y/n); email | eeee; password | pppp; notification_count | n; notif_x | notif; msg | mmmmmm;
        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|login;email|" + email + ";password|" + password + ";";
        boolean exit = false;
        String rspToClient = "Failed to login";
        sendUDPDatagram(msg);

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                if (cleanMessage.get(2)[1].equals("y")) {
                    int numNotifications = Integer.parseInt(cleanMessage.get(5)[1]);

                    rspToClient = "Logged in successfully " + email;
                    subscribe(email, client);

                    if (numNotifications > 0) {
                        rspToClient += "\nMissed notifications:\n";
                        for (int i = 0; i < numNotifications; i++) {
                            rspToClient += cleanMessage.get(6 + i)[1] + "\n";
                        }
                    }
                } else {
                    // result | n
                    rspToClient = cleanMessage.get(cleanMessage.size()-2)[1]; // Error message comes penultima
                }
                exit = true;
            }
        }
        return rspToClient;
    }

    public String regularToEditor(String editor, String regular) {
        // Request  -> flag | id; type | privilege; user1 | username; user2; username;
        // Response -> flag | id; type | privilege; result | (y/n): user1 | username; user2 | username; msg | mmmmmmmm;

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String uuidNotify = UUID.randomUUID().toString();
        String idNotify = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|privilege;user1|" + editor + ";user2|" + regular + ";";
        boolean exit = false;
        String rspToClient = "Failed to cast " + regular + " to editor";

        sendUDPDatagram(msg);

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                System.out.println(cleanMessage.get(2)[1]);
                if (cleanMessage.get(2)[1].equals("y")) {
                    System.out.println("Sucesss");
                    rspToClient = regular + " casted to Editor with success";
                    // Notify him
                    try {
                        clients.get(regular).printOnClient("You got promoted to Editor by " + editor);
                    } catch (RemoteException re) {
                        clients.remove(regular);
                        System.out.println("Client is off");
                        sendUDPDatagram("flag|"+idNotify+";type|notifyfail;email|" + regular + ";message|" + "You got promoted to Editor by " + editor + ";");
                    } catch (NullPointerException npe) {
                        System.out.println("Client is off");
                        sendUDPDatagram("flag|"+idNotify+";type|notifyfail;email|" + regular + ";message|" + "You got promoted to Editor by " + editor + ";");
                    }
                } else {
                    System.out.println("Aqui");
                    rspToClient = cleanMessage.get(cleanMessage.size()-2)[1];
                }
                System.out.println("Aqui true");
                exit = true;
            }
        }
        System.out.println("Bou dar a answer");
        return rspToClient;
    }

    public String uploadMusic(String title, String uploader) {
        // Request  -> flag | id; type | requestTCPConnection; operation | upload; title | tttt; uploader | uuuu; email | eeee
        // Response -> flag | id; type | requestTCPConnection; operation | upload; email | eeee; result | y; port | pppp;
        // Response -> flag | id; type | requestTCPConnection; operation | upload; email | eeee; result | n; msg | mmmmmmmmm;


        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String rspToClient = "No dice";
        String msg = "flag|"+id+";type|requestTCPConnection;operation|upload;title|" + title + ";email|" + uploader + ";";
        sendUDPDatagram(msg);

        boolean exit = false;
        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);
            if (cleanMessage.get(0)[1].equals(id)) {
                System.out.println(cleanMessage.get(4)[1]);
                if (cleanMessage.get(4)[1].equals("y")) {
                    System.out.println("Recebi datagram no uploadMusic vou retornar a porta");
                    return cleanMessage.get(5)[1] + " " + cleanMessage.get(6)[1];
                } else {
                    return cleanMessage.get(cleanMessage.size()-2)[1];
                }
            }
        }
        return "";

    }

    public String downloadMusic(String title, String uploader, String email)  {
        // Request  -> flag | id; type | requestTCPConnection; operation | download; email | eeee;
        // Response -> flag | id; type | requestTCPConnection; operation | download; email | eeee; result | y; ip| iiii; port | pppp;
        // Response -> flag | id; type | requestTCPConnection; operation | download; email | eeee; result | n; msg | mmmmmmmmm;

        System.out.println("a iniciar funcao de download");

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        try {
            refreshMulticastHashes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean exit = false;
        int c = 0;
        exit = false;


        for(String hash: rmiServer.multicastHashes) {
            sendUDPDatagramGeneral("flag|" + id + ";type|requestTCPConnection;operation|download;title|" + title + ";uploader|" + uploader + ";email|" + email + ";hash|" + hash + ";");
            System.out.println("asking multicasts to search music " + hash);
        }

        MulticastSocket socket = null;
        DatagramPacket packet = null;

        try {
            socket = new MulticastSocket(RCV_PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            byte[] buffer = new byte[65536];
            packet = new DatagramPacket(buffer, buffer.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!exit) {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String message = new String(packet.getData(), 0, packet.getLength());
            ArrayList<String[]> cleanMessage = cleanTokens(message);
            if(cleanMessage.get(0)[1].equals(id)) {

                if (cleanMessage.get(4)[1].equals("n")) {
                    c++;
                    if(c == multicastHashes.size()) {
                        exit = true;
                    }
                }else if (cleanMessage.get(4)[1].equals("y")) {
                    System.out.println("Received IP and PORT to download "+title);
                    return cleanMessage.get(5)[1] + " " + cleanMessage.get(6)[1];
                }

            }

        }
        return "Music file not found.";
    }

    public String share(String title, String shareTo, String uploader) {
        // Request  -> flag | id; type | share; title | tttt; shareTo | sssss; uploader | uuuuuu;
        // Response -> flag | id; type | share; result | (y/n): title | ttttt; shareTo | ssssss; uploader | uuuuuu; msg | mmmmmm;

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|share;title|" + title + ";shareTo|" + shareTo + ";uploader|" + uploader + ";";
        sendUDPDatagram(msg);

        boolean exit = false;
        String rspToClient = "Packet didn't arrive";
        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                if (cleanMessage.get(2)[1].equals("y")) {
                    rspToClient = "Successfully shared " + title + " with " + shareTo;
                    exit = true;
                } else {
                    rspToClient = cleanMessage.get(cleanMessage.size()-2)[1];
                    exit = true;
                }
            }
        }


        return rspToClient;
    }

    public String rateAlbum(int stars, String albumName, String review, String email) {
        // flag | id; type | critic; album | name; critic | blabla; rate | n; email | eeee;
        // flag | id; type | critic; result | (y/n); album | name; critic | blabla; rate | n; msg | mmmmm;

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|critic;album|" + albumName + ";critic|" + review + ";rate|" + stars + "; email|" + email + ";";
        boolean exit = false;
        String rspToClient = "Failed to rate " + albumName;

        sendUDPDatagram(msg);

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                if (cleanMessage.get(2)[1].equals("y")) {
                    rspToClient = "Rated "+albumName+" as "+stars+" successfully";
                } else {
                    rspToClient = cleanMessage.get(cleanMessage.size()-2)[1];
                }
                exit = true;
            }
        }
        return rspToClient;
    }

    public String search(String param, String keyword) {
        // Request  -> flag | id; type | details; param | (art, gen, tit); keyword | kkkk;
        // Response -> flag | id; type | details; result | (y/n); param | (art, gen, tit); keyword | kkkk; item_count | n; iten_x_name | name; [...] msg | mmmmmm;

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|details;param|" + param + ";keyword|" + keyword + ";", rspToClient = "";
        boolean exit = false;

        sendUDPDatagram(msg);

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                if (cleanMessage.get(2)[1].equals("y")) {
                    int numItems = Integer.parseInt(cleanMessage.get(5)[1]);
                    for (int i = 0; i < numItems; i++) {
                        rspToClient += cleanMessage.get(6 + i)[1];
                    }
                } else {
                    rspToClient = cleanMessage.get(cleanMessage.size()-2)[1];
                }
                exit = true;
            }
        }
        return rspToClient;
    }

    public String addArtist(String artist, String details, String email) throws RemoteException {
        // Request  -> flag | s; type | addart; name | nnnn; details | dddd; email | dddd;
        // Response -> flag | r; type | addart; email | dddd; result | (y/n); notif_count | n; notif | email; notif | email; [etc...]; msg | mmmmm;

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|addart;name|" + artist + ";details|" + details + ";email|" + email + ";", rspToClient = "";
        sendUDPDatagram(msg);
        boolean exit = false;

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {

                rspToClient = cleanMessage.get(cleanMessage.size()-2)[1]; // Get message for y or n
                if (rspToClient.equals("Artist updated")) {
                    int numUsers = Integer.parseInt(cleanMessage.get(4)[1]);
                    System.out.println(numUsers);
                    System.out.println("Num users: "+numUsers);
                    if (numUsers > 0) {
                        String userEmail = "";
                        String notification = "";
                        try {
                            for (int i = 0; i < numUsers; i++) {
                                userEmail = cleanMessage.get(5+i)[1];
                                System.out.println(userEmail);
                                notification = "Artist `" + artist + "` was edited";
                                clients.get(userEmail).printOnClient(notification);
                            }

                        } catch (RemoteException re) {
                            String uuidNotify = UUID.randomUUID().toString();
                            String idNotify = uuid.substring(0, Math.min(uuid.length(), 8));
                            sendUDPDatagram("flag|" + idNotify + ";type|notifyfail;email|" + userEmail + ";message|" + notification+";");
                        } catch (NullPointerException npe) {
                            String uuidNotify = UUID.randomUUID().toString();
                            String idNotify = uuid.substring(0, Math.min(uuid.length(), 8));
                            sendUDPDatagram("flag|" + idNotify + ";type|notifyfail;email|" + userEmail + ";message|" + notification+";");
                        }
                    }
                } else if(rspToClient.equals("Artist created")) {
                    rspToClient = "Artist created";
                }

                exit = true;
            }
        }
        return rspToClient;
    }

    public String addAlbum(String artist, String albumTitle, String description, String genre, String email) throws  RemoteException{
        // Request  -> flag | s; type | addalb; art | aaaa; alb | bbbb; description | dddd; genre | gggg; email | dddd;
        // Response -> flag | r; type | addalb; email | ddd; result |(y/n); notif_count | n; notif | email; notif | email; [etc...]; msg | mmmmm;

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|addalb;art|" + artist + ";alb|" + albumTitle + ";description|" + description + ";genre|" + genre + ";email|" + email + ";", rspToClient = "";
        sendUDPDatagram(msg);
        boolean exit = false;

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                rspToClient = cleanMessage.get(cleanMessage.size()-2)[1];

                // Need to notify
                if (rspToClient.equals("Album updated") || rspToClient.equals("Album created")) {
                    int numUsers = Integer.parseInt(cleanMessage.get(4)[1]);
                    System.out.println("Num users: "+numUsers);
                    if (numUsers > 0) {
                        String userEmail = "";
                        String notification = "";
                        try {
                            for (int i = 0; i < numUsers; i++) {
                                userEmail = cleanMessage.get(5+i)[1];
                                System.out.println(userEmail);
                                notification = "Album `" + albumTitle + "` by " + artist + " was edited";
                                clients.get(userEmail).printOnClient(notification);
                            }

                        } catch (RemoteException re) {
                            String uuidNotify = UUID.randomUUID().toString();
                            String idNotify = uuid.substring(0, Math.min(uuid.length(), 8));
                            sendUDPDatagram("flag|" + idNotify + ";type|notifyfail;email|" + userEmail + ";message|" + notification+";");
                        } catch (NullPointerException npe) {
                            String uuidNotify = UUID.randomUUID().toString();
                            String idNotify = uuid.substring(0, Math.min(uuid.length(), 8));
                            sendUDPDatagram("flag|" + idNotify + ";type|notifyfail;email|" + userEmail + ";message|" + notification+";");
                        }
                    }
                }
                exit = true;

            }

        }
        return rspToClient;
    }

    public String addMusic(String musicTitle, String track, String albumTitle , String email) throws  RemoteException{
        // Request  -> flag | s; type | addmusic; alb | bbbb; title | tttt; track | n; email | dddd;
        // Response -> flag | r; type | addmusic; title | tttt; email | dddd; result | (y/n); notif_count | n; notif | email; notif | email; [etc...]; msg | mmmmm;

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg =  "flag|"+id+";type|addmusic;alb|"+albumTitle+";title|"+musicTitle+";track|"+track+";email|"+email+";"  ,rspToClient = "";
        sendUDPDatagram(msg);
        boolean exit = false;

        while(!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if(cleanMessage.get(0)[1].equals(id)) {
                rspToClient = cleanMessage.get(cleanMessage.size()-2)[1];

                // Try to notify
                if (rspToClient.equals("Music updated") || rspToClient.equals("Music created")) {
                    int numUsers = Integer.parseInt(cleanMessage.get(5)[1]);
                    System.out.println("Num users: "+numUsers);
                    if (numUsers > 0) {
                        String userEmail = "";
                        String notification = "";
                        try {
                            for (int i = 0; i < numUsers; i++) {
                                userEmail = cleanMessage.get(6+i)[1];
                                System.out.println(userEmail);
                                notification = "Track `" + musicTitle + "` from " + albumTitle + " was edited;";
                                clients.get(userEmail).printOnClient(notification);
                            }

                        } catch (RemoteException re) {
                            String uuidNotify = UUID.randomUUID().toString();
                            String idNotify = uuid.substring(0, Math.min(uuid.length(), 8));
                            sendUDPDatagram("flag|" + idNotify + ";type|notifyfail;email|" + userEmail + ";message|" + notification+";");
                        } catch (NullPointerException npe) {
                            String uuidNotify = UUID.randomUUID().toString();
                            String idNotify = uuid.substring(0, Math.min(uuid.length(), 8));
                            sendUDPDatagram("flag|" + idNotify + ";type|notifyfail;email|" + userEmail + ";message|" + notification+";");
                        }
                    }
                }
                exit = true;
            }

        }
        return rspToClient;
    }

    public boolean isAlive() throws RemoteException {
        return true;
    }


    public void refreshMulticastHashes() throws IOException {

        boolean exit = false;
        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));
        rmiServer.sendUDPDatagramGeneral("flag|"+id+";type|connectionrequest;");
        rmiServer.multicastHashes.clear();
        MulticastSocket socket = new MulticastSocket(RCV_PORT);  // create socket and bind it
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        socket.joinGroup(group);
        socket.setSoTimeout(3000);


        while(!exit) {
            // waiting for MulticastServers to group up

            try {
                byte[] buffer = new byte[65536];

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received packet in refreshMulticast(): " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message: " + message);
                ArrayList<String[]> cleanMessage = rmiServer.cleanTokens(message);


                if (cleanMessage.get(0)[1].equals(id)) {
                    rmiServer.multicastHashes.add(cleanMessage.get(2)[1]);
                }

                System.out.println("no fim deste método " + rmiServer.multicastHashes);
            } catch (SocketTimeoutException yo) {
                exit = true;
            } catch (UnknownHostException e) {
                exit = true;
            } catch (SocketException e) {
                exit = true;
            } catch (IOException e) {
                exit = true;
            }
        }


    }



    public static void main(String args[]) throws NotBoundException, AlreadyBoundException, IOException {

        System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());
        String msg;
        String q = null;

        MulticastSocket socket = new MulticastSocket(RCV_PORT);  // create socket and bind it
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        socket.joinGroup(group);


        try {
            int failCount = 0;
            boolean failedLastTime = false, takeOver = false;
            Registry r = null;
            rmiServer = new RMIServer();


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


            byte[] buffer = new byte[65536];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message: " + message);
            ArrayList<String[]> cleanMessage = rmiServer.cleanTokens(message);

            if (cleanMessage.get(0)[1].equals("r") && cleanMessage.get(1)[1].equals("ack"))
                if (!rmiServer.multicastHashes.contains(cleanMessage.get(2)[1]))
                    rmiServer.multicastHashes.add(cleanMessage.get(2)[1]);


        }

    }
}
