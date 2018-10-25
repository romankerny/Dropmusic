import java.io.*;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLOutput;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class RMIClient extends UnicastRemoteObject implements RMIClientInterface {

    private static String email = "";
    private RMIServerInterface serverInterface;
    private static String ip;

    public RMIClient() throws RemoteException {
        super();
    }

    public void printOnClient(String msg) throws RemoteException {
        System.out.println(msg);
    }

    public String getEmail() throws RemoteException {
        return email;
    }

    public static String strCatSpaces(String[] tokens, int index) {
        String str = "";

        for (int i = index; i < tokens.length; i++)
            str += tokens[i] + " ";
        str = str.replaceAll(".$", "");
        return str;
    }

    void waitForServer(RMIClient client) {

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

        if (args.length != 1) {
            System.out.println("Missing IP argument");
            System.exit(0);
        } else {
            ip = args[0];
        }

        System.setProperty("java.rmi.server.hostname", ip);

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
                    "- rate [album name] [1-5] [review] (max 300 chars)\n\n"+
                    "- upload [path/to/file] [music title]\n"+
                    "- download [user] [music title]\n"+
                    "- share [user] [music title]"+
                    "  \nEditor-specific:\n"+
                    "- promote [email]\n"+
                    "- add {art, alb, mus} [name]\n";


        System.out.println("Connecting to: " + ip);

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

                        if (tokens[1].equals("art")) {
                            String description;
                            System.out.print("Artist description: ");
                            description = sc.nextLine();
                            System.out.println(client.serverInterface.addArtist(name, description, email));
                        } else if (tokens[1].equals("alb")) {
                            String artist, description, genre;
                            System.out.print("Artist's name: ");
                            artist = sc.nextLine();
                            System.out.print("Album description: ");
                            description = sc.nextLine();
                            System.out.print("Album genre: ");
                            genre = sc.nextLine();
                            System.out.println(client.serverInterface.addAlbum(artist, name, description, genre, email));
                        } else if (tokens[1].equals("mus")) {
                            String albumName, track;
                            System.out.print("Track #: ");
                            track = sc.nextLine();
                            System.out.print("Album name: ");
                            albumName = sc.nextLine();
                            System.out.println(client.serverInterface.addMusic(name, track, albumName, email));
                        }

                    } else {
                        System.out.println("Usage: add {art, alb, mus} [name]");
                    }

                } else if (tokens[0].equals("rate") && !email.equals("")) {

                    if (tokens.length >= 4) {
                        String albumName = "";
                        int stars;
                        String review = "";
                        scanner = new Scanner(userInput);
                        scanner.next(); // Skip "rate"
                        while (!scanner.hasNextInt())
                            albumName = albumName + scanner.next() + " ";
                        albumName = albumName.replaceFirst(".$", "");
                        stars = scanner.nextInt();
                        while (scanner.hasNext())
                            review = review + scanner.next() + " ";
                        review = review.replaceFirst(".$", "");
                        System.out.println(client.serverInterface.rateAlbum(stars, albumName, review, email));

                    } else {
                        System.out.println("Usage: rate [album name] [1-5] [review] (max 300 chars)");
                    }

                } else if (tokens[0].equals("promote") && !email.equals("")) {

                    if (tokens.length == 2) {
                        System.out.println(client.serverInterface.regularToEditor(email, tokens[1]));
                    } else {
                        System.out.println("Usage: promote [user]");
                    }

                } else if (tokens[0].equals("upload") && !email.equals("")) {

                    if (tokens.length >= 3) {
                        int port;
                        String musicName = strCatSpaces(tokens, 2);
                        String ipport = client.serverInterface.uploadMusic(musicName, email);
                        String [] ipPort = ipport.split(" ");

                        // Criar socket e receber o File
                        Socket s = new Socket(ipPort[0], Integer.parseInt(ipPort[1]));
                        if (s.isConnected()) {

                            File musicFile = new File(tokens[1]);
                            FileInputStream fis = new FileInputStream(musicFile);

                            DataOutputStream out = new DataOutputStream(s.getOutputStream());
                            // Send filename and size before actual bytes
                            out.writeUTF(musicFile.getName());
                            out.writeLong(musicFile.length());
                            byte buffer[] = new byte[4096];
                            int count;
                            System.out.println("Uploading file...");
                            while ((count = fis.read(buffer)) != -1) {
                                out.write(buffer, 0, count);
                            }
                            out.close();
                            System.out.println("Done");
                        }
                        s.close();
                    } else {
                        System.out.println("Usage: upload [path] [music name]");
                    }

                } else if (tokens[0].equals("download") && !email.equals("")) {

                    if (tokens.length >= 3) {

                        String musicName = strCatSpaces(tokens, 2);
                        String ipport = client.serverInterface.downloadMusic(musicName, tokens[1], email);
                        String [] ipPort = ipport.split(" ");

                        // Criar socket e receber o File
                        Socket s = new Socket(ipPort[0], Integer.parseInt(ipPort[1]));
                        if (s.isConnected()) {
                            DataInputStream in = new DataInputStream(s.getInputStream());
                            String filename = in.readUTF();
                            FileOutputStream fos = new FileOutputStream((new File(filename)));
                            byte buffer[] = new byte[4096];
                            int count;
                            System.out.println("Downloading `"+filename+"`");
                            while ((count = in.read(buffer)) != -1) {
                                fos.write(buffer, 0, count);
                            }
                            fos.flush();
                            fos.close();
                            in.close();
                            s.close();
                            System.out.println("Done");
                        }
                        s.close();
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

                askInput = true;

            } catch (RemoteException b) {
                client.waitForServer(client);
                askInput = false;
            } catch (FileNotFoundException nf) {
                System.out.println("Couldn't find your file to upload");
            }

        } // while

    }
}
