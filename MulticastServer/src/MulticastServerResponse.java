import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
    private Connection con;


    MulticastServerResponse(DatagramPacket packet, String ip, String code, Connection con) {

        this.packet = packet;
        MULTICAST_ADDRESS = ip;
        this.hashCode = code;
        this.con = con;
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

    boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
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
     * @param title Music's track # in album
     * @param email Email of the user that requested an upload
     * @param code Multicast server's hash, this method is only run if hash send by RMIServer matches Multicast's hash.
     * @see #getSocket()
     * @see #sendResponseMulticast(String, String)
     */

    public void uploadMusic(String id, String artistName, String albumName, String title, String email, String code) throws IOException {
        // Request  -> flag | id; type | requestTCPConnection; operation | upload; artist | name; album | name; title | name; uploader | uuuu; email | eeee
        // Response -> flag | id; type | requestTCPConnection; operation | upload; email | eeee; result | y; ip| iiii; port | pppp;
        // Response -> flag | id; type | requestTCPConnection; operation | upload; email | eeee; result | n; msg | mmmmmmmmm;

        // ---------- BD

        if (this.hashCode.equals(code)) {
            PreparedStatement pstmt;
            ResultSet rs;
            try {
                pstmt = con.prepareStatement("select music.id " +
                        "from music, (select id from album where title = ? and artist_name = ?) as alb " +
                        "where music.album_id = alb.id and music.title = ?");
                pstmt.setString(1, albumName);
                pstmt.setString(2, artistName);
                pstmt.setString(3, title);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    int musicID = rs.getInt(1);
                    rs.close();
                    // Time to upload
                    ServerSocket serverSocket = getSocket();
                    Socket client = null;
                    String ip = InetAddress.getLocalHost().getHostAddress();
                    int port = serverSocket.getLocalPort();

                    sendResponseMulticast("flag|"+id+";type|requestTCPConnection;operation|upload;email|"+email+";result|y;ip|"+ip+";port|"+port+";", code);

                    client = serverSocket.accept();
                    // Connected
                    DataInputStream in = new DataInputStream(client.getInputStream());
                    // Create unique path artist/album/track/email/filename.mp3
                    char ps = File.separatorChar;
                    String filename = in.readUTF();
                    String uploadedPath = "uploads"+ps+artistName+ps+albumName+ps+title+ps+email+ps;

                    File directory = new File(uploadedPath);
                    directory.mkdirs();
                    File upload = new File(uploadedPath+filename);

                    FileOutputStream fos = new FileOutputStream(upload);
                    byte buffer[] = new byte[4096];
                    int count;
                    System.out.println("Uploading `" + filename + "`");
                    while ((count = in.read(buffer)) != -1) {
                        fos.write(buffer, 0, count);
                    }
                    in.close();
                    System.out.println("Done");

                    // Add to DB
                    int rsl;
                    pstmt = con.prepareStatement("INSERT into upload (musicfilename, music_id, user_email) " +
                                        "VALUES (?,?,?) ON DUPLICATE KEY UPDATE musicfilename = ?");
                    pstmt.setString(1, uploadedPath+filename);
                    pstmt.setInt(2, musicID);
                    pstmt.setString(3, email);
                    pstmt.setString(4, uploadedPath+filename);

                    rsl = pstmt.executeUpdate();
                    if (rsl == 1)
                        System.out.println("Added upload to DB with success");
                    else
                        System.out.println("Failed to add upload to DB");

                    // Add uploader as allowed to download
                    pstmt = con.prepareStatement("INSERT into upload_user (upload_music_id, upload_user_email, user_email)" +
                            "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE user_email=?");
                    pstmt.setInt(1, musicID);
                    pstmt.setString(2, email);
                    pstmt.setString(3, email);
                    pstmt.setString(4, email);


                    rsl = pstmt.executeUpdate();
                    if (rsl == 1)
                        System.out.println(email +" added to allowed");
                    else
                        System.out.println("Failed to add "+email+" to allowed");

                    pstmt.close();

                } else {
                    String errorMsg = "msg|Couldn't find `"+title+"` in "+albumName+" by "+artistName+" in database;";
                    sendResponseMulticast("flag|"+id+";type|requestTCPConnection;operation|upload;email|"+email+";result|n;"+errorMsg, code);
                }

            } catch (SQLException e) {
                e.printStackTrace();
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
     * @see #sendResponseMulticast(String, String)
     */

    public void downloadMusic(String id, String title, String uploader, String email, String albumName, String artistName, String code) throws IOException {
        // Request  -> flag | id; type | requestTCPConnection; operation | download; email | eeee;
        // Response -> flag | id; type | requestTCPConnection; operation | download; email | eeee; result | y; ip| iiii; port | pppp;
        // Response -> flag | id; type | requestTCPConnection; operation | download; email | eeee; result | n; msg | mmmmmmmmm;


        // server tries to find the music w/ the given uploader
        // Music msc = searchMusic(title, uploader);
        ResultSet rs;
        PreparedStatement pstmt = null, pstmt1 = null;
        String path;
        int c = 0;

        try {
            // check if user with email can downlaoad

            System.out.println("Figuring out if user with ´email´ can download the given music. . .");
            pstmt = con.prepareStatement("SELECT DISTINCT m.id FROM music m, artist a, album alb, upload_user up " +
                    "                          WHERE up.upload_user_email = ? AND up.user_email = ? AND up.upload_music_id = m.id" +
                    "                          AND m.title = ? AND alb.title = ? AND a.name = ?" +
                    "                          AND alb.artist_name = a.name AND m.album_id = alb.id");
            pstmt.setString(1, uploader);
            pstmt.setString(2, email);
            pstmt.setString(3, title);
            pstmt.setString(4, albumName);
            pstmt.setString(5, artistName);

            rs = pstmt.executeQuery();

            if ((rs.next())) {
                System.out.println("Fetching file path in DB");
                String musicID = rs.getString("id");
                pstmt1 = con.prepareStatement("SELECT musicfilename FROM upload WHERE music_id = ? AND user_email = ?");
                pstmt1.setString(1, musicID);
                pstmt1.setString(2, uploader);
                rs = pstmt1.executeQuery();

                if(rs.next()) {
                    System.out.println("Starting Download . . .");
                    path = rs.getString("musicfilename");
                    System.out.println("Path : " + path);
                    ServerSocket serverSocket = getSocket();
                    String ip = InetAddress.getLocalHost().getHostAddress();
                    int port = serverSocket.getLocalPort();
                    Socket client = null;
                    sendResponseMulticast("flag|" + id + ";type|requestTCPConnection;operation|download;email|" + email + ";result|y;ip|" + ip + ";port|" + port + ";", code);
                    client = serverSocket.accept();
                    System.out.println("Accepted client socket");
                    File musicFile = new File(path);
                    FileInputStream fis = new FileInputStream((musicFile));
                    DataOutputStream out = new DataOutputStream(client.getOutputStream());

                    out.writeUTF(musicFile.getName());
                    System.out.println("File.getName() : " + musicFile.getName());
                    byte buffer[] = new byte[4096];
                    int count;
                    System.out.println("Uploading file...");
                    while ((count = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, count);
                    }
                    out.close();
                } else {
                    sendResponseMulticast("flag|"+id+";type|requestTCPConnection;operation|download;email|"+email+";result|n;msg|Could not file File in DB;", code);
                }

            } else {
                // if music does not exist / no permission
                sendResponseMulticast("flag|"+id+";type|requestTCPConnection;operation|download;email|"+email+";result|n;", code);
                return;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    /**
     * Method to share an uploaded music with another user.
     * First searches if user did indeed upload that song. If not then answers with result|n and an error message,
     * if found then the user is now allowed to download from uploader and a result|y answer is sent.
     *
     * @param id Packet's unique ID
     * @param title Music's name
     * @param shareTo User that the uploader want's to share
     * @param uploader The uploader himself
     * @param code Hash sent by RMIServer
     * @see #sendResponseMulticast(String, String)
     */

    public void share(String id, String artist, String album, String title, String shareTo, String uploader, String code) {
        // Request  -> flag | id; type | share; artist | name; album | name; title | name; shareTo | sssss; uploader | uuuuuu;
        // Response -> flag | id; type | share; result | (y/n): title | ttttt; shareTo | ssssss; uploader | uuuuuu; msg | mmmmmm;

        // ----------- BD

        String rsp = "flag|"+id+";type|share;result|n;title|"+title+";shareTo|"+shareTo+";uploader|"+uploader+";msg|Couldn't find upload file;";
        PreparedStatement pstmt;
        int rs;
        try {
            pstmt = con.prepareStatement("INSERT INTO upload_user (upload_music_id, upload_user_email, user_email) " +
                                                "select upload.music_id, upload.user_email, ? " +
                                                "from upload, (select music.id " +
                                                              "from music, (select id " +
                                                                           "from album " +
                                                                           "where album.title = ? and album.artist_name = ?) alb " +
                                                              "where alb.id = music.album_id and music.title = ? ) mus " +
                                                 "where upload.music_id = mus.id and upload.user_email = ?;");

            pstmt.setString(1, shareTo);
            pstmt.setString(2, album);
            pstmt.setString(3, artist);
            pstmt.setString(4, title);
            pstmt.setString(5, uploader);
            rs = pstmt.executeUpdate();

            if (rs == 1)
                rsp = "flag|"+id+";type|share;result|y;title|"+title+";shareTo|"+shareTo+";uploader|"+uploader+";";

            pstmt.close();

        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 1452: // Foreign key violation
                    System.out.println("Wrong user_email");
                    rsp = "flag|"+id+";type|share;result|n;title|"+title+";shareTo|"+shareTo+";uploader|"+uploader+";msg|Couldn't find user;";
                    break;
                case 1048: // Can't be null (means select failed, user didn't upload that song)
                    System.out.println("Wrong upload_music_id");
                    rsp = "flag|"+id+";type|share;result|n;title|"+title+";shareTo|"+shareTo+";uploader|"+uploader+";msg|Couldn't find your upload of `"+title+"` in "+album+" by "+artist+";";
                    break;
                case 1062: // Duplicate entry
                    rsp = "flag|"+id+";type|share;result|n;title|"+title+";shareTo|"+shareTo+";uploader|"+uploader+";msg|You already shared this;";

            }
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

        String rsp = "flag|"+id+";type|register;result|n;username|" + email + ";password|" + password + ";msg|Failed to register;";
        // ---------- BD
        PreparedStatement pstmt;
        int rs;

        try {
            pstmt = con.prepareStatement("INSERT INTO user (email, password) VALUES (?,?)");
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            rs = pstmt.executeUpdate();

            System.out.println("Inserted " + rs + " new user(s).");
            rsp = "flag|"+id+";type|register;result|y;username|" + email + ";password|" + password + ";";

        } catch (SQLException e) {
            e.printStackTrace();

            switch (e.getErrorCode()) {
                case 1062:
                    // duplicate entry
                    System.out.println("Got ERROR:1062");
                    String message = "User : " + email +" already exists.";
                    System.out.println(message);
                    rsp = "flag|"+id+";type|register;result|n;username|" + email + ";password|" + password + ";"+"msg|"+message+";";
                    break;
            }

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

        // ------------- BD
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement("SELECT password from user where email = ?");

            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            String dbPassword;
            if (rs.next()) { // Get password
                dbPassword = rs.getString(1);

                if(dbPassword.equals(password)) { // User authenticated, retrieve his missed notifications

                    pstmt = con.prepareStatement("SELECT notification, user_email, id " +
                            "from notification where user_email = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

                    pstmt.setString(1, email);
                    rs = pstmt.executeQuery();

                    int notifCount = 0;
                    String notifications = "";
                    rsp = "flag|"+id+";type|login;result|y;email|" + email + ";password|" + password + ";notif_count|";

                    while (rs.next()) {
                        notifCount++;
                        notifications += "notif|"+ rs.getString(1) +";";
                        rs.deleteRow();
                    }

                    rsp += notifCount +";"+notifications;

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { rs.close(); pstmt.close(); } catch (SQLException e) { System.out.println(e); }
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

        String rsp = "flag|"+id+";type|privilege;result|n;user1|" + user1 +";user2|" + user2 + ";msg|Failed to promote user;";

        // ---------------- BD
        PreparedStatement pstmt;
        int rs;

        try {
            pstmt = con.prepareStatement("update user as u, (select editor from user where email = ? ) as p "+
                    "set u.editor = true " +
                    "where p.editor = true and u.email = ?");
            pstmt.setString(1, user1);
            pstmt.setString(2, user2);

            rs = pstmt.executeUpdate();

            if (rs == 0)
                System.out.println("Failed to promote user");
            else if (rs == 1) {
                System.out.println("Updated sucessfully");
                rsp = "flag|"+id+";type|privilege;result|y;user1|" + user1 +";user2|" + user2 + ";";
            }
            else
                System.out.println("Unreachable code");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        sendResponseMulticast(rsp, code); // -> RMIServer
    }

    /**
     * Method to review and score an album.
     * First checks if album exists, if it does then adds a new rating and review to it. If not found then an error message
     * is returned.
     * @param id Packet's unique ID
     * @param albumName Album's name
     * @param critic The review itself
     * @param rate 1-5 rating
     * @param email User's email that rated
     * @param code Hash sent by RMIServer
     * @see #sendResponseMulticast(String, String)
     */

    public void writeCritic(String id, String artistName, String albumName, String critic, String rate, String email, String code) {

        // flag | id; type | critic; album | name; critic | blabla; rate | n email | eeee;
        // flag | id; type | critic; result | (y/n); album | name; critic | blabla; rate | n; msg | mmmmm;

        // BD
        String rsp = "flag|"+id+";type|critic;result|n;album|" + albumName + ";critic|" + critic +";rate|" + rate + ";"+"msg|Couldn't find album `"+albumName+"` by "+artistName+";";

        PreparedStatement pstmt;
        int rs;
        try {
            pstmt = con.prepareStatement("INSERT INTO review (critic, rating, user_email, album_id) " +
                    "VALUES (?, ?, ?, (select id from album where title = ? and artist_name = ?)) ON DUPLICATE KEY UPDATE critic = ?, rating = ?");

            pstmt.setString(1, critic);
            pstmt.setInt(2, Integer.parseInt(rate));
            pstmt.setString(3, email);
            pstmt.setString(4, albumName);
            pstmt.setString(5, artistName);
            pstmt.setString(6, critic);
            pstmt.setInt(7, Integer.parseInt(rate));

            rs = pstmt.executeUpdate();

            if (rs == 1)
                rsp = "flag|"+id+";type|critic;result|y;album|" + albumName + ";critic|" + critic +";rate|" + rate + ";";

            pstmt.close();

        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 1452:
                    System.out.println("Wrong user_email");
                    break;
                case 1048:
                    System.out.println("Wrong album_id");
            }
        }

        sendResponseMulticast(rsp, code);
    }

    /**
     * Method to store offline user's missed notifications
     * @param email Offline user's email
     * @param message Missed notification
     */

    public void offUserNotified(String id, String email, String message) {

        // ------------ BD
        PreparedStatement pstmt;
        int rs;

        try {
            pstmt = con.prepareStatement("INSERT into notification (notification, user_email) " +
                                        "VALUES (?, ?)");

            pstmt.setString(1, message);
            pstmt.setString(2, email);
            rs = pstmt.executeUpdate();

            if ( rs == 1)
                System.out.println("Added offline notification to "+email);
            else // UNREACHABLE
                System.out.println("Shouldn't happen");

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

        PreparedStatement pstmt = null;
        ResultSet rs;
        String response = "", rsp = "", result = "", message = "";
        int a = 0;

        try {

            if (type.equals("art")) {
                pstmt = con.prepareStatement("SELECT * from artist where name = ?");

            } else if (type.equals("alb")) {
                pstmt = con.prepareStatement("SELECT * from album where title = ?");

            }

            pstmt.setString(1, keyword);
            rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            String aux = "";
            while (rs.next()) {

                aux += "item_"+ a +"_name|";
                for (int i = 1; i <= columnsNumber; i++) {
                    String columnValue = rs.getString(i);
                    aux +=  columnValue + " ";
                }
                aux += "\n;";
                a++;

                response += aux;
                aux = "";
            }

            if(a > 0) {
                result = "y";
            } else {
                result = "n";
                message = "Unable to find " + keyword + " in DB";
            }


        } catch (SQLException e) {
            result = "n";
            message = "Couldn't find `"+keyword+"` in database";
        }

        rsp = "flag|"+id+";type|details;result|"+result+";param|"+type+";keyword|"+keyword+";item_count|"+ a +";"+response+";msg|"+message+";";
        sendResponseMulticast(rsp, code);

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

        // ------------- BD
        PreparedStatement pstmt = null, pstmtEd = null, pstmtNot = null;
        String usrIsEditor = "", rsp = "flag|"+id+";type|addart;email|"+email+";result|n;msg|Failed to add artist;", notify  = "", bfNot = "";
        ResultSet qSet;
        int rs, notifCount = 0;

        try {

            // check if user is editor
            pstmtEd = con.prepareStatement("SELECT editor FROM user WHERE email = ?");
            pstmtEd.setString(1, email);
            qSet = pstmtEd.executeQuery();
            while(qSet.next()) {
                usrIsEditor = qSet.getString("editor");
            }

            if(usrIsEditor.equals("1")) {
                // insert new Artist
                System.out.println(email + " is editor");
                pstmt = con.prepareStatement("INSERT INTO artist (name, details) VALUES (?,?)");
                pstmt.setString(1, name);
                pstmt.setString(2, details);
                rs = pstmt.executeUpdate();

                // notify Editors
                pstmtNot = con.prepareStatement("SELECT user_email FROM artist_user WHERE artist_name = ?");
                pstmtNot.setString(1, name);
                qSet = pstmtNot.executeQuery();
                while(qSet.next()) {
                    notifCount++;
                    String notifEmail = qSet.getString("user_email");
                    notify += "notif|"+ notifEmail+";";
                }
                bfNot = "notif_count|" + Integer.toString(notifCount) + ";" + notify;
                rsp = "flag|"+id+";type|addart;email|"+email+";result|y;"+bfNot+"msg|Artist info added with success;";

                System.out.println("Artist " + name + " added to DB with success");
                System.out.println("Inserted " + rs + " new artist(s).");

            } else {
                // user is not editor
                rsp = "flag|"+id+";type|addart;email|"+email+";result|n;msg|Only an editor can add a new artist;";
            }
            if (pstmt != null )
                pstmt.close();
            if (pstmtEd != null)
                pstmtEd.close();
            if(pstmtNot != null)
                pstmtNot.close();

        } catch (SQLException e) {

            switch (e.getErrorCode()) {
                case 1062:
                    // duplicate entry
                    System.out.println("Got ERROR:1062 - Artist already exists");
                    rsp = "flag|"+id+";type|addart;email|"+email+";result|n;msg|Artist already exists;";
                    break;
            }

        }

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

    public void addAlbum(String id, String artName, String albName, String description, String genre, String email, String launchDate, String editorLabel, String code) {
        // Request  -> flag | s; type | addalb; art | aaaa; alb | bbbb; description | dddd; genre | gggg; email | dddd;
        // Response -> flag | r; type | addalb; email | ddd; result |(y/n); notif_count | n; notif | email; notif | email; [etc...]; msg | mmmmm;
        String rsp = "flag|"+id+";type|addalb;email|"+email+";result|n;msg|Failed to add album;";
        String notify = "";

        // ----BD

        PreparedStatement pstmt1 = null, pstmt2 = null, pstmtNot = null, pstmtEd = null;
        String usrIsEditor = "", bfNot = "";
        int rs, notifCount = 0;
        ResultSet qSet;

        try {

            // check if user is editor
            pstmtEd = con.prepareStatement("SELECT editor FROM user WHERE email = ?");
            pstmtEd.setString(1, email);
            qSet = pstmtEd.executeQuery();
            while(qSet.next()) {
                usrIsEditor = qSet.getString("editor");
            }

            if(usrIsEditor.equals("1") && isValidDate(launchDate)) {

                pstmt1 = con.prepareStatement("INSERT INTO album (title, description, genre, launch_date, editor_label, artist_name) VALUES (?, ?, ?, ?, ?, ?)" +
                        "ON DUPLICATE KEY UPDATE description = ?, genre = ?, launch_date = ?, editor_label = ?");
                pstmt1.setString(1, albName);
                pstmt1.setString(2, description);
                pstmt1.setString(3, genre);
                pstmt1.setString(4, launchDate);
                pstmt1.setString(5, editorLabel);
                pstmt1.setString(6, artName);

                pstmt1.setString(7, description);
                pstmt1.setString(8, genre);
                pstmt1.setString(9, launchDate);
                pstmt1.setString(10, editorLabel);

                rs = pstmt1.executeUpdate();

                // notify editors
                pstmtNot = con.prepareStatement("SELECT user_email FROM artist_user WHERE artist_name = ?");
                pstmtNot.setString(1, artName);
                qSet = pstmtNot.executeQuery();
                while (qSet.next()) {
                    notifCount++;
                    String notifEmail = qSet.getString("user_email");
                    notify += "notif|"+ notifEmail+";";
                }
                bfNot = "notif_count|" + Integer.toString(notifCount) + ";" + notify;
                rsp = "flag|"+id+";type|addalb;email|"+email+";result|y;"+bfNot+"msg|Album info added with success;";

                // user_email = email will ignore the duplicate key
                // the user will only have 1 instance in the table of Editors
                pstmt2 = con.prepareStatement("INSERT INTO artist_user (user_email, artist_name) VALUES (?, ?) ON DUPLICATE KEY UPDATE user_email = ?");
                pstmt2.setString(1, email);
                pstmt2.setString(2, albName);
                pstmt2.setString(3, email);

                System.out.println("Info of Artist " + artName + " added to DB with success");

                if (pstmt1 != null)
                    pstmt1.close();
                if (pstmt2 != null)
                    pstmt2.close();
                if (pstmtEd != null)
                    pstmtEd.close();
                if(pstmtNot != null)
                    pstmtNot.close();

            } else {
                // user is not editor
                if (usrIsEditor.equals("0"))
                    rsp = "flag|"+id+";type|addart;email|"+email+";result|n;msg|Only an editor can add a new albuns;";
                else
                    rsp = "flag|"+id+";type|addart;email|"+email+";result|n;msg|Date inserted has invalid format;";

            }

        } catch (SQLException e) {
            e.printStackTrace();

            switch (e.getErrorCode()) {

                case 1452:
                    System.out.println("Got ERROR: 1452 - Artist not found");
                    break;
            }

        }

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

    public void addMusic(String id, String albName, String title, String track, String email, String lyrics, String artiName, String code) {
        // Request  -> flag | s; type | addmusic; alb | bbbb; title | tttt; track | n; email | dddd;
        // Response -> flag | r; type | addmusic; title | tttt; email | dddd; result | (y/n); notif_count | n; notif | email; notif | email; [etc...]; msg | mmmmm;
        String rsp = "flag|"+id+";type|addmusic;title|"+title+";email|"+email+";result|n;msg|Failed to add music;";
        boolean isEditor = false;
        boolean found = false;
        boolean alreadyExists = false;
        String notify = "", artName = null;

        // BD

        PreparedStatement pstmt1 = null, pstmt2 = null, pstmtNot = null, pstmtAlb = null, pstmtEd = null;
        int rs, notifCount = 0;
        String usrIsEditor = "", bfNot = "", albID = "";
        ResultSet qSet;

        try {

            // check if user is editor
            pstmtEd = con.prepareStatement("SELECT editor FROM user WHERE email = ?");
            pstmtEd.setString(1, email);
            qSet = pstmtEd.executeQuery();
            while(qSet.next()) {
                usrIsEditor = qSet.getString("editor");
            }


            if(usrIsEditor.equals("1")) {


                pstmt1 = con.prepareStatement("INSERT INTO music (track, title, lyrics, album_id) VALUES (?, ?, ?, (SELECT id FROM album WHERE title = ? AND artist_name = ?)) " +
                                                   "ON DUPLICATE KEY UPDATE lyrics = ?, title = ?");
                pstmt1.setString(1, track);
                pstmt1.setString(2, title);
                pstmt1.setString(3, lyrics);
                pstmt1.setString(4, albName);
                pstmt1.setString(5, artiName);

                pstmt1.setString(6, lyrics);
                pstmt1.setString(7, title);

                rs = pstmt1.executeUpdate();


                // notify editors of that album
                pstmtNot = con.prepareStatement("SELECT user_email FROM artist_user WHERE artist_name = ?");
                pstmtNot.setString(1, artiName);
                qSet = pstmtNot.executeQuery();
                while (qSet.next()) {
                    notifCount++;
                    String notifEmail = qSet.getString("user_email");
                    notify += "notif|"+ notifEmail+";";
                }

                bfNot = "notif_count|" + Integer.toString(notifCount) + ";" + notify;
                rsp = "flag|"+id+";type|addalb;email|"+email+";result|y;"+bfNot+"msg|Music info added with success;";


                // add editor
                // user_email = email will ignore the duplicate key
                // the user will only have 1 instance in the table of Editors
                pstmt2 = con.prepareStatement("INSERT INTO artist_user (user_email, artist_name) VALUES (?, ?) ON DUPLICATE KEY UPDATE user_email = ?");

                pstmt2.setString(1, email);
                pstmt2.setString(2, albName);
                pstmt2.setString(3, email);

                System.out.println("Info of music " + title + " added to DB with success");

                if (pstmt1 != null)
                    pstmt1.close();
                if (pstmt2 != null)
                    pstmt2.close();
                if (pstmtAlb != null)
                    pstmtAlb.close();
                if (pstmtEd != null)
                    pstmtEd.close();
                if (pstmtNot != null)
                    pstmtNot.close();

            } else {
                // user is not editor
                rsp = "flag|"+id+";type|addmusic;title|"+title+";email|"+email+";result|n;msg|Only an Editor can add a new music;";
            }

        } catch (SQLException e) {
            rsp = "flag|"+id+";type|addmusic;title|"+title+";email|"+email+";result|n;msg|Something went wrong adding music;";
            switch (e.getErrorCode()) {

                case 1452:
                    System.out.println("Got ERROR: 1452 - Music not found");
                    break;
            }

        }
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
                writeCritic(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(5)[1], cleanMessage.get(6)[1],cleanMessage.get(cleanMessage.size()-1)[1]);// (album, critic, rate, email)
            } else if(cleanMessage.get(1)[1].equals("privilege")) {
                turnIntoEditor(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(cleanMessage.size()-1)[1]);       // (Editor, regularToEditor)
            } else if(cleanMessage.get(1)[1].equals("notifyfail")) {
                offUserNotified(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1]);    // (email, message)
            } else if(cleanMessage.get(1)[1].equals("share")) {
                share(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(5)[1], cleanMessage.get(6)[1], cleanMessage.get(cleanMessage.size() - 1)[1]); // (title, shareTo, uploader)
            } else if (cleanMessage.get(1)[1].equals("addart")) {
                addArtist(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1] ,cleanMessage.get(cleanMessage.size()-1)[1]);
            } else if (cleanMessage.get(1)[1].equals("addalb")) {
                System.out.println(cleanMessage.get(5)[1] + cleanMessage.get(6)[1] + cleanMessage.get(7)[1] + cleanMessage.get(8)[1] + cleanMessage.get(9)[1]);
                addAlbum(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(5)[1], cleanMessage.get(6)[1], cleanMessage.get(7)[1], cleanMessage.get(8)[1], cleanMessage.get(9)[1]);
            } else if (cleanMessage.get(1)[1].equals("addmusic")) {
                addMusic(cleanMessage.get(0)[1], cleanMessage.get(2)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(5)[1], cleanMessage.get(6)[1], cleanMessage.get(7)[1], cleanMessage.get(8)[1]);
            } else if(cleanMessage.get(1)[1].equals("requestTCPConnection") && cleanMessage.get(2)[1].equals("upload")) {
                // flag | s; type | requestTCPConnection; operation | upload; tittle | tttt; email | eeee;
                try {
                    uploadMusic(cleanMessage.get(0)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(5)[1], cleanMessage.get(6)[1], cleanMessage.get(cleanMessage.size() - 1)[1]); // (title, email)
                } catch (IOException e) {
                    e.printStackTrace();
                }

        } else if(cleanMessage.get(1)[1].equals("requestTCPConnection") && cleanMessage.get(2)[1].equals("download")) {
            // Request  -> flag | s; type | requestTCPConnection; operation | download; title | tttt; uploader | uuuu; email | eeee

            // "flag|"+id+";type|requestTCPConnection;operation|download;title|"+title+";uploader|"+uploader+";email|"+email+";hash|"+hash+";");
            try {
                // ID title uploader email hash
                downloadMusic(cleanMessage.get(0)[1], cleanMessage.get(3)[1], cleanMessage.get(4)[1], cleanMessage.get(5)[1], cleanMessage.get(6)[1], cleanMessage.get(7)[1], cleanMessage.get(cleanMessage.size() - 1)[1]); // (title, uploader, email)
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
