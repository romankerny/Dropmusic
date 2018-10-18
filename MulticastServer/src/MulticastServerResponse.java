import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.Iterator;

public class MulticastServerResponse extends Thread {

    private DatagramPacket packet;
    private MulticastSocket sendSocket = null; // socket to respond to Multicast group
    private int SEND_PORT = 5214;
    private int TCPPort = 5252;
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


    public void uploadMusic(String title, String email) throws IOException, ClassNotFoundException {
        // flag | s; type | requestTCPConnection; operation | upload; email | eeee;
        // flag | r; type | requestTCPConnection; operation | upload; email | eeee; result | y; port | pppp;

        sendResponseMulticast("flag|r;type|requestTCPConnection;operation|upload;email|"+email+";result|y;port|"+TCPPort+";");
        Iterator iArtists = artists.iterator();
        System.out.println("A dar upload de musica "+title);

        ServerSocket serverSocker = new ServerSocket(TCPPort);
        Socket client = null;
        System.out.println("antes do bind");
        // serverSocker.bind(new InetSocketAddress(TCPPort));
        System.out.println("depois do bind");

        while(iArtists.hasNext()) {
            Artist a = (Artist) iArtists.next();
            Iterator iAlbum = a.albums.iterator();

            while(iAlbum.hasNext()) {
                Album alb = (Album) iAlbum.next();
                Iterator iMusic = alb.tracks.iterator();

                while(iMusic.hasNext()) {
                    Music m = (Music) iMusic.next();
                    if(m.title.equals(title)) {

                        System.out.println("socket is bound");
                        client = serverSocker.accept();

                        DataInputStream in = new DataInputStream(client.getInputStream());

                        // Filename and size first
                        String filename = in.readUTF();
                        long size = in.readLong();

                        byte[] buffer = new byte[(int)size];
                        int count;

                        // Ler todos os bytes
                        while((count = in.read(buffer)) != -1);

                        m.musicFiles.put(email, new MusicFile(filename, buffer));

                        in.close();
                        serverSocker.close();

                    }
                }
            }
        }
    }

    public void downloadMusic(String title, String uploader, String email) throws IOException {
        // Request  -> flag | s; type | requestTCPConnection; operation | download; title | tttt; uploader | uuuu; email | eeee;
        // Response -> flag | r; type | requestTCPConnection; operation | download; email | eeee; result | y; port | pppp;

        sendResponseMulticast("flag|r;type|requestTCPConnection;operation|download;email|"+email+";result|y;port|"+TCPPort+";");
        Iterator iArtists = artists.iterator();
        System.out.println("A dar download de musica "+title);

        ServerSocket serverSocket = new ServerSocket(TCPPort);
        Socket client = null;

        while(iArtists.hasNext()) {
            Artist a = (Artist) iArtists.next();
            Iterator iAlbum = a.albums.iterator();

            while(iAlbum.hasNext()) {
                Album alb = (Album) iAlbum.next();
                Iterator iMusic = alb.tracks.iterator();

                while(iMusic.hasNext()) {
                    Music m = (Music) iMusic.next();
                    if(m.title.equals(title)) {
                        if (m.musicFiles.get(uploader).emails.contains(email)) {
                            client = serverSocket.accept();
                            DataOutputStream out = new DataOutputStream(client.getOutputStream());
                            MusicFile mf = m.musicFiles.get(uploader);

                            // Send filename before raw data
                            out.writeUTF(mf.filename);
                            out.write(mf.rawData);
                        } else {
                            System.out.println("Nao tem permission");
                        }


                    }
                }
            }
        }
    }

    public void share(String title, String shareTo, String uploader) {
        // Request  -> flag | s; type | share; title | tttt; shareTo | sssss; uploader | uuuuuu;
        // Response -> flag | r; type | share; result | (y/n); title | ttttt; shareTo | ssssss; uploader | uuuuu;

        String rsp;
        boolean found = false;
        Iterator iArtists = artists.iterator();

        while(iArtists.hasNext()) {
            Artist a = (Artist) iArtists.next();
            Iterator iAlbum = a.albums.iterator();

            while(iAlbum.hasNext()) {
                Album alb = (Album) iAlbum.next();
                Iterator iMusic = alb.tracks.iterator();

                while(iMusic.hasNext()) {
                    Music m = (Music) iMusic.next();
                    if(m.title.equals(title)) {

                        if(m.musicFiles.containsKey(uploader)) {
                            found = true;
                            m.musicFiles.get(uploader).shareWith(shareTo);
                        }

                    }
                }
            }
        }

        if (found) {
            rsp = "flag|r;type|share;result|y;title|"+title+";shareTo|"+shareTo+";uploader|"+uploader+";";
        } else {
            rsp = "flag|r;type|share;result|n;title|"+title+";shareTo|"+shareTo+";uploader|"+uploader+";";
        }
        sendResponseMulticast(rsp);
    }

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
                if(al.title.equals(albumName)) {
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
        // flag | r; type | privilege; result | (y/n); user1 | username; user2 | username;

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

        sendResponseMulticast(rsp);
    }



    public void getDetails(String type, String keyword) {
        // Request  -> flag | s; type | search; param | (art, gen, tit); keyword | kkkk;
        // Response -> flag | r; type | search; param | (art, gen, tit); keyword | kkkk; item_count | n; item_x_name | name; [...]

        // AINDA NAO TESTEI ESTE MÉTODO
        // Roman: Alterei o metodo para dar num caso especifico (1 artista), precisa ser generalizado

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
                        if (al.title.equals(keyword)) {
                            response = al.toString();
                            exit = true;
                            result = 'y';
                        }
                    }
                }
            }
        }

        rsp = "flag|r;type|details;result|" +result+";param|"+type+";keyword|"+keyword+";item_count|1;item_x_name|"+response+";";
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
                    if(alb.title.equals(keyword)) {
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
            } else if(cleanMessage.get(1)[1].equals("share")) {
                share(cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1]); // (title, shareTo, uploader)
            } else if(cleanMessage.get(1)[1].equals("requestTCPConnection") && cleanMessage.get(2)[1].equals("upload")) {
                // flag | s; type | requestTCPConnection; operation | upload; tittle | tttt; email | eeee;

                try {
                    System.out.println("A chamar o metodo de upload");
                    uploadMusic(cleanMessage.get(3)[1], cleanMessage.get(4)[1]); // (title, email)
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if(cleanMessage.get(1)[1].equals("requestTCPConnection") && cleanMessage.get(2)[1].equals("download")) {
               // Request  -> flag | s; type | requestTCPConnection; operation | download; title | tttt; uploader | uuuu; email | eeee
                try {
                    System.out.println("A chamar o metodo de download");
                    downloadMusic(cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(5)[1]); // (title, uploader, email)
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Invalid protocol message");
            }

        }
    }


}
