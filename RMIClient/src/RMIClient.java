import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLOutput;
import java.util.Scanner;

import static java.lang.Thread.sleep;

/**
 * This class will be runned in the Client's machine.
 * It basically gets user strings via command line, parses them, and then makes remote invocations on the RMIServer
 * via it's interface based on them.
 * It connects to the RMIServer with the IP provided in the args array when starting the program
 * If for some reason RMIServers go off during the execution of a RMIServer method the client will hold and wait
 * for the reconection - this happens in the method waitForServer()
 * and then retry to call the method.
 *
 */
public class RMIClient extends UnicastRemoteObject implements RMIClientInterface {

    private static String email = "";
    private RMIServerInterface serverInterface;
    private static String ip;

    private RMIClient() throws RemoteException {
        super();
    }

    public void printOnClient(String msg) throws RemoteException {
        System.out.println(msg);
    }

    /**
     * called by RMIServer, simply returns the email of the Client
     * @return
     * @throws RemoteException
     */
    public String getEmail() throws RemoteException {
        return email;
    }

    /**
     * function used to simplify Strings
     * @param tokens
     * @param index
     * @return
     */

    private static String strCatSpaces(String[] tokens, int index) {
        String str = "";

        for (int i = index; i < tokens.length; i++)
            str += tokens[i] + " ";
        str = str.replaceAll(".$", "");
        return str;
    }

    /**
     * In main when an RMIServer method call throws an RemoteExeption this method is called in the handling of the
     * throw
     * The method sleeps for 1 sec and then try's to lookup the registry of the RMIServer
     * this happens mostly when an RMI Server goes down
     * if the lookup happens w/ successes than the client also call's subscribe to send it's interface to the new RMIServer
     *
     * @param client
     */

