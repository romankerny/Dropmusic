import java.io.IOException;
import java.net.*;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuthService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import shared.*;
import shared.manage.Album;
import shared.manage.Artist;
import shared.manage.Music;
import uc.sd.apis.DropBoxApi2;

import static java.lang.Thread.activeCount;
import static java.lang.Thread.sleep;




/**
 *
 * shared.RMIServer contains all methods that RMI Client can call via remote interface.
 * The shared.RMIServer interface is named 'rmiserver' and the registry runs on port 1099.
 *
 * This class contains a concurrentHaspMap that holds the correspondence between client's emails and the corresponding
 * RMI Interfaces. It's mainly used to control which users are logged to the Server and comes in hand when the server
 * needs to call the method printOnClient() via it's interface.
 *
 * The Server has 2 main UDP ports:
 *  - 5213 where it sends Datagram Packets
 *  - 5214 where it receives Datagram Packets
 *
 *  The class also contains an array of Strings that has all the UUID hashes that identify MulticastServers in an
 *  unique way.
 *
 */


public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {

    private ConcurrentHashMap<String, RMIClientInterface> clients = new ConcurrentHashMap<String, RMIClientInterface>();
    private static final long serialVersionUID = 1L;
    private static String MULTICAST_ADDRESS = "224.3.2.1";
    private static int SEND_PORT = 5213, RCV_PORT = 5214;
    private static RMIServerInterface rmiServer;
    public static ArrayList<String> multicastHashes = new ArrayList<>();
    public static int globalCounter = 0;

    private static final String API_APP_KEY = "38rui0xr3sccsdp";
    private static final String API_APP_SECRET = "tp9cimdal84i23l";
    private OAuthService service = new ServiceBuilder()
            .provider(DropBoxApi2.class)
            .apiKey(API_APP_KEY)
            .apiSecret(API_APP_SECRET)
            .callback("http://localhost:8080/associateDropBoxTokenAction") //
            .build();


    private OAuthService serviceBeforeLogin = new ServiceBuilder()
            .provider(DropBoxApi2.class)
            .apiKey(API_APP_KEY)
            .apiSecret(API_APP_SECRET)
            .callback("http://localhost:8080/loginDropbox") //
            .build();


    public RMIServer() throws RemoteException {
        super();
    }


    /**
     * Using round-robin algorithm picks an hash code to concatenate with the @param resp
     * then calls sendUDPDatagramGeneral() to send the protocol message.
     *
     * @param resp - protocol instruction to send
     */

    public static void sendUDPDatagram(String resp) {


        if (globalCounter == multicastHashes.size()) globalCounter = 0;
        if (multicastHashes.size() > 0)
            resp += "hash|" + multicastHashes.get(globalCounter++);


        sendUDPDatagramGeneral(resp);

    }

    /**
     *  Creates a multicastSocket and sends a Datagram packet with
     *  some protocol instruction.
     *
     * @param resp - protocol instruction to send
     */

    public static void sendUDPDatagramGeneral(String resp) {

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

    /**
     *
     * Try's to receive a Datagram via Multicast
     * If socket is activated for + than 5 seconds without getting an answer form Multicast the setSoTimeout() creates an exception
     * that re-sends the Datagram to Multicast that was supposed to generate the response.
     * The method that calls this function will be locked until the answer packet it's waiting for is received.
     *
     * @param rspToRetry
     * @return received Datagram
     */

    public static String receiveUDPDatagram(String rspToRetry) {

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

    /**
     *
     * Parses datagram String in simple Tokens
     *
     * @param msg
     * @return ArrayList of tokens example: (id, 54fsdsf4), (type, login) , ....
     */
    static ArrayList<String[]> cleanTokens(String msg) {

        String[] tokens = msg.split(";");
        String[] p;

        ArrayList<String[]> rtArray = new ArrayList<String[]>();

        for (int i = 0; i < tokens.length; i++) {
            p = tokens[i].split(Pattern.quote("|"));
            rtArray.add(p);
        }
        return rtArray;
    }

    /**
     * Receives a name and password
     * creates datagram with the following protocol:
     *
     * Sends to Multicast:
     * Request  -> flag | id; type | register; email | eeee; password | pppp;
     *
     * Receives from Multicast:
     * Response -> flag | id; type | register; result | (y/n); email | eeee; password | pppp; msg | mmmmmm;
     *
     * If result | y -> successes
     * If result | n -> failed, returns an error message to RMIClient
     *
     * @param name
     * @param password
     * @return rsp to Client with result
     */
    public String register(String name, String password) {


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

    /**
     *
     * When a user calls login() w/ success
     * It's @param clientInterface is added to the clients concurrentHashMap
     *
     *
     * @param email
     * @param clientInterface
     */
    public void subscribe(String email, RMIClientInterface clientInterface) {
        this.clients.put(email, clientInterface);
    }

    /**
     * Simply removes the clientInterface from the clients concurrentHashMap
     * @param email
     * @return
     */

    public String logout(String email) {

        clients.remove(email);
        return "Logged out";
    }

    /**
     * Sends to Multicast:
     * Request  -> flag | id; type | login; email | eeee; password | pppp;
     *
     * Receives from Multicast:
     * Response -> flag | id; type | login; result | (y/n); email | eeee; password | pppp; notification_count | n; notif_x | notif; msg | mmmmmm;
     *
     * If the op. is successful the @param client is added to clients concurrentHashMap
     *
     * @param email
     * @param password
     * @param client
     * @return rsp to Client
     * @throws RemoteException
     */

    public String login(String email, String password, RMIClientInterface client) {
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

    public String login(String email, String password) {
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
                    // subscribe(email, client);

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

    public boolean loginDropbox() throws RemoteException {
        return false;
    }

    /**
     * Request  -> flag | id; type | privilege; user1 | username; user2; username;
     * Response -> flag | id; type | privilege; result | (y/n): user1 | username; user2 | username; msg | mmmmmmmm;
     *
     * Try's to cast @param regular to editor
     * If the op. is successful the notification to the @param editor comes along in the UDP Datagram
     *
     * the client with email == @param editor in clients concurrentHashMap gets printed w/ printOnClient() method
     * @param editor
     * @param regular
     * @return rsp to Client that called the function
     */

    public String regularToEditor(String editor, String regular) {

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
                        /*
                        clients.remove(regular);
                        System.out.println("Client is off");
                        sendUDPDatagram("flag|"+idNotify+";type|notifyfail;email|" + regular + ";message|" + "You got promoted to Editor by " + editor + ";");
                        */
                    } catch (NullPointerException npe) {
                        /*
                        System.out.println("Client is off");
                        sendUDPDatagram("flag|"+idNotify+";type|notifyfail;email|" + regular + ";message|" + "You got promoted to Editor by " + editor + ";");
                        */
                    }
                } else {
                    rspToClient = cleanMessage.get(cleanMessage.size()-2)[1];
                }
                exit = true;
            }
        }
        return rspToClient;
    }

    /**
     * Request  -> flag | id; type | requestTCPConnection; operation | upload; title | tttt; uploader | uuuu; email | eeee
     * Response -> flag | id; type | requestTCPConnection; operation | upload; email | eeee; result | y; port | pppp;
     * Response -> flag | id; type | requestTCPConnection; operation | upload; email | eeee; result | n; msg | mmmmmmmmm;
     *
     *
     * @param track
     * @param uploader
     * @return
     */

    public String uploadMusic(String artistName, String albumName, String track, String uploader) {

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String rspToClient = "No dice";
        String msg = "flag|"+id+";type|requestTCPConnection;operation|upload;artist|"+artistName+";album|"+albumName+";track|" + track + ";email|" + uploader + ";";
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

    /**
     * Request  -> flag | id; type | requestTCPConnection; operation | download; email | eeee;
     * Response -> flag | id; type | requestTCPConnection; operation | download; email | eeee; result | y; ip| iiii; port | pppp;
     * Response -> flag | id; type | requestTCPConnection; operation | download; email | eeee; result | n; msg | mmmmmmmmm;
     *
     * RMI and multicast need to establish some things before a download can happen
     *
     * int n_responses it's the number of negative Multicast answers
     *
     * When sending download packets the RMI will wait in maximum for rmiServer.multicastHashes.size()
     * if n_responses == rmiServer.multicastHashes.size() it means that there isn't an online MulticastServer that has
     * the music with @param title with a file available to download
     * @param title
     * @param uploader
     * @param email
     * @return
     */

    public String downloadMusic(String title, String uploader, String email, String albumName, String artistName)  {


        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));
        System.out.println("A iniciar download");
        try {
            // refresh rmiServer.multicastHashes bc there is the possibility that a server died in the past
            // if that happens we need to "clean" our set of Hashes
            refreshMulticastHashes();

            System.out.println(multicastHashes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("AQUI");

        boolean exit = false;
        int n_responses = 0;
        exit = false;

        // send
        for(String hash: RMIServer.multicastHashes) {
            sendUDPDatagramGeneral("flag|" + id + ";type|requestTCPConnection;operation|download;title|" + title + ";uploader|" + uploader + ";email|" + email + ";albumName|" + albumName + ";artistName|" + artistName + ";hash|" + hash + ";");
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
                    n_responses++;
                    if(n_responses == multicastHashes.size()) {
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

    /**
     * Request  -> flag | id; type | share; title | tttt; shareTo | sssss; uploader | uuuuuu;
     * Response -> flag | id; type | share; result | (y/n): title | ttttt; shareTo | ssssss; uploader | uuuuuu; msg | mmmmmm;
     *
     * shares a music with the user with the email == shareTo
     *
     * @param track
     * @param shareTo
     * @param uploader
     * @return result of the operation
     */

    public String share(String artist, String album, String track, String shareTo, String uploader) {

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|share;artist|"+artist+";album|"+album+";track|" + track + ";shareTo|" + shareTo + ";uploader|" + uploader + ";";
        sendUDPDatagram(msg);

        boolean exit = false;
        String rspToClient = "Packet didn't arrive";
        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                if (cleanMessage.get(2)[1].equals("y")) {
                    rspToClient = "Successfully shared with " + shareTo;
                    exit = true;
                } else {
                    rspToClient = cleanMessage.get(cleanMessage.size()-2)[1];
                    exit = true;
                }
            }
        }


        return rspToClient;
    }

    /**
     * flag | id; type | critic; album | name; critic | blabla; rate | n; email | eeee;
     * flag | id; type | critic; result | (y/n); album | name; critic | blabla; rate | n; msg | mmmmm;
     *
     * @param stars
     * @param albumName
     * @param review
     * @param email
     * @return
     */

    public String rateAlbum(int stars, String artistName, String albumName, String review, String email) {


        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|critic;artist|"+artistName+";album|" + albumName + ";critic|" + review + ";rate|" + stars + ";email|" + email + ";";
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

    /**
     * Request  -> flag | id; type | details; param | (art, gen, tit); keyword | kkkk;
     * Response -> flag | id; type | details; result | (y/n); param | (art, gen, tit); keyword | kkkk; item_count | n; iten_x_name | name; [...] msg | mmmmmm;
     * @param param
     * @param keyword
     * @return
     */

    public String search(String param, String keyword) throws RemoteException{

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|details;param|" + param + ";keyword|" + keyword + ";", rspToClient = "";
        boolean exit = false;

        sendUDPDatagram(msg);

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                rspToClient = cleanMessage.get(cleanMessage.size()-2)[1];
                exit = true;
            }
        }
        return rspToClient;
    }

    /**
     * Request  -> flag | id; type | details; param | art; keyword | kkkk;
     * Response -> flag | id; type | details; result | (y/n); param | (art, gen, tit); keyword | kkkk; item_count | n; iten_x_name | name; [...] msg | mmmmmm;
     * @param keyword
     * @return
     */

    public ArrayList<Object> searchArtist(String keyword) throws RemoteException {

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));
        ArrayList<Object> results = new ArrayList<>();

        String msg = "flag|"+id+";type|details;param|art;keyword|" + keyword + ";";
        boolean exit = false;

        sendUDPDatagram(msg);


        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                int nArtists = Integer.parseInt(cleanMessage.get(5)[1]);

                for (int i = 0; i < nArtists; i++) {

                    Artist art = new Artist(cleanMessage.get(6 + i)[1], cleanMessage.get(7 + i)[1]);
                    System.out.println(art);
                    results.add(art);
                }

                exit = true;
            }
        }
        return results;
    }

    /**
     * Request  -> flag | id; type | details; param | alb; keyword | kkkk;
     * Response -> flag | id; type | details; result | (y/n); param | (art, gen, tit); keyword | kkkk; item_count | n; iten_x_name | name; [...] msg | mmmmmm;
     * @param keyword
     * @return
     */

    public ArrayList<Object> searchAlbum(String keyword) throws RemoteException {

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|details;param|alb;keyword|" + keyword + ";", rspToClient = "";
        boolean exit = false;

        sendUDPDatagram(msg);

        ArrayList<Object> results = new ArrayList<>();

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);
            if (cleanMessage.get(0)[1].equals(id)) {

                int nAlbums = Integer.parseInt(cleanMessage.get(5)[1]);

                for (int i = 0; i < nAlbums; i++) {

                    int nReviews = Integer.parseInt(cleanMessage.get(13)[1]);

                    int offset = i*(8+nReviews*3);

                    String albumName = cleanMessage.get(6+offset)[1];
                    String artistName = cleanMessage.get(12+offset)[1];

                    Album foundAlbum = new Album(albumName, cleanMessage.get(7+offset)[1], cleanMessage.get(8+offset)[1], artistName, cleanMessage.get(9+offset)[1], cleanMessage.get(10+offset)[1]);
                    foundAlbum.setAvgRating(Float.parseFloat(cleanMessage.get(11+offset)[1]));

                    System.out.println(foundAlbum);

                    for (int j = 0; j < nReviews; j++) {
                        foundAlbum.getReviews().add(new Review(Integer.parseInt(cleanMessage.get(14+j*3+offset)[1]), cleanMessage.get(15+j*3+offset)[1], cleanMessage.get(16+j*3+offset)[1], artistName, albumName));
                    }
                    results.add(foundAlbum);

                }

                exit = true;
            }
        }
        return results;
    }

    /**
     * Request  -> flag | id; type | details; param | mus; keyword | kkkk;
     * Response -> flag | id; type | details; result | (y/n); param | (art, gen, tit); keyword | kkkk; item_count | n; iten_x_name | name; [...] msg | mmmmmm;
     * @param keyword
     * @return
     */

    public ArrayList<Object> searchMusic(String keyword) throws RemoteException {

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|details;param|mus;keyword|" + keyword + ";";;
        boolean exit = false;

        sendUDPDatagram(msg);

        ArrayList<Object> results = new ArrayList<>();

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {

            int nMusics = Integer.parseInt(cleanMessage.get(5)[1]);

                for (int i = 0; i < nMusics; i++) {

                    Music mus = new Music(cleanMessage.get(6 + i)[1], cleanMessage.get(7 + i)[1], cleanMessage.get(8 + i)[1], cleanMessage.get(9+i)[1], cleanMessage.get(10+i)[1]);
                    System.out.println(mus);
                    results.add(mus);

                }
                exit = true;
                }
            }
        return results;
        }

    public String getMusicURL(String artist, String album, String title, String email) throws RemoteException {
        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));
        String msg = "flag|"+id+";type|musURL;artist|" + artist + ";album|"+album+";title|"+title+";email|"+email+";";

        sendUDPDatagram(msg);
        boolean exit = false;
        String url = "";

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                if (cleanMessage.get(2)[1].equals("y")) {
                    url = cleanMessage.get(3)[1];
                }
                exit = true;
            }
        }
        return url;
    }


    public ArrayList<String> getEditors(String artistName)  throws RemoteException  {

            // Request  -> flag | id; type | getEditors; name | nnnn;
            // Rsponse  -> flag | id; type | getEditors; notif_count | n; Aname | nnn; Aname | nnn; [etc...];

            String uuid = UUID.randomUUID().toString();
            String id = uuid.substring(0, Math.min(uuid.length(), 8));
            String msg = "flag|"+id+";type|getEditors;name|" + artistName + ";";
            sendUDPDatagram(msg);
            boolean exit = false;
            ArrayList<String> editors = new ArrayList<>();

            while (!exit) {
                String rsp = receiveUDPDatagram(msg);
                ArrayList<String[]> cleanMessage = cleanTokens(rsp);

                if (cleanMessage.get(0)[1].equals(id)) {
                    int nEditors = Integer.parseInt(cleanMessage.get(2)[1]);

                    for (int i = 0; i < nEditors; i++) {
                       editors.add(cleanMessage.get(3 + i)[1]);
                        System.out.println();
                    }

                    exit = true;
                }
            }

            return editors;

        }


    /**
     * Request  -> flag | id; type | addart; name | nnnn; details | dddd; email | dddd;
     * Rsponse -> flag | id; type | addart; email | dddd; result | (y/n); notif_count | n; notif | email; notif | email; [etc...]; msg | mmmmm;
     *
     * Adds artist and notifies users if they that have edited the artist work in the past
     * @param artist
     * @param details
     * @param email
     * @return
     * @throws RemoteException
     */

    public String addArtist(String artist, String details, String email) throws RemoteException {


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
                if (rspToClient.equals("Artist info added with success")) {
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
                            /*
                            String uuidNotify = UUID.randomUUID().toString();
                            String idNotify = uuid.substring(0, Math.min(uuid.length(), 8));
                            sendUDPDatagram("flag|" + idNotify + ";type|notifyfail;email|" + userEmail + ";message|" + notification+";");
                            */
                        } catch (NullPointerException npe) {
                            /*
                            String uuidNotify = UUID.randomUUID().toString();
                            String idNotify = uuid.substring(0, Math.min(uuid.length(), 8));
                            sendUDPDatagram("flag|" + idNotify + ";type|notifyfail;email|" + userEmail + ";message|" + notification+";");
                            */
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

    /**
     *  Request  -> flag | id; type | addalb; art | aaaa; alb | bbbb; description | dddd; genre | gggg; email | dddd;
     *  Response -> flag | id; type | addalb; email | ddd; result |(y/n); notif_count | n; notif | email; notif | email; [etc...]; msg | mmmmm;
     * @param artist
     * @param albumTitle
     * @param description
     * @param genre
     * @param email
     * @return
     * @throws RemoteException
     */

    public String addAlbum(String artist, String albumTitle, String description, String genre, String launchDate, String editorLabel,  String email) throws  RemoteException{

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|addalb;art|" + artist + ";alb|" + albumTitle + ";description|" + description + ";genre|" + genre + ";email|" + email +
                ";launchDate|" + launchDate + ";editorLabel|" + editorLabel + ";", rspToClient = "";
        sendUDPDatagram(msg);
        boolean exit = false;

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                rspToClient = cleanMessage.get(cleanMessage.size()-2)[1];

                // Need to notify
                if (rspToClient.equals("Album info added with success")) {
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
                            /*
                            String uuidNotify = UUID.randomUUID().toString();
                            String idNotify = uuid.substring(0, Math.min(uuid.length(), 8));
                            sendUDPDatagram("flag|" + idNotify + ";type|notifyfail;email|" + userEmail + ";message|" + notification+";");
                            */
                        } catch (NullPointerException npe) {
                            /*
                            String uuidNotify = UUID.randomUUID().toString();
                            String idNotify = uuid.substring(0, Math.min(uuid.length(), 8));
                            sendUDPDatagram("flag|" + idNotify + ";type|notifyfail;email|" + userEmail + ";message|" + notification+";");
                            */
                        }
                    }
                }
                exit = true;

            }

        }
        return rspToClient;
    }

    /**
     * Request  -> flag | id; type | addmusic; alb | bbbb; title | tttt; track | n; email | dddd;
     * Response -> flag | id; type | addmusic; title | tttt; email | dddd; result | (y/n); notif_count | n; notif | email; notif | email; [etc...] msg | mmmmm;
     *
     * @param musicTitle
     * @param track
     * @param albumTitle
     * @param email
     * @return
     * @throws RemoteException
     */

    public String addMusic(String musicTitle, String track, String albumTitle , String email, String lyrics, String artistName) throws  RemoteException{


        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg =  "flag|"+id+";type|addmusic;alb|"+albumTitle+";title|"+musicTitle+";track|"+track+";email|"+email+";lyrics|" + lyrics + ";artistName|" + artistName + ";",rspToClient = "";
        sendUDPDatagram(msg);
        boolean exit = false;

        while(!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if(cleanMessage.get(0)[1].equals(id)) {
                rspToClient = cleanMessage.get(cleanMessage.size()-2)[1];

                // Try to notify
                if (rspToClient.equals("Music info added with success")) {
                    int numUsers = Integer.parseInt(cleanMessage.get(4)[1]);
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
                            /*
                            String uuidNotify = UUID.randomUUID().toString();
                            String idNotify = uuid.substring(0, Math.min(uuid.length(), 8));
                            sendUDPDatagram("flag|" + idNotify + ";type|notifyfail;email|" + userEmail + ";message|" + notification+";");
                            */
                        } catch (NullPointerException npe) {
                            /*
                            String uuidNotify = UUID.randomUUID().toString();
                            String idNotify = uuid.substring(0, Math.min(uuid.length(), 8));
                            sendUDPDatagram("flag|" + idNotify + ";type|notifyfail;email|" + userEmail + ";message|" + notification+";");
                            */
                        }
                    }
                }
                exit = true;
            }

        }
        return rspToClient;
    }

    /**
     * Method to be used between RMI Main and RMI Backup
     * The Backup will be constantly calling this method with the RMIMain's Interface
     *
     * @return
     * @throws RemoteException
     */

    public boolean isAlive() throws RemoteException {
        return true;
    }

    /**
     * RMI sends:
     * Request  -> flag | id; type | connectionrequest;
     *
     * Multicast responds:
     * Response -> flag | id; type | ack; hash | hhhh;
     *

     * @throws IOException
     */


    public void refreshMulticastHashes() throws IOException {

        boolean exit = false;
        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));
        RMIServer.sendUDPDatagramGeneral("flag|"+id+";type|connectionrequest;");
        RMIServer.multicastHashes.clear();
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
                ArrayList<String[]> cleanMessage = RMIServer.cleanTokens(message);


                if (cleanMessage.get(0)[1].equals(id)) {
                    RMIServer.multicastHashes.add(cleanMessage.get(2)[1]);
                }

                System.out.println("no fim deste método " + RMIServer.multicastHashes);
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


    public String associateDropBox() throws RemoteException{

        String urlAuth = "";

        return service.getAuthorizationUrl(null);
    }

    public String associateDropBoxBeforeLogin() throws RemoteException{

        String urlAuth = "";

        return serviceBeforeLogin.getAuthorizationUrl(null);
    }

    public String canLogin(String code) throws RemoteException {
        // flag | id; type | logindropbox; emaildropbox | eeee;
        // flag | id; type | logindropbox; rsp | y/n; email | eeee;

        String dropMusicEmail = "";

        // Get account_id and access_token from API
        OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/oauth2/token", serviceBeforeLogin);
        request.addParameter("code", code);
        request.addParameter("grant_type","authorization_code");
        request.addParameter("client_id","wbwulmkt4ykv4ry");
        request.addParameter("client_secret","n1kg0x7177alqbv");
        request.addParameter("redirect_uri", "http://localhost:8080/loginDropbox");
        Response response = request.send();
        JSONObject rj = (JSONObject) JSONValue.parse(response.getBody());
        String account_id = rj.get("account_id").toString();
        String acessToken = rj.get("access_token").toString();


        // Get email from user's Dropbox acc
        request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/users/get_account", serviceBeforeLogin);
        request.addHeader("Authorization", "Bearer " + acessToken);
        request.addHeader("Content-Type",  "application/json");
        request.addPayload("{\"account_id\": \"" + account_id + "\"}");
        response = request.send();
        rj = (JSONObject) JSONValue.parse(response.getBody());
        String emailDropbox = rj.get("email").toString();


        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|logindropbox;emaildropbox|"+emailDropbox+";";
        boolean exit = false;

        sendUDPDatagram(msg);

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                if(cleanMessage.get(2)[1].equals("y")){
                    dropMusicEmail = cleanMessage.get(3)[1]; // get email from multicast to identify user
                } else {
                    dropMusicEmail = "null";
                }
                exit = true;
            }
        }

        return dropMusicEmail;
    }

    public boolean setToken(String email, String code) throws RemoteException {

        // flag | id; type | token; token | tttt; email | eeee; emaildropbox | eeee;
        // flag | id; rsp | y/n;

        boolean r = false;


        try {


            // get account email and token
            OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/oauth2/token", service);
            request.addParameter("code", code);
            request.addParameter("grant_type","authorization_code");
            request.addParameter("client_id","38rui0xr3sccsdp");
            request.addParameter("client_secret","tp9cimdal84i23l");
            request.addParameter("redirect_uri", "http://localhost:8080/associateDropBoxTokenAction");
            Response response = request.send();
            JSONObject rj = (JSONObject) JSONValue.parse(response.getBody());
            String account_id = rj.get("account_id").toString();
            String acessToken = rj.get("access_token").toString();

            // Get email from user's Dropbox acc
            request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/users/get_account", service);
            request.addHeader("Authorization", "Bearer " + acessToken);
            request.addHeader("Content-Type",  "application/json");
            request.addPayload("{\"account_id\": \"" + account_id + "\"}");
            response = request.send();
            rj = (JSONObject) JSONValue.parse(response.getBody());
            String emailDropbox = rj.get("email").toString();


            String uuid = UUID.randomUUID().toString();
            String id = uuid.substring(0, Math.min(uuid.length(), 8));

            String msg = "flag|"+id+";type|token;token|" + acessToken + ";email|" + email + ";emaildropbox|"+emailDropbox+";", rspToClient = "";
            boolean exit = false;

            sendUDPDatagram(msg);

            while (!exit) {
                String rsp = receiveUDPDatagram(msg);
                ArrayList<String[]> cleanMessage = cleanTokens(rsp);

                if (cleanMessage.get(0)[1].equals(id)) {
                    if(cleanMessage.get(1)[1].equals("y")){
                        r = true;
                    } else {
                        r = false;
                    }
                    exit = true;
                }
            }

        } catch(OAuthException e) {
            e.printStackTrace();
            r = false;
        }

        return r;
    }

    public String getTokenFromMulticast(String email) {

        // flag | id; type | getToken; email | eeee;
        // flag | id; type | getToken; result | y/n; token | ttttt;

        String uuid = UUID.randomUUID().toString();
        String id = uuid.substring(0, Math.min(uuid.length(), 8));

        String msg = "flag|"+id+";type|getToken;email|"+email+";";
        boolean exit = false;
        String token = "";

        sendUDPDatagram(msg);

        while (!exit) {
            String rsp = receiveUDPDatagram(msg);
            ArrayList<String[]> cleanMessage = cleanTokens(rsp);

            if (cleanMessage.get(0)[1].equals(id)) {
                if(cleanMessage.get(2)[1].equals("y")){
                    token = cleanMessage.get(3)[1]; // get token
                } else {
                    token = "null";
                }
                exit = true;
            }
        }

        return token;
    }

    public boolean associateMusic(String email, String artist, String album, String musicTitle, String fileName) throws RemoteException {



        // get acess Token of email from multicast
        String userToken = getTokenFromMulticast(email);

        JSONObject j = new JSONObject();
        j.put("path","/DropMusic/" + fileName);


        // get link in DropBox to fileName
        // /create_shared_link_with_settings
        OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropboxapi.com/2/sharing/create_shared_link_with_settings", service);
        request.addHeader("Authorization", "Bearer " + userToken);
        request.addHeader("Content-Type",  "application/json");
        request.addPayload(j.toJSONString());
        Response response = request.send();

        JSONObject rj = (JSONObject) JSONValue.parse(response.getBody());
        String url = rj.get("url").toString();

        System.out.println("URL: " + url);


        return true;

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

            System.getProperties().put("java.security.policy", "policy.all");
            System.setSecurityManager(new SecurityManager());


            try {
                // try to bind, if it fails that means that an shared.RMIServer is already running
                r = LocateRegistry.createRegistry(1099);
                r.bind("rmiserver", rmiServer);

            } catch (ExportException ree) {
                RMIServerInterface mainServerInterface;
                System.out.println("Starting RMIBackup");

                while (!takeOver) {
                    try {

                        try {
                            // lookup the interface and ping it
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

                            if (failCount == 5 && failedLastTime) { // mainRMI is down
                                failCount = 0;
                                takeOver = true;
                                r = LocateRegistry.createRegistry(1099); // create new registry
                            }
                            failedLastTime = true;
                        }

                        sleep((long) (500));

                    } catch (InterruptedException Et) {
                        System.out.println(Et);
                    }

                }
            }

            // rebind is necessary
            // when run by mainRMI it has no effect bc/ mainRMI is already bound
            // when run by BackUP it binds the the registry r (above)
            r.rebind("rmiserver", rmiServer);
            System.out.println("Taking over RMIMain");


            // this multicast message tells every online Multicast to identify themselves
            // the multicast send a packet with their Hash-code
            // Request  -> flag | id; type | connectionrequest;
            q = "flag|s;type|connectionrequest;";
            RMIServer.sendUDPDatagramGeneral(q);


        } catch (RemoteException re) {
            System.out.println("Exception in shared.RMIServer.main: " + re);
        }

        while(true) {
            // waiting for MulticastServers to group up
            // Response -> flag | id; type | ack; hash | hhhh;


            byte[] buffer = new byte[65536];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message: " + message);
            ArrayList<String[]> cleanMessage = RMIServer.cleanTokens(message);

            if (cleanMessage.get(0)[1].equals("r") && cleanMessage.get(1)[1].equals("ack"))
                if (!RMIServer.multicastHashes.contains(cleanMessage.get(2)[1]))
                    RMIServer.multicastHashes.add(cleanMessage.get(2)[1]);


        }

    }
}
