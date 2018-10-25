import com.mysql.fabric.Server;
import java.io.*;
import java.net.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.Iterator;

public class MulticastServerResponse extends Thread {

    private DatagramPacket packet;
    private String hashCode;
    private MulticastSocket sendSocket = null; // socket to respond to Multicast group
    private int SEND_PORT = 5214;
    private String MULTICAST_ADDRESS;
    private CopyOnWriteArrayList<User> users;
    private CopyOnWriteArrayList<Artist> artists;


    MulticastServerResponse(DatagramPacket packet, String ip, CopyOnWriteArrayList<User> users, CopyOnWriteArrayList<Artist> artists, String code) {

        this.packet = packet;
        MULTICAST_ADDRESS = ip;
        this.users = users;
        this.artists = artists;
        this.hashCode = code;
    }

    public void sendResponseMulticast(String resp, String code) {

        if(this.hashCode.equals(code)) {
            // only the designated Multicast Server will respond to RMIServer
            try {
                MulticastSocket socket = new MulticastSocket();
                byte[] buffer = (resp+"hash|" + hashCode + ";").getBytes();
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, SEND_PORT);
                socket.send(packet);

            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public ServerSocket getSocket() {

        boolean exit = false;
        ServerSocket serverSocket= null;
        int TCPPort;

        while(!exit) {
            TCPPort = ((int)(Math.random()*1000)) + 5000;
            System.out.println("New port: "+TCPPort);
            try {
                serverSocket = new ServerSocket(TCPPort);
                exit = true;
            } catch (IOException v) {
            }
        }
        return serverSocket;
    }

    public void uploadMusic(String id, String title, String email, String code) throws IOException, ClassNotFoundException {
        // flag | s; type | requestTCPConnection; operation | upload; email | eeee;
        // flag | r; type | requestTCPConnection; operation | upload; email | eeee; result | y; ip | ip; port | pppp;
        if(this.hashCode.equals(code)) {
            Iterator iArtists = artists.iterator();
            System.out.println("A dar upload de musica " + title);
            ServerSocket serverSocket = getSocket();
            Socket client = null;

            String ip = InetAddress.getLocalHost().getHostAddress();
            int port = serverSocket.getLocalPort();

            sendResponseMulticast("flag|"+id+";type|requestTCPConnection;operation|upload;email|"+email+";result|y;ip|"+ip+";port|"+port+";", code);


            while (iArtists.hasNext()) {
                Artist a = (Artist) iArtists.next();
                Iterator iAlbum = a.albums.iterator();

                while (iAlbum.hasNext()) {
                    Album alb = (Album) iAlbum.next();
                    Iterator iMusic = alb.tracks.iterator();

                    while (iMusic.hasNext()) {
                        Music m = (Music) iMusic.next();
                        if (m.title.equals(title)) {

                            System.out.println("socket is bound");
                            client = serverSocket.accept();

                            DataInputStream in = new DataInputStream(client.getInputStream());

                            // Filename and size first
                            String filename = in.readUTF();
                            long size = in.readLong();

                            byte[] rawData = new byte[(int) size];
                            // Ler todos os bytes
                            in.readFully(rawData);

                            m.musicFiles.put(email, new MusicFile(filename, rawData));
                            m.musicFiles.get(email).emails.add(email);

                            in.close();
                            serverSocket.close();

                        }
                    }
                }
            }
            ObjectFiles.writeArtistsToMemory(artists);
            System.out.println("Upload of " + title+ " done" );
        }

    }

    public void downloadMusic(String id, String title, String uploader, String email, String code) throws IOException {
        // Request  -> flag | s; type | requestTCPConnection; operation | download; title | tttt; uploader | uuuu; email | eeee;
        // Response -> flag | r; type | requestTCPConnection; operation | download; email | eeee; result | y; ip | ip; port | pppp;
        // alterar p so responder quem encontrar musica
        // ID title uploader email hash

        System.out.println("NO DOWNLOAD!");
        Music msc = searchMusic(title, uploader);

        if(msc == null) {
            sendResponseMulticast("flag|"+id+";type|requestTCPConnection;operation|download;email|"+email+";result|n;", code);
            return;
        }
        System.out.println("tenho a musica posso deixar fazer download!");

        Iterator iArtists = artists.iterator();
        System.out.println("A dar download de musica "+title);

        ServerSocket serverSocket = getSocket();
        String ip = InetAddress.getLocalHost().getHostAddress();
        int port = serverSocket.getLocalPort();
        Socket client = null;

        sendResponseMulticast("flag|"+id+";type|requestTCPConnection;operation|download;email|"+email+";result|y;ip|"+ip+";port|"+port+";", code);

        if(this.hashCode.equals(code)) {
            while (iArtists.hasNext()) {
                Artist a = (Artist) iArtists.next();
                Iterator iAlbum = a.albums.iterator();

                while (iAlbum.hasNext()) {
                    Album alb = (Album) iAlbum.next();
                    Iterator iMusic = alb.tracks.iterator();

                    while (iMusic.hasNext()) {
                        Music m = (Music) iMusic.next();
                        if (m.title.equals(title)) {

                            for (String s : m.musicFiles.get(uploader).emails)
                                System.out.println("Pode fazer download: " + s);

                            if (m.musicFiles.get(uploader).emails.contains(email)) {
                                client = serverSocket.accept();
                                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                                MusicFile mf = m.musicFiles.get(uploader);

                                // Send filename before raw data
                                out.writeUTF(mf.filename);
                                out.write(mf.rawData);
                                out.close();
                                serverSocket.close();
                                System.out.println(" a bazar do download ");
                            } else {
                                System.out.println("Nao tem permission");
                            }


                        }
                    }
                }
            }
        }
        ObjectFiles.writeArtistsToMemory(artists);
    }

    public void share(String id, String title, String shareTo, String uploader, String code) {
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
                            //System.out.println(m.musicFiles.get(uploader).emails.size());
                        }

                    }
                }
            }
        }
        ObjectFiles.writeArtistsToMemory(artists);
        ObjectFiles.writeUsersToMemory(users);

        if (found) {
            rsp = "flag|"+id+";type|share;result|y;title|"+title+";shareTo|"+shareTo+";uploader|"+uploader+";";
        } else {
            rsp = "flag|"+id+";type|share;result|n;title|"+title+";shareTo|"+shareTo+";uploader|"+uploader+";";
        }
        sendResponseMulticast(rsp, code);
    }

    public void register(String id, String email, String password, String code) {

        String message;
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

        if(found) {
            message = "User "+email+" is already registered";
            rsp = "flag|"+id+";type|register;result|n;username|" + email + ";password|" + password + ";"+"msg|"+message+";";
            System.out.println("User " + email + " already has an acc.");
        } else {
            // Fazer registo e adicionar a BD o novo user
            User s = new User(email, password);
            this.users.add(s);
            rsp = "flag|"+id+";type|register;result|y;username|" + email + ";password|" + password + ";";
            ObjectFiles.writeUsersToMemory(users);
        }
        sendResponseMulticast(rsp, code);
    }

    public void login(String id, String email, String password, String code) {
        // Request  -> flag | s; type | login; email | eeee; password | pppp;
        // Response -> flag | r; type | login; result | (y/n); email | eeee; password | pppp; notification_count | n; notif_x | notif; msg | mmmmmm;
        String rsp = "flag|"+id+";type|login;result|n;email|" + email + ";password|" + password + ";msg|Incorrect user/password;";

        Iterator iUsers = users.iterator();
        User u = null;
        boolean found = false;

        while (iUsers.hasNext() && !found) {
            u = (User) iUsers.next();
            if (u.email.equals((email)) & u.password.equals(password)) {
                found = true;
            }
        }

        if (found) {
            rsp = "flag|"+id+";type|login;result|y;email|" + email + ";password|" + password + ";";

            // Concatenate notifications to `rsp` if they exist
            rsp += "notification_count|"+u.notifications.size()+";";

            Iterator iMessages = u.notifications.iterator();
            while(iMessages.hasNext()) {
                rsp += "notif|" + (String) iMessages.next() + ";";

            }

            System.out.println(email + " logged in");
        }
        sendResponseMulticast(rsp, code);
    }

    public void turnIntoEditor(String id, String user1, String user2, String code) {

        // flag | s; type | privilege; user1 | username; user2; username;
        // flag | r; type | privilege; result | (y/n): user1 | username; user2 | username; msg | mmmmmmmm;
        String rsp = "flag|"+id+";type|privilege;result|n:user1|" + user1 +";user2|" + user2 + ";";

        Iterator iUsers1 = users.iterator();
        Iterator iUsers2 = users.iterator();
        boolean found1 = false;
        boolean found2 = false;

        // Find editor and check if he is actually an editor
        while (iUsers1.hasNext() & !found1) {
            User s = (User) iUsers1.next();
            if(s.email.equals(user1) && s.isEditor())
                found1 = true;

        }
        if (found1) {
            // Find regular user
            while (iUsers2.hasNext() && !found2) {
                User s = (User) iUsers2.next();
                if (s.email.equals(user2)) {
                    s.becomeEditor();
                    found2 = true;
                    rsp = "flag|"+id+";type|privilege;result|y:user1|" + user1 +";user2|" + user2 + ";";
                }
            }
            if (!found2)
                rsp += "msg|Couldn't find user to promote;";
        } else {
            rsp += "msg|You have to be an Editor to use /promote;";
        }

        ObjectFiles.writeUsersToMemory(users);

        sendResponseMulticast(rsp, code); // -> RMIServer
    }

    public void writeCritic(String id, String albumName, String critic, String rate, String email, String code) {

        // flag | s; type | critic; album | name; critic | blabla; rate | n email | eeee;
        // flag | r; type | critic; result | (y/n); album | name; critic | blabla; rate | n; email | eeee;

        Iterator iArtists = artists.iterator();
        boolean found = false;
        String rsp;

        while (iArtists.hasNext() & !found) {
            Artist a = (Artist) iArtists.next();
            Iterator iAlbum = a.albums.iterator();

            while(iAlbum.hasNext()) {
                Album al = (Album) iAlbum.next();
                if(al.title.equals(albumName)) {
                    al.addCritic(critic, Integer.parseInt(rate), email);
                    found = true;
                }
            }
        }
        if (found)
            rsp = "flag|"+id+";type|critic;result|y;album|" + albumName + ";critic|" + critic +";rate|" + rate + ";";
        else
            rsp = "flag|"+id+";type|critic;result|n;album|" + albumName + ";critic|" + critic +";rate|" + rate + ";"+"msg|Couldn't find album `"+albumName+"`;";

        ObjectFiles.writeArtistsToMemory(artists);
        sendResponseMulticast(rsp, code);
    }

    public void offUserNotified(String id, String email, String message) {
        Iterator iUsers = users.iterator();

        while (iUsers.hasNext()) {
            User offUser = (User) iUsers.next();

            if (offUser.email.equals(email)) {
                offUser.addNotification(message);
                //System.out.println("Set user: " + email + " off");
            }
        }
        ObjectFiles.writeUsersToMemory(users);
    }

    public void getDetails(String id, String type, String keyword, String code) {
        // Request  -> flag | s; type | search; param | (art, alb, gen); keyword | kkkk;
        // Response -> flag | r; type | search; param | (art, alb, gen); keyword | kkkk; item_count | n; item_x_name | name; [...]

        Iterator iArtists = artists.iterator();
        String rsp, response = "";
        String message= " ";
        boolean found = false;
        char result = 'n';


        while (iArtists.hasNext()) {
            Artist a = (Artist) iArtists.next();
            if (type.equals("art")) {
                if (a.name.equals(keyword)) {
                    response = a.toString();
                    found = true;
                    result = 'y';
                }
            } else if (type.equals("alb") || type.equals("gen")) {
                Iterator iAlbums = a.albums.iterator();
                while (iAlbums.hasNext()) {
                    Album al = (Album) iAlbums.next();
                    System.out.println("Album " + al.title);
                    if (al.title.equals(keyword)) {
                        response = a.toString();
                        found = true;
                        result = 'y';
                    }
                    if (al.genre.equals(keyword)) {
                        response += al.toString();
                        result = 'y';
                        found = true;
                    }
                }
            }

        }

        if (!found)
            message = "Couldn't find `"+keyword+"` in database";

        rsp = "flag|"+id+";type|details;result|"+result+";param|"+type+";keyword|"+keyword+";item_count|1;item_x_name|"+response+";msg|"+message+";";
        sendResponseMulticast(rsp, code);

    }

    public void addDetail(String id, String gen, String keyword, String detail, String email) {
        // Request  -> flag | s; type | detail; gen | (art, album); keyword | vvvvv; detail | ddddddddddddddddddd; email | eeee;
        // Response -> flag | r; type | detail; gen | (art, album); keyword | vvvvv; response | (y/n); email | eeee;
        // TODO
        Iterator iUsers = users.iterator();
        User editor = null;

        while (iUsers.hasNext()) {
            User s = (User) iUsers.next();
            if (s.email.equals(email) && s.isEditor()) {
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

        ObjectFiles.writeUsersToMemory(users);
        ObjectFiles.writeArtistsToMemory(artists);

    }

    public void addArtist(String id, String name, String details, String email, String code) {
        // Request  -> flag | s; type | addart; name | nnnn; details | dddd; email | dddd;
        // Response -> flag | r; type | addart; email | dddd; result | (y/n);

        String rsp = "flag|"+id+";type|addart;email|"+email+";result|n;msg|Only an Editor can add a new artist;";
        boolean alreadyExists = false;
        Iterator iUsers = users.iterator();
        Iterator iArtists = artists.iterator();

        while(iArtists.hasNext() && !alreadyExists){
            Artist a = (Artist) iArtists.next();
            if (a.name.equals(name))
                alreadyExists = true;

        }

        if (alreadyExists) {
            rsp = "flag|"+id+";type|addart;email|"+email+";result|n;msg|"+name+" already exists in database;";
        } else {
            while (iUsers.hasNext()) {
                User u = (User) iUsers.next();
                if (u.email.equals(email) && u.isEditor()) {
                    this.artists.add(new Artist(name, details));
                    rsp = " flag|" + id + ";type|addart;email|" + email + ";result|y;";
                }
            }
        }
        ObjectFiles.writeArtistsToMemory(artists);
        sendResponseMulticast(rsp, code);
    }

    public Music searchMusic(String musicTitle, String uploader) {
        // Request  -> flag | id; type | search; music | musicTitle;
        // Response -> flag | id; type | search; result | (y/n); msg | mmmmm;

        for(Artist a : artists) {
            for(Album b : a.albums) {
                for(Music m : b.tracks) {
                    if(m.title.equals(musicTitle) && m.musicFiles.containsKey(uploader)) {
                        System.out.println("Yes!");
                        return m;
                    }
                }
            }
        }

        return null;
    }

    public void addAlbum(String id, String artName, String albName, String description, String genre, String email, String code) {
        // Request  -> flag | s; type | addalb; art | aaaa; alb | bbbb; description | dddd; genre | gggg; email | dddd;
        // Response -> flag | r; type | addalb; email | ddd; result |(y/n); |
        String rsp = "flag|"+id+";type|addalb;email|"+email+";result|n;msg|Failed to add album;";
        Iterator iUsers = users.iterator();
        boolean isEditor = false;
        boolean found = false;
        boolean alreadyExists = false;

        while (iUsers.hasNext()) {
            User u = (User) iUsers.next();
            if (u.email.equals(email) && u.isEditor()) {
                isEditor = true;
                Iterator iArtists = artists.iterator();

                while (iArtists.hasNext()) {
                    Artist a = (Artist) iArtists.next();
                    if (a.name.equals(artName)) {

                        // Check if already exists
                        for (Album al : a.albums) {
                            if (al.title.equals(albName)) {
                                alreadyExists = true;
                            }
                        }

                        if (!alreadyExists) {
                            found = true;
                            a.albums.add(new Album(albName, description, genre));
                            rsp = "flag|" + id + ";type|addalb;email|" + email + ";result|y;";
                        }
                    }
                }
            }
        }
        if (!isEditor)
            rsp = "flag|"+id+";type|addalb;email|"+email+";result|n;msg|Only an Editor can add a new album;";
        else if (alreadyExists)
            rsp = "flag|"+id+";type|addalb;email|"+email+";result|n;msg|`"+albName+"` already exists;";
        else if (!found)
            rsp = "flag|"+id+";type|addalb;email|"+email+";result|n;msg|Couldn't find artist `"+artName+"`;";

        ObjectFiles.writeArtistsToMemory(artists);

        sendResponseMulticast(rsp, code);
    }

    public void addMusic(String id, String albName, String title, String track, String email, String code) {
        // Request  -> flag | s; type | addmusic; alb | bbbb; title | tttt; track | n; email | dddd;
        // Response -> flag | r; type | addmusic; title | tttt; email | dddd; result | (y/n);
        String rsp = "flag|"+id+";type|addmusic;title|"+title+";email|"+email+";result|n;msg|Failed to add music;";
        Iterator iUsers = users.iterator();
        boolean isEditor = false;
        boolean found = false;
        boolean alreadyExists = false;

        while (iUsers.hasNext()) {
            User u = (User) iUsers.next();
            if (u.email.equals(email) && u.isEditor()) {
                isEditor = true;

                Iterator iArtists = artists.iterator();

                while (iArtists.hasNext()) {
                    Artist a = (Artist) iArtists.next();
                    Iterator iAlbums = a.albums.iterator();
                    while(iAlbums.hasNext()) {
                        Album al = (Album) iAlbums.next();
                        if (al.title.equals(albName)) {

                            for (Music m : al.tracks)
                                if (m.track == Integer.parseInt(track))
                                    alreadyExists = true;

                            if (!alreadyExists) {
                                found = true;
                                al.tracks.add(new Music(Integer.parseInt(track), title));
                                rsp = "flag|" + id + ";type|addmusic;title|" + title + ";email|" + email + ";result|y;";
                            }
                        }
                    }
                }
            }
        }

        if (!isEditor)
            rsp = "flag|"+id+";type|addmusic;title|"+title+";email|"+email+";result|n;msg|Only an Editor can add a new music;";
        else if (alreadyExists)
            rsp = "flag|"+id+";type|addmusic;title|"+title+";email|"+email+";result|n;msg|Track #"+track+" already exists;";
        else if (!found)
            rsp = "flag|"+id+";type|addmusic;title|"+title+";email|"+email+";result|n;msg|Couldn't find album `"+albName+"`;";

        ObjectFiles.writeArtistsToMemory(artists);

        sendResponseMulticast(rsp, code);
    }



    public void run() {



        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:" + message);

        // Decode message
        ArrayList<String[]> cleanMessage = cleanTokens(message); // if the packet contains "flag | s" the server has to respond



        if (cleanMessage.get(1)[1].equals("register")) { //register
            register(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(cleanMessage.size()-1)[1]);    // (email, password)
        } else if (cleanMessage.get(1)[1].equals("login")) { // login
            login(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(cleanMessage.size()-1)[1]); // (email, password)
        } else if (cleanMessage.get(1)[1].equals("details")) { // search Artist, Album, Music
            getDetails(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(cleanMessage.size()-1)[1]); // (Artist or Album, keyword)
        } else if(cleanMessage.get(1)[1].equals("critic")) {            // add critic to album
            writeCritic(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(5)[1], cleanMessage.get(cleanMessage.size()-1)[1]);// (album, critic, rate, email)
        } else if(cleanMessage.get(1)[1].equals("privilege")) {
            turnIntoEditor(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(cleanMessage.size()-1)[1]);       // (Editor, regularToEditor)
        } else if(cleanMessage.get(1)[1].equals("notify")) {
            offUserNotified(cleanMessage.get(0)[1], cleanMessage.get(4)[1], cleanMessage.get(2)[1]);    // (email, message)
        } else if(cleanMessage.get(1)[1].equals("share")) {
            share(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(cleanMessage.size() - 1)[1]); // (title, shareTo, uploader)
        } else if (cleanMessage.get(1)[1].equals("addart")) {
            addArtist(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1] ,cleanMessage.get(cleanMessage.size()-1)[1]);
        } else if (cleanMessage.get(1)[1].equals("addalb")) {
            addAlbum(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(5)[1], cleanMessage.get(6)[1], cleanMessage.get(cleanMessage.size()-1)[1]);
        } else if (cleanMessage.get(1)[1].equals("addmusic")) {
            addMusic(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(5)[1], cleanMessage.get(cleanMessage.size()-1)[1]);
        } else if(cleanMessage.get(1)[1].equals("requestTCPConnection") && cleanMessage.get(2)[1].equals("upload")) {
            // flag | s; type | requestTCPConnection; operation | upload; tittle | tttt; email | eeee;
            try {
                uploadMusic(cleanMessage.get(0)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(cleanMessage.size() - 1)[1]); // (title, email)
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }



        } else if(cleanMessage.get(1)[1].equals("requestTCPConnection") && cleanMessage.get(2)[1].equals("download")) {
            // Request  -> flag | s; type | requestTCPConnection; operation | download; title | tttt; uploader | uuuu; email | eeee

            // "flag|"+id+";type|requestTCPConnection;operation|download;title|"+title+";uploader|"+uploader+";email|"+email+";hash|"+hash+";");
            try {
                // ID title uploader email hash
                downloadMusic(cleanMessage.get(0)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(5)[1], cleanMessage.get(cleanMessage.size() - 1)[1]); // (title, uploader, email)
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(cleanMessage.get(1)[1].equals("connectionrequest")) {
            // Receive RMI packets asking Multicast to respond with their Hash codes
            // Request  -> flag | s; type | connectionrequest;

            if(cleanMessage.get(0)[1].equals("s"))
                sendResponseMulticast("flag|r;type|ack;", hashCode);
            else
                sendResponseMulticast("flag|"+cleanMessage.get(0)[1]+";type|ack;", hashCode);

        } else {
            System.out.println("Invalid protocol message");
        }

        }


}