    private void waitForServer(RMIClient client) {

        boolean exit = false;
        while(!exit) {
            try {
                sleep((long) (1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                this.serverInterface = (RMIServerInterface) LocateRegistry.getRegistry(ip, 1099).lookup("rmiserver");
                this.serverInterface.subscribe(email, client);
                exit = true;
            } catch (ConnectException e) {
                System.out.println("RMI server down, retrying...");
            } catch (NotBoundException v) {
                System.out.println("RMI server down, retrying...");
            } catch (RemoteException tt) {
                System.out.println("RMI server down, retrying...");
            }
        }
    }



    public static void main(String args[]) throws IOException, NotBoundException {


        // read RMIServer's IP from the args array
        if (args.length != 1) {
            System.out.println("Missing IP argument");
            System.exit(0);
        } else {
            ip = args[0];
        }
        // this line is very important and it works around a serious issue that we found when connecting Client-ServerRMI
        // in different machines
        // it assures that java.rmi owns the right IP of the machine where it's running on.
        // For some reason without this line java.rmi assumes as if the machine's IP is the loopback address
        System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());

        Scanner sc = new Scanner(System.in);
        Scanner scanner;
        String userInput = "";
        String[] tokens = null;
        RMIClient client = new RMIClient();
        boolean exit = false, rmiConnected = false;


        String help = "Commands:\n"+
                    "- register [email] [password]\n"+
                    "- login [email] [password]\n"+
                    "- logout (no arguments)\n\n"+
                    "- search {art, alb} keyword\n"+
                    "- rate [1-5]\n\n"+
                    "- upload [track #]\n"+
                    "- download [user] [music title]\n"+
                    "- share [user] [music title]"+
                    "  \nEditor-specific:\n"+
                    "- promote [email]\n"+
                    "- add {art, alb, mus} [name]\n";


        System.out.println("Connecting to: " + ip);

        // connects to RMIServer when the Client first runs, if it fails the code won't exit the while loop
        while (!rmiConnected)
            try {
                client.serverInterface = (RMIServerInterface) LocateRegistry.getRegistry(ip, 1099).lookup("rmiserver");
                rmiConnected = true;
            } catch (ConnectException e) {
                System.out.println("Connecting to " + ip + " failed, retrying ...");
                try {
                    sleep((long) (1000));
                } catch (InterruptedException re) {
                    e.printStackTrace();
                }
            }

        System.out.println(help);
        boolean askInput = true;


        while (!exit) {
            try {
                // get instruction from user
                if(askInput) {
                    userInput = sc.nextLine();
                    tokens = userInput.split(" ");
                }

                if (tokens[0].equals("register")) {
                    if (tokens.length == 3) {
                        System.out.println(client.serverInterface.register(tokens[1], tokens[2]));
                    } else {
                        System.out.println("Usage: register [email] [password]");
                    }

                } else if (tokens[0].equals("login")) {

                    if (tokens.length == 3) {
                        System.out.println(client.serverInterface.login(tokens[1], tokens[2], client));
                        email = tokens[1];
                    } else {
                        System.out.println("Usage: login [email] [password]");
                    }


                } else if (tokens[0].equals("logout")) {

                    System.out.println(client.serverInterface.logout(email));
                    email = "";

                } else if (!email.equals("") && tokens[0].equals("search")
                        && (tokens[1].equals("art") || tokens[1].equals("alb") || tokens[1].equals("gen"))) {

                    if (tokens.length >= 3) {
                        String keyword = strCatSpaces(tokens, 2);
                        System.out.println(client.serverInterface.search(tokens[1], keyword));
                    } else {
                        System.out.println("Usage: search {art, alb, gen} [keyword]");
                    }

                } else if (tokens[0].equals("add") && !email.equals("")) {

                    if (tokens.length >= 3) {
                        String name = strCatSpaces(tokens, 2);

                        switch (tokens[1]) {
                            case "art": {
                                String description;
                                System.out.print("Artist description: ");
                                description = sc.nextLine();
                                System.out.println(client.serverInterface.addArtist(name, description, email));
                                break;
                            }
                            case "alb": {
                                String artist, description, genre, launchDate, editorLabel;
                                System.out.print("Artist's name: ");
                                artist = sc.nextLine();
                                System.out.print("Album description: ");
                                description = sc.nextLine();
                                System.out.print("Album genre: ");
                                genre = sc.nextLine();
                                System.out.println("Launch Date: ");
                                launchDate = sc.nextLine();
                                System.out.println("Editor Label: ");
                                editorLabel = sc.nextLine();
                                System.out.println(client.serverInterface.addAlbum(artist, name, description, genre, email, launchDate, editorLabel));
                                break;
                            }
                            case "mus":
                                String albumName, track, lyrics, artistName;
                                System.out.print("Track #: ");
                                track = sc.nextLine();
                                System.out.print("Album name: ");
                                albumName = sc.nextLine();
                                System.out.print("Artist name: ");
                                artistName = sc.nextLine();
                                System.out.print("Lyrics: ");
                                lyrics = sc.nextLine();
                                System.out.println(client.serverInterface.addMusic(name, track, albumName, email, lyrics, artistName));
                                break;
                        }

                    } else {
                        System.out.println("Usage: add {art, alb, mus} [name]");
                    }

                } else if (tokens[0].equals("rate") && !email.equals("")) {

                    if (tokens.length == 2) {
                        int stars = Integer.parseInt(tokens[1]);

                        System.out.print("Artist's name: ");
                        String artistName = sc.nextLine();
                        System.out.print(artistName +"'s album name: ");
                        String albumName = sc.nextLine();
                        System.out.print(artistName +" review (max 300 characters): ");
                        String review = sc.nextLine();

                        if (review.length() <= 300)
                            System.out.println(client.serverInterface.rateAlbum(stars, artistName, albumName, review, email));
                        else
                            System.out.println("Character limit exceeded for review (max 300)");

                    } else {
                        System.out.println("Usage: rate [1-5]");
                    }

                } else if (tokens[0].equals("promote") && !email.equals("")) {

                    if (tokens.length == 2) {
                        System.out.println(client.serverInterface.regularToEditor(email, tokens[1]));
                    } else {
                        System.out.println("Usage: promote [user]");
                    }

                } else if (tokens[0].equals("upload") && !email.equals("")) {

                    if (tokens.length == 2) {
                        int port;
                        int trackNumber = Integer.parseInt(tokens[1]);
                        System.out.print("Path/to/file: ");
                        String path = sc.nextLine();
                        System.out.print("Artist's name: ");
                        String artistName = sc.nextLine();
                        System.out.print(artistName +"'s album name: ");
                        String albumName = sc.nextLine();
                        String ipport = client.serverInterface.uploadMusic(artistName, albumName, Integer.toString(trackNumber), email);

                        // If first char is a number then we received an IP:Port, if not then it's an error message
                        if (Character.isDigit(ipport.charAt(0))) {
                            String[] ipPort = ipport.split(" ");

                            // Create socket
                            Socket s = new Socket(ipPort[0], Integer.parseInt(ipPort[1]));
                            if (s.isConnected()) {

                                File musicFile = new File(path);
                                FileInputStream fis = new FileInputStream(musicFile);

                                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                                // Send filename first
                                out.writeUTF(musicFile.getName());
                                byte buffer[] = new byte[4096];
                                int count;
                                System.out.println("Uploading file...");
                                while ((count = fis.read(buffer)) != -1) {
                                    out.write(buffer, 0, count);
                                }
                                out.close();
                                s.close();
                                System.out.println("Done");
                            }
                        } else {
                            System.out.println(ipport);
                        }
                    } else {
                        System.out.println("Usage: upload [music name]");
                    }

                } else if (tokens[0].equals("download") && !email.equals("")) {

                    if (tokens.length >= 3) {
                        String albumName, artistName;
                        String musicName = strCatSpaces(tokens, 2);
                        System.out.print("Album name: ");
                        albumName = sc.nextLine();
                        System.out.print("Artist name: ");
                        artistName = sc.nextLine();
                        String r = client.serverInterface.downloadMusic(musicName, tokens[1], email, albumName, artistName);
                        if (!r.equals("Music file not found.")) {

                            String[] ipPort = r.split(" ");

                            // Create socket and receive file
                            Socket s = new Socket(ipPort[0], Integer.parseInt(ipPort[1]));
                            if (s.isConnected()) {
                                DataInputStream in = new DataInputStream(s.getInputStream());
                                String filename = in.readUTF();
                                FileOutputStream fos = new FileOutputStream((new File(filename)));
                                byte buffer[] = new byte[4096];
                                int count;
                                System.out.println("Downloading `" + filename + "`");
                                while ((count = in.read(buffer)) != -1) {
                                    fos.write(buffer, 0, count);
                                }
                                in.close();
                                System.out.println("Done");
                            }
                            s.close();
                        } else {
                            System.out.println(r);
                        }

                    } else {
                        System.out.println("Usage: download [user] [music name]");
                    }

                } else if (tokens[0].equals("share") && !email.equals("")) {

                    if (tokens.length >= 3) {
                        String musicName = strCatSpaces(tokens, 2);
                        System.out.println(client.serverInterface.share(musicName, tokens[1], email));
                    } else {
                        System.out.println("Usage: share [user] [music name]");
                    }
                } else if (tokens[0].equals("help")) {
                    System.out.println(help);
                } else {
                    System.out.println("Invalid command. Type 'help' for more info");
                }

                // if an instruction runs nicely then the client can receive another one
                askInput = true;


            } catch (RemoteException b) {
                client.waitForServer(client);
                // if an instruction fails than the client will recall it
                askInput = false;
            } catch (FileNotFoundException nf) {
                System.out.println("Couldn't find your file to upload");
            }

        } // while

    }
}
