import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.Iterator;

public class MulticastServerResponse extends Thread {

    private DatagramPacket packet;
    private MulticastSocket sendSocket = null; // socket to respond to Multicast group
    private int SEND_PORT = 5214;
    private String MULTICAST_ADDRESS;
    private CopyOnWriteArrayList<User> users;
    private CopyOnWriteArrayList<Artist> artists;

    MulticastServerResponse(DatagramPacket packet, String ip, CopyOnWriteArrayList<User> users, CopyOnWriteArrayList<Artist> artists) {

        this.packet = packet;
        MULTICAST_ADDRESS = ip;
        this.users = users;
        this.artists = artists;
    }

    public void sendResponseMulticast(String resp) {

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

    ArrayList cleanTokens(String msg) {

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

    // -----------------------------------------------------------------------------------------------------------------

    public void register(String email, String password) {


        // Verificar se existe
        String rsp;
        boolean found = false;
        Iterator iUser = users.iterator();

        while(iUser.hasNext()) {
            User s = (User) iUser.next();
            if(s.email.equals(email)) {
                found = true;
            }
        }

        if(found == true) {
            rsp = "flag|r;type|register;result|n;flag|r;username|" + email + ";password|" + password + ";";
            System.out.println("User " + email + " already has an acc.");
        } else {

            // Fazer registo e adicionar a BD o novo user
            User s = new Regular(email, password);
            this.users.add(s);
            System.out.println("Gonna register " + email + " with password " + password);
            rsp = "flag|r;type|register;result|y;username|" + email + ";password|" + password + ";";
        }
        sendResponseMulticast(rsp);
    }

    public void login(String email, String password) {
        // Request  -> flag | s; type | login; email | eeee; password | pppp;
        // Response -> flag | r; type | login; resutl | (y/n); email | eeee; password | pppp;

        Iterator iUsers = users.iterator();
        String rsp = "flag|r;type|login;result|n;email|" + email + ";password|" + password + ";";

        while (iUsers.hasNext()) {
            User s = (User) iUsers.next();
            if (s.email.equals((email)) & s.password.equals(password)) {
                s.login();
                // enviar notificações

                Iterator iMessages = s.notifications.iterator();
                while(iMessages.hasNext()) {
                    String notification = (String) iMessages.next();
                    sendResponseMulticast("flag|r;type|notify;message|" + notification + ";user_count|1;user_0_email|" + email + ";");
                }


                rsp = "flag|r;type|login;result|y;email|" + email + ";password|" + password + ";";
                System.out.println(email + " logged in");
            }
        }
        sendResponseMulticast(rsp);
    }

    public void logout(String email) {
        // Request  -> flag | s; type | logout; email | eeee;
        // Response -> flag | r; type | logout; result | (y/n); email | eeee;

        Iterator iUsers = users.iterator();
        String rsp = "flag|r;type|logout;result|n;email| " + email + ";";

        while (iUsers.hasNext()) {
            User s = (User) iUsers.next();
            if(s.email.equals(email)){
                s.logout();
                rsp = "flag|r;type|logout;result|y;email| " + email + ";";
            }
        }

        sendResponseMulticast(rsp);
    }

    public void writeCritic(String albumName, String critic, String rate, String email) {

        // flag | s; type | critic; album | name; critic | blabla; rate | n email | eeee;
        // flag | r; type | critic; result | (y/n); album | name; critic | blabla; rate | n; email | eeee;

        Iterator iArtists = artists.iterator();
        boolean exit = false;
        String rsp = "flag|r;type|critic;result|n;album|" + albumName + ";critic|" + critic +";rate|" + rate + ";";

        while (iArtists.hasNext() & !exit) {
            Artist a = (Artist) iArtists.next();
            Iterator iAlbum = a.albums.iterator();

            while(iAlbum.hasNext()) {
                Album al = (Album) iAlbum.next();
                if(al.tittle.replaceAll(" ", "").equals(albumName)) {
                    // flag | r; type | notify; message | mmmmmmm; user_count | n; user_x_email | email; [...]
                    String ts = "flag|r;type|notify;message|" + albumName + " has a new review by " + email + ";";
                    int n = al.nCritics;
                    ts += "user_count|" + n + ";";
                    Iterator iReview = al.reviews.iterator();
                    int i = 0;
                    while(iReview.hasNext()) {
                        Review r = (Review) iReview.next();
                        ts += "user_" + i + "_email|" + r.email + ";";
                        i++;
                    }
                    sendResponseMulticast(ts);
                    al.addCritic(critic, Integer.parseInt(rate), email);
                    rsp = "flag|r;type|critic;result|y;album|" + albumName + ";critic|" + critic +";rate|" + rate + ";";
                    exit = true;
                }
            }
        }

        sendResponseMulticast(rsp);
    }

    public void offUserNotified(String email, String message) {
        Iterator iUsers = users.iterator();

        while (iUsers.hasNext()) {
            User offUser = (User) iUsers.next();
            offUser.logout();
            offUser.addNotification(message);
            System.out.println("Set user: " + email + " off");
        }
    }

    public void turnIntoEditor(String user1, String user2) {

        // flag | s; type | privilege; user1 | username; user2; username;
        // flag | r; type | privilege; result | (y/n): user1 | username; user2 | username;

        Iterator iUsers1 = users.iterator();
        Iterator iUsers2 = users.iterator();
        User regularUser = null;
        String rsp = "flag|r;type|privilege;result|n:user1|" + user1 +";user2|" + user2 + ";";

        boolean exit = false;

        while(iUsers2.hasNext() & !exit) {
            regularUser = (User) iUsers2.next();
            if(regularUser.email.equals(user2) & regularUser.getType().equals("regular")) {
                exit = true;
            }
        }

        exit = false;

        while (iUsers1.hasNext() & !exit) {
            User s = (User) iUsers1.next();
            if (s.email.equals(user1) && s.getType().equals("editor")) {
                    users.remove(regularUser);
                    Editor ed = new Editor(regularUser.email, regularUser.password);
                    users.add(ed);
                    exit = true;
                    rsp = "flag|r;type|privilege;result|y;user1|" + user1 +";user2|" + user2 + ";";
                    // flag | r; type | notify; user_count | n; user_x_email | email; [...] message | mmmmmmm;
                    sendResponseMulticast("flag|r;type|notify;message|You got promoted to Editor;user_count|1;user_0_email|" + ed.email + ";");
            }
        }



        for(User s : users)
            System.out.println(s.toString());

        sendResponseMulticast(rsp);
    }



    public void getDetails(String type, String keyword) {
        // flag | s; type | details; param | (art, alb); keyword | kkkk;
        // flag | r; type | details; param | (art, alb); keyword | kkkk; result | (y/n); response | blablablabla;

        // AINDA NAO TESTEI ESTE MÉTODO

        Iterator iArtists = artists.iterator();
        String rsp = " ", response = " ";
        boolean exit = false;
        char result = 'n';


        while (iArtists.hasNext() & !exit) {
            Artist a = (Artist) iArtists.next();

            if(type.equals("art")) {
                if (a.name.equals(keyword)) {
                    response = a.toString();
                    exit = true;
                    result = 'y';
                } else if(type.equals("alb")){
                    Iterator iAlbum = a.albums.iterator();
                    while(iAlbum.hasNext()) {
                        Album al = (Album) iAlbum.next();
                        if (al.tittle.equals(keyword)) {
                            response = al.toString();
                            exit = true;
                            result = 'y';
                        }
                    }
                }
            }
        }

        rsp = "flag|s;type|details;param|" +type+";keyword|"+keyword+";result|"+result+";response| "+ response+";";
        sendResponseMulticast(rsp);

    }

    public void addDetail(String gen, String keyword, String detail, String email) {
        // Request  -> flag | s; type | detail; gen | (art, album); keyword | vvvvv; detail | ddddddddddddddddddd; email | eeee;
        // Response -> flag | r; type | detail; gen | (art, album); keyword | vvvvv; response | (y/n); email | eeee;

        Iterator iUsers = users.iterator();
        User editor = null;

        while (iUsers.hasNext()) {
            User s = (User) iUsers.next();
            if (s.email.equals((email))) {
                editor = s;
            }
        }

        if(gen.equals("art")) {

            Iterator iArtists = artists.iterator();

            while (iArtists.hasNext()) {
                Artist a = (Artist) iArtists.next();
                if(a.name.equals(keyword)) {
                    a.setDetails(detail, editor);
                }
            }

        } else if(gen.equals(("album"))) {

            Iterator iArtists = artists.iterator();


            while (iArtists.hasNext()) {
                Artist a = (Artist) iArtists.next();
                Iterator iAlbum = a.albums.iterator();
                while(iAlbum.hasNext()) {
                    Album alb = (Album) iAlbum.next();
                    if(alb.tittle.equals(keyword)) {
                        alb.setDetails(detail, editor);
                    }
                }
            }

        }

    }



    public void run() {


        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:" + message);

        // Decode message
        ArrayList<String[]> cleanMessage = cleanTokens(message); // if the packet contains "flag | s" the server has to respond


        if (cleanMessage.get(0)[1].equals("s")) {


            if (cleanMessage.get(1)[1].equals("register")) { //register
                register(cleanMessage.get(2)[1], cleanMessage.get(3)[1]);    // (email, password)

            } else if (cleanMessage.get(1)[1].equals("login")) { // login
                login(cleanMessage.get(2)[1], cleanMessage.get(3)[1]); // (email, password)

            } else if (cleanMessage.get(2)[1].equals("logout")) { // logout
                logout(cleanMessage.get(2)[1]);  // (email)

            } else if (cleanMessage.get(1)[1].equals("details")) { // search Artist, Album, Music
                getDetails(cleanMessage.get(2)[1], cleanMessage.get(3)[1]); // (Artist or Album, keyword)

            } else if(cleanMessage.get(1)[1].equals("critic")) {            // add critic to album
                writeCritic(cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(5)[1]);// (album, critic, rate, email)

            } else if(cleanMessage.get(1)[1].equals("privilege")) {
                System.out.println("para editor");
                turnIntoEditor(cleanMessage.get(2)[1], cleanMessage.get(3)[1]);       // (Editor, regularToEditor)
            } else if(cleanMessage.get(1)[1].equals("notify")) {
                offUserNotified(cleanMessage.get(4)[1], cleanMessage.get(2)[1]);    // (email, message)

            } else {
                System.out.println("Invalid protocol message");
            }

        }
    }


}
