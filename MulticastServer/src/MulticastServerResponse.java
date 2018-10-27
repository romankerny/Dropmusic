import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.Iterator;

/**
 *
 * MulticastServerResponse is a Thread that is created by MulticastServer every time a new packet arrives for processing.
 * It received the packet, Multicast's IP, users and artists array and it's hashCode.
 *
 * This thread contains all methods to deal with all kinds of packets send by the RMIServer.
 *
 */

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

    /**
     *  Creates a MulticastSocket and sends a Datagram packet with
     *  some protocol instruction. This method is only run if Multicast's hash matches RMIServer's requested hash.
     *
     * @param resp - protocol instruction to send
     * @param code - unique hash
     *
     */

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

    /**
     *
     * Parses datagram String in simple Tokens
     *
     * @param msg
     * @return ArrayList of tokens example: (id, 54fsdsf4), (type, login) , ....
     */

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


    /**
     *
     * Creates a new MulticastSocket with random port between 5000 and 6000
     * Used upon a upload/download request.
     *
     *
     * @return MulticastSocket
     */

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

    /**
     * Method to upload a music. First checks if music exists in database, if not then sends an answer with result|n and
     * an error message as msg|error here
     *
     * If found then a result|y answer is sent along with Multicast's IP and Port for the client to connect.
     * Afterwards creates a new TCP socket using #getSocket() and awaits RMIClient to connect. Once connected the client
     * sends a filename, size and finally all file's raw data as bytes. These are stored in as a #MusicFile along with
     * email of the uploader.
     *
     * @param id Packet's unique ID
     * @param title Music's title
     * @param email Email of the user that requested an upload
     * @param code Multicast server's hash, this method is only run if hash send by RMIServer matches Multicast's hash.
     * @see #getSocket()
     * @see #sendResponseMulticast(String, String)
     */

    public void uploadMusic(String id, String title, String email, String code) throws IOException {
        // Request  -> flag | id; type | requestTCPConnection; operation | upload; title | tttt; uploader | uuuu; email | eeee
        // Response -> flag | id; type | requestTCPConnection; operation | upload; email | eeee; result | y; port | pppp;
        // Response -> flag | id; type | requestTCPConnection; operation | upload; email | eeee; result | n; msg | mmmmmmmmm;

        if(this.hashCode.equals(code)) {

            Music song = null;

            for (Artist a : artists)
                for (Album al : a.albums)
                    for (Music m : al.tracks)
                        if (m.title.equals(title)) {
                            song = m;
                        }

            if (song != null) {
                ServerSocket serverSocket = getSocket();
                Socket client = null;
                String ip = InetAddress.getLocalHost().getHostAddress();
                int port = serverSocket.getLocalPort();

                System.out.println("A dar upload de musica " + title);
                sendResponseMulticast("flag|"+id+";type|requestTCPConnection;operation|upload;email|"+email+";result|y;ip|"+ip+";port|"+port+";", code);

                System.out.println("socket is bound");
                client = serverSocket.accept();

                DataInputStream in = new DataInputStream(client.getInputStream());

                // Filename and size first
                String filename = in.readUTF();
                long size = in.readLong();

                byte[] rawData = new byte[(int) size];
                // Ler todos os bytes
                in.readFully(rawData);

                song.musicFiles.put(email, new MusicFile(filename, rawData));
                song.musicFiles.get(email).emails.add(email);

                in.close();
                serverSocket.close();
                ObjectFiles.writeArtistsToDisk(artists);
                System.out.println("Upload of " + title+ " done" );
            } else {
                sendResponseMulticast("flag|"+id+";type|requestTCPConnection;operation|upload;email|"+email+";result|n;msg|Couldn't find `"+title+"` in database;", code);
            }
        }

    }

    /**
     * Method do download a music. For now only one MulticastServer contains a user's uploader. First each Multicast
     * needs to search if it is the one that has the uploaded file. If it doesn't then immediately after sends a result|n
     * claiming that it does not have the file and exits.
     *
     * If it does, then first checks if requesting user is allowed to download from the uploader, if not then sends result|n
     * with an error message to display.
     *
     * Otherwise creates a new TCP socket, sends an IP and Port as answer and awaits connection. Once connected, sends
     * file's name and raw data afterwards.
     *
     *
     * @param id Packet's unique ID
     * @param title Music's title
     * @param uploader Email from whom the user is trying to download
     * @param email Requesting user's email
     * @param code Hash sent by RMIServer
     * @see #getSocket()
     * @see #searchMusic(String, String)
     * @see #sendResponseMulticast(String, String)
     */

    public void downloadMusic(String id, String title, String uploader, String email, String code) throws IOException {
        // Request  -> flag | id; type | requestTCPConnection; operation | download; email | eeee;
        // Response -> flag | id; type | requestTCPConnection; operation | download; email | eeee; result | y; ip| iiii; port | pppp;
        // Response -> flag | id; type | requestTCPConnection; operation | download; email | eeee; result | n; msg | mmmmmmmmm;
        Music msc = searchMusic(title, uploader);

        if(msc == null) {
            sendResponseMulticast("flag|"+id+";type|requestTCPConnection;operation|download;email|"+email+";result|n;", code);
            return;
        }

        Music song = null;
        if(this.hashCode.equals(code)) {
            for (Artist a : artists)
                for (Album al : a.albums)
                    for (Music m : al.tracks)
                        if (m.title.equals(title))
                            song = m;

            if (song != null) { // It was found
                if (song.musicFiles.get(uploader).emails.contains(email)) { // Allowed to download
                    ServerSocket serverSocket = getSocket();
                    String ip = InetAddress.getLocalHost().getHostAddress();
                    int port = serverSocket.getLocalPort();
                    Socket client = null;

                    sendResponseMulticast("flag|"+id+";type|requestTCPConnection;operation|download;email|"+email+";result|y;ip|"+ip+";port|"+port+";", code);

                    client = serverSocket.accept();
                    DataOutputStream out = new DataOutputStream(client.getOutputStream());
                    MusicFile mf = song.musicFiles.get(uploader);

                    // Send filename before raw data
                    out.writeUTF(mf.filename);
                    out.write(mf.rawData);
                    out.close();
                    serverSocket.close();
                    System.out.println(" a bazar do download ");

                } else { // Not allowed
                    sendResponseMulticast("flag|"+id+";type|requestTCPConnection;operation|download;email|"+email+";result|n;msg|You're not allowed to download this song;", code);
                }
            } else { // Not found
                sendResponseMulticast("flag|"+id+";type|requestTCPConnection;operation|download;email|"+email+";result|n;msg|Couldn't find `"+title+"` in database;", code);
            }
        }

        ObjectFiles.writeArtistsToDisk(artists);
    }

    /**
     * Method to share an uploaded music with another user.
     * First searches if user did indeed upload that song. If not then answers with result|n and an error message,
     * if found then the user is now allowed to download from uploader and a result|y answer is sent.
     *
     * @param id Packet's unique ID
     * @param title Music's title
     * @param shareTo User that the uploader want's to share
     * @param uploader The uploader himself
     * @param code Hash sent by RMIServer
     * @see #getSocket()
     * @see #sendResponseMulticast(String, String)
     */

    public void share(String id, String title, String shareTo, String uploader, String code) {
        // Request  -> flag | id; type | share; title | tttt; shareTo | sssss; uploader | uuuuuu;
        // Response -> flag | id; type | share; result | (y/n): title | ttttt; shareTo | ssssss; uploader | uuuuuu; msg | mmmmmm;

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
        ObjectFiles.writeArtistsToDisk(artists);
        ObjectFiles.writeUsersToDisk(users);

        if (found) {
            rsp = "flag|"+id+";type|share;result|y;title|"+title+";shareTo|"+shareTo+";uploader|"+uploader+";";
        } else {
            rsp = "flag|"+id+";type|share;result|n;title|"+title+";shareTo|"+shareTo+";uploader|"+uploader+";msg|Couldn't find upload file;";
        }
        sendResponseMulticast(rsp, code);
    }

    /**
     * Method to register a new user. First checks if the email is registered already, if not then adds the new user
     * to the database. Answers with result|n for the former and result|y for latter.
     *
     * @param id Packet's unique ID
     * @param email User's new email
     * @param password User's password
     * @param code Hash sent by RMIServer
     * @see #sendResponseMulticast(String, String)
     */

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
            users.add(s);
            rsp = "flag|"+id+";type|register;result|y;username|" + email + ";password|" + password + ";";
            ObjectFiles.writeUsersToDisk(users);
        }
        sendResponseMulticast(rsp, code);
    }

    /**
     * Method to login. Checks if email and password match, answering with result|y or result|n if not.
     * If user has any missed notifications, these are added to Multicast's answer.
     * @param id Packet's unique ID
     * @param email User's email
     * @param password User's password
     * @param code Hash sent by RMIServer
     * @see #sendResponseMulticast(String, String)
     */

    public void login(String id, String email, String password, String code) {
        // Request  -> flag | s; type | login; email | eeee; password | pppp;
        // Response -> flag | r; type | login; result | (y/n); email | eeee; password | pppp; notification_count | n; notif_x | notif; msg | mmmmmm;
        String rsp = "flag|"+id+";type|login;result|n;email|" + email + ";password|" + password + ";msg|Incorrect user/password;";

        Iterator iUsers = users.iterator();
        User u = null;
        boolean found = false;

        while (iUsers.hasNext() && !found) {
            u = (User) iUsers.next();
            if (u.email.equals((email)) && u.password.equals(password)) {
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
            u.notifications.clear();

            System.out.println(email + " logged in");
        }
        sendResponseMulticast(rsp, code);
    }

    /**
     * Method to turn a regular user into an editor.
     * First checks if user1 is actually an editor, if it is then proceeds to find the user that he wants to promote,
     * if found then turns him into an editor and answers with result|y. Otherwise if any of the conditions fail,
     * answers with result|n and an error message instead.
     * @param id
     * @param user1 The promoter
     * @param user2 The promotee
     * @param code Hash sent by RMIServer
     * @see #sendResponseMulticast(String, String)
     */

    public void turnIntoEditor(String id, String user1, String user2, String code) {

        // Request  -> flag | id; type | privilege; user1 | username; user2; username;
        // Response -> flag | id; type | privilege; result | (y/n): user1 | username; user2 | username; msg | mmmmmmmm;

        String rsp = "flag|"+id+";type|privilege;result|n;user1|" + user1 +";user2|" + user2 + ";";

        Iterator iUsers1 = users.iterator();
        Iterator iUsers2 = users.iterator();
        boolean isEditor = false;
        boolean found2 = false;

        // Find editor and check if he is actually an editor
        while (iUsers1.hasNext() && !isEditor) {
            User s = (User) iUsers1.next();
            if(s.email.equals(user1) && s.isEditor())
                isEditor = true;

        }
        if (isEditor) {
            // Find regular user
            while (iUsers2.hasNext() && !found2) {
                User s = (User) iUsers2.next();
                if (s.email.equals(user2)) {
                    System.out.println(s);
                    s.becomeEditor();
                    System.out.println(s);
                    found2 = true;
                    rsp = "flag|"+id+";type|privilege;result|y;user1|" + user1 +";user2|" + user2 + ";";
                }
            }
            if (!found2)
                rsp += "msg|Couldn't find user to promote;";
        } else {
            rsp += "msg|You have to be an Editor to use /promote;";
        }

        ObjectFiles.writeUsersToDisk(users);

        sendResponseMulticast(rsp, code); // -> RMIServer
    }

    /**
     * Method to review and score an album.
     * First checks if album exists, if does then adds a new rating and review to it. If not found then an error message
     * is returned.
     * @param id Packet's unique ID
     * @param albumName Album's name
     * @param critic The review iself
     * @param rate 1-5 rating
     * @param email User's email that rated
     * @param code Hash sent by RMIServer
     * @see #sendResponseMulticast(String, String)
     */

    public void writeCritic(String id, String albumName, String critic, String rate, String email, String code) {

        // flag | id; type | critic; album | name; critic | blabla; rate | n email | eeee;
        // flag | id; type | critic; result | (y/n); album | name; critic | blabla; rate | n; msg | mmmmm;

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

        ObjectFiles.writeArtistsToDisk(artists);
        sendResponseMulticast(rsp, code);
    }

    /**
     * Method to store offline user's missed notifications
     * @param email Offline user's email
     * @param message Missed notification
     */

    public void offUserNotified(String id, String email, String message) {

        for (User u : users)
            if(u.email.equals(email))
                u.addNotification(message);
        ObjectFiles.writeUsersToDisk(users);
    }

    /**
     * Method to get details of an artist or album. Searches by artist name and album's title or genre.
     * If keyword matches then it is added as a new found item to be sent as answer. If nothing is found then returns
     * an error message.
     * @param id Packet's unique ID
     * @param type Either 'art', 'alb' or 'gen' for artist, album and genre respectively
     * @param keyword Keyword used for the search query
     * @param code Hash sent by RMIServer
     * @see #sendResponseMulticast(String, String)
     */
    public void getDetails(String id, String type, String keyword, String code) {
        // Request  -> flag | id; type | search; param | (art, alb, gen); keyword | kkkk;
        // Response -> flag | id; type | search; param | (art, alb, gen); keyword | kkkk; item_count | n; item_x_name | name; [...] msg | mmmmmm;

        String rsp, response = "";
        String message= " ";
        boolean found = false;
        char result = 'n';

        if (type.equals("art")) {
            for (Artist a : artists) {
                if (a.name.equals(keyword)) {
                    found = true;
                    response = a.toString();
                    result = 'y';
                }
            }
        } else if (type.equals("alb") || type.equals("gen")) {
            for (Artist a : artists) {
                for (Album al : a.albums) {
                    if (al.title.equals(keyword) || al.genre.equals(keyword)) {
                        found = true;
                        response += al.toString();
                        result = 'y';
                    }
                }
            }
        }

        if (!found)
            message = "Couldn't find `"+keyword+"` in database";

        rsp = "flag|"+id+";type|details;result|"+result+";param|"+type+";keyword|"+keyword+";item_count|1;item_x_name|"+response+";msg|"+message+";";
        sendResponseMulticast(rsp, code);

    }

    /**
     * Method used before downloading a music to see if the file exists in this particular Multicast's database
     * @param musicTitle Music's title
     * @param uploader Uploader's email
     * @return Music object or null if not found
     * @see #downloadMusic(String, String, String, String, String)
     */

    public Music searchMusic(String musicTitle, String uploader) {

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

    /**
     * Method to add/edit an artist. An edit happens if artist's name matches an existing one in database. If edited, then
     * every user that edited this particular artist, it's albums or songs is notified.
     * If user is not an editor then an error message is sent instead.
     * @param id Packet's unique ID
     * @param name Artist's name
     * @param details Artist's description
     * @param email Requesting
     * @param code Hash sent by RMIServer
     * @see #sendResponseMulticast(String, String)
     */

    public void addArtist(String id, String name, String details, String email, String code) {
        // Request  -> flag | s; type | addart; name | nnnn; details | dddd; email | dddd;
        // Response -> flag | r; type | addart; email | dddd; result | (y/n); notif_count | n; notif | email; notif | email; [etc...]; msg | mmmmm;

        String rsp = "flag|"+id+";type|addart;email|"+email+";result|n;msg|Failed to add artist;";
        boolean alreadyExists = false;
        boolean isEditor = false;
        String notify = "notif_count|0;";
        User editor = null;

        for (User u : users)
            if (u.email.equals(email) && u.isEditor()) {
                isEditor = true;
                editor = u;
        }

        if (isEditor) {
            for (Artist a : artists) {
                if (a.name.equals(name)) {
                    alreadyExists = true;
                }
                if (alreadyExists) {
                    a.name = name;
                    a.details = details;

                    // Get users to notify
                    if (a.notifyIfEdited.size() > 0) {
                        notify = "notif_count|" + a.notifyIfEdited.size() + ";";
                        for (User u : a.notifyIfEdited) {
                            notify += "notif|"+u.email+";";
                        }
                    }
                    a.addNotifyIfEdited(editor);
                    rsp = "flag|"+id+";type|addart;email|"+email+";result|y;"+notify+"msg|Artist updated;";

                }
            }
            if (!alreadyExists) {
                Artist a = new Artist(name, details);
                artists.add(a);
                a.addNotifyIfEdited(editor);
                // if artist was now created there is no one to notify

                rsp = "flag|"+id+";type|addart;email|"+email+";result|y;msg|Artist created;";
            }
        }
        ObjectFiles.writeArtistsToDisk(artists);

        if(!isEditor)
            rsp = "flag|"+id+";type|addart;email|"+email+";result|n;msg|Only an editor can add a new artist;";

        sendResponseMulticast(rsp, code);
    }

    /**
     * Method to add/edit a new album to an artist. An edit happens if album's name matches an existing one in artist's
     * database. Once added/edited every user that has added/edited an album, song or created this artist is notified.
     * If the user is not an editor, or artist's name is not found, then an error message is sent instead.
     *
     * @param id Packet's unique ID
     * @param artName Artist's name
     * @param albName Album's name
     * @param description Album's description
     * @param genre Album's genre
     * @param email User's email
     * @param code Hash sent by RMIServer
     * @see #sendResponseMulticast(String, String)
     */

    public void addAlbum(String id, String artName, String albName, String description, String genre, String email, String code) {
        // Request  -> flag | s; type | addalb; art | aaaa; alb | bbbb; description | dddd; genre | gggg; email | dddd;
        // Response -> flag | r; type | addalb; email | ddd; result |(y/n); notif_count | n; notif | email; notif | email; [etc...]; msg | mmmmm;
        String rsp = "flag|"+id+";type|addalb;email|"+email+";result|n;msg|Failed to add album;";
        boolean isEditor = false;
        boolean found = false;
        boolean alreadyExists = false;
        String notify = "notif_count|0;";

        User editor = null;
        Artist aux = null;

        for (User u : users)
            if (u.isEditor() && u.email.equals(email)) {
                isEditor = true;
                editor = u;
        }

        if (isEditor) {
            for (Artist a : artists) {
                if (a.name.equals(artName)) {
                    found = true;
                    aux = a;
                }
                if (found) {
                    for (Album al : a.albums) {
                        if (al.title.equals(albName)) {
                            alreadyExists = true;
                        }

                        if (alreadyExists) {
                            al.title = albName;
                            al.description = description;
                            al.genre = genre;

                            // Get users to notify
                            if (a.notifyIfEdited.size() > 0) {
                                notify = "notif_count|" + a.notifyIfEdited.size() + ";";
                                for (User u : a.notifyIfEdited) {
                                    notify += "notif|"+u.email+";";
                                }
                            }
                            a.addNotifyIfEdited(editor);

                            rsp = "flag|"+id+";type|addalb;email|"+email+";result|y;"+notify+"msg|Album updated;";
                        }
                    }
                    if (!alreadyExists) {
                        System.out.println(albName + " does not exits, adding");

                        Album toAdd = new Album(albName, description, genre);
                        aux.albums.add(toAdd);
                        // Get users to notify
                        if (aux.notifyIfEdited.size() > 0) {
                            notify = "notif_count|" + aux.notifyIfEdited.size() + ";";
                            for (User u : aux.notifyIfEdited) {
                                notify += "notif|"+u.email+";";
                            }
                        }
                        aux.addNotifyIfEdited(editor);

                        rsp = "flag|"+id+";type|addalb;email|"+email+";result|y;"+notify+"msg|Album created;";
                    }
                }
            }
        }


        if (!isEditor)
            rsp = "flag|"+id+";type|addalb;email|"+email+";result|n;msg|Only an Editor can add a new album;";
        else if (!found)
            rsp = "flag|"+id+";type|addalb;email|"+email+";result|n;msg|Couldn't find artist `"+artName+"`;";

        ObjectFiles.writeArtistsToDisk(artists);
        sendResponseMulticast(rsp, code);
    }

    /**
     * Method to add/edit a music. An edit happens if track's number matches an existing one.
     * Once edited/added every user that has modified either the artist, his albums or any of his songs will be notified.
     * If user is not an editor or album's name is not found an error message is sent instead.
     * @param id Packet's unique ID
     * @param albName Album's name
     * @param title Track's title
     * @param track Track number
     * @param email User's email
     * @param code Hash sent by RMIServer
     * @see #sendResponseMulticast(String, String)
     */

    public void addMusic(String id, String albName, String title, String track, String email, String code) {
        // Request  -> flag | s; type | addmusic; alb | bbbb; title | tttt; track | n; email | dddd;
        // Response -> flag | r; type | addmusic; title | tttt; email | dddd; result | (y/n); notif_count | n; notif | email; notif | email; [etc...]; msg | mmmmm;
        String rsp = "flag|"+id+";type|addmusic;title|"+title+";email|"+email+";result|n;msg|Failed to add music;";
        boolean isEditor = false;
        boolean found = false;
        boolean alreadyExists = false;
        String notify = "notif_count|0;";

        User editor = null;
        // como saber a q artista adicionar a musica?

        int trackNum = Integer.parseInt(track);
        Artist aux = null;

        for (User u : users) {
            System.out.println(u);
            if (u.email.equals(email) && u.isEditor()) {
                isEditor = true;
                editor = u;
            }
        }

        if (isEditor) {
            for (Artist a : artists) {
                for (Album al : a.albums) {
                    if (al.title.equals(albName)) {
                        found = true;
                        aux = a;
                    }
                    if (found) {
                        for (Music m : al.tracks) {
                            if (m.track == trackNum) {
                                alreadyExists = true;
                            }


                            if (alreadyExists) {
                                m.title = title;
                                m.track = trackNum;

                                // Get users to notify
                                if (a.notifyIfEdited.size() > 0) {
                                    notify = "notif_count|" + a.notifyIfEdited.size() + ";";
                                    for (User u : a.notifyIfEdited) {
                                        notify += "notif|"+u.email+";";
                                    }
                                }
                                a.addNotifyIfEdited(editor);

                                rsp = "flag|"+id+";type|addmusic;title|"+title+";email|"+email+";result|y;"+notify+"msg|Music updated;";
                            }
                        }
                        if (!alreadyExists) {

                            Music toAdd = new Music(trackNum, title);
                            System.out.println("GAJOS: "+ aux.notifyIfEdited);
                            // Get users to notify
                            if (aux.notifyIfEdited.size() > 0) {
                                notify = "notif_count|" + aux.notifyIfEdited.size() + ";";
                                for (User u : aux.notifyIfEdited) {
                                    notify += "notif|"+u.email+";";
                                }
                            }
                            aux.addNotifyIfEdited(editor);
                            al.tracks.add(toAdd);
                            rsp = "flag|"+id+";type|addmusic;title|"+title+";email|"+email+";result|y;"+notify+"msg|Music created;";
                        }
                    }
                }
            }
        }

        if (!isEditor)
            rsp = "flag|"+id+";type|addmusic;title|"+title+";email|"+email+";result|n;msg|Only an Editor can add a new music;";
        else if (!found)
            rsp = "flag|"+id+";type|addmusic;title|"+title+";email|"+email+";result|n;msg|Couldn't find album `"+albName+"`;";

        ObjectFiles.writeArtistsToDisk(artists);

        sendResponseMulticast(rsp, code);
    }


    /**
     * Thread that receives a packet, parses it with cleanTokens() and calls the respective function depending on type of packet
     * @see #cleanTokens(String)
     */

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
            } else if(cleanMessage.get(1)[1].equals("notifyfail")) {
                offUserNotified(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1]);    // (email, message)
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
