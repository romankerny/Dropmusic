import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLOutput;
import java.util.Scanner;

import sun.audio.*;

import static java.lang.Thread.sleep;

public class RMIClient extends UnicastRemoteObject implements RMIClientInterface {

    private static String email = "";
    private static RMIServerInterface serverInterface;

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

    void waitForServer(RMIServerInterface serverInterface, RMIClient client) throws RemoteException, MalformedURLException, NotBoundException {
        try {
            sleep((long) (2500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.serverInterface = (RMIServerInterface) Naming.lookup("rmiserver");
        this.serverInterface.subscribe(email, client);
    }



    public static void main(String args[]) throws IOException {

        Scanner sc = new Scanner(System.in);
        Scanner scanner;
        String userInput;
        String[] tokens;
        RMIClient client = new RMIClient();
        boolean exit = false;
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
                    "- promote [email]";

		try {
		    serverInterface = (RMIServerInterface) Naming.lookup("rmiserver");
            System.out.println(help);
            while(!exit) {
                userInput = sc.nextLine();
                tokens = userInput.split(" ");

                if (tokens[0].equals("register")) {
                    if (tokens.length == 3) {

                        try {
                            System.out.println(serverInterface.register(tokens[1], tokens[2]));
                        } catch (RemoteException re) {
                            client.waitForServer(serverInterface, client);
                            System.out.println(serverInterface.register(tokens[1], tokens[2]));
                        }
                    } else {
                        System.out.println("Usage: register [email] [password]");

                    }





                } else if (tokens[0].equals("login")) {
                    if (tokens.length == 3) {

                        try {
                            System.out.println(serverInterface.login(tokens[1], tokens[2], client));
                            email = tokens[2];
                        } catch (RemoteException re) {
                            client.waitForServer(serverInterface, client);
                            System.out.println(serverInterface.login(tokens[1], tokens[2], client));
                        }

                    } else {
                        System.out.println("Usage: login [email] [password]");
                    }





                } else if (tokens[0].equals("logout")) {
                    try {
                        System.out.println(serverInterface.logout(email));
                    } catch (RemoteException e) {
                        client.waitForServer(serverInterface, client);
                        System.out.println(serverInterface.login(tokens[1], tokens[2], client));
                    }




                } else if (tokens[0].equals("search")) {
                    if (tokens.length >= 3) {
                        String keyword = strCatSpaces(tokens, 2);
                        try {
                            System.out.println(serverInterface.search(tokens[1], keyword));
                        } catch (RemoteException re) {
                            System.out.println("aqui");
                            client.waitForServer(serverInterface, client);
                            System.out.println(serverInterface.search(tokens[1], keyword));
                        }
                    } else {
                        System.out.println("Usage: search {art, alb} [keyword]");
                    }




                } else if (tokens[0].equals("rate")) {
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

                        try {
                            System.out.println(serverInterface.rateAlbum(stars, albumName, review, email));
                        } catch (RemoteException re) {
                            client.waitForServer(serverInterface, client);
                            System.out.println(serverInterface.rateAlbum(stars, albumName, review, email));
                        }

                    } else {
                        System.out.println("Usage: rate [album name] [1-5] [review] (max 300 chars)");
                    }




                } else if (tokens[0].equals("promote")) {
                    if (tokens.length == 2) {
                        try {
                            System.out.println(serverInterface.regularToEditor(email, tokens[1]));
                        } catch (RemoteException re) {
                            client.waitForServer(serverInterface, client);
                            System.out.println(serverInterface.regularToEditor(email, tokens[1]));
                        }
                    } else {
                        System.out.println("Usage: promote [user]");
                    }




                } else if(tokens[0].equals("upload")) {

                    if (tokens.length >= 3) {
                        int port;
                        String musicName = strCatSpaces(tokens, 2);
                        File musicFile = new File(tokens[1]);
                        FileInputStream fis = new FileInputStream(musicFile);

                        try {
                            port = serverInterface.uploadMusic(musicName, email);
                        } catch (RemoteException re) {
                            client.waitForServer(serverInterface, client);
                            port = serverInterface.uploadMusic(musicName, email);
                        }
                        // criar socket e mandar pa la o nosso file
                        Socket s = new Socket("localhost", port);
                        if (s.isConnected()) {

                            DataOutputStream out = new DataOutputStream(s.getOutputStream());
                            // Send filename and size before actual bytes
                            out.writeUTF(musicFile.getName());
                            out.writeLong(musicFile.length());

                            byte buffer[] = new byte[4096];
                            int count;
                            while ((count = fis.read(buffer)) != -1)
                                out.write(buffer, 0, count);

                            out.close();

                        }
                        s.close();
                    } else {
                        System.out.println("Usage: upload [path] [music name]");
                    }

                } else if (tokens[0].equals("download")) {

                    if (tokens.length >= 3) {
                        int port;
                        String musicName = strCatSpaces(tokens, 2);
                        try {
                            port = serverInterface.downloadMusic(musicName, tokens[1], email);
                        } catch (RemoteException e) {
                            client.waitForServer(serverInterface, client);
                            port = serverInterface.downloadMusic(musicName, tokens[1], email);
                        }
                        // Criar socket e receber o File
                        Socket s = new Socket("localhost", port);
                        if (s.isConnected()) {
                            DataInputStream in = new DataInputStream(s.getInputStream());
                            String filename = in.readUTF();
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream((new File(filename))));

                            byte buffer[] = new byte[4096];
                            int count;
                            while ((count = in.read(buffer)) != -1)
                                bos.write(buffer, 0, count);
                            bos.flush();
                            bos.close();
                            in.close();

                        }
                        s.close();
                    } else {
                        System.out.println("Usage: download [user] [music name]");
                    }

                } else if (tokens[0].equals("share")) {

                    if (tokens.length >= 3) {
                        String musicName = strCatSpaces(tokens, 2);
                        try {
                            System.out.println(serverInterface.share(musicName, tokens[1], email));
                        } catch (RemoteException e) {
                            client.waitForServer(serverInterface, client);
                            System.out.println(serverInterface.share(musicName, tokens[1], email));
                        }

                    } else {
                        System.out.println("Usage: share [user] [music name]");
                    }



                } else if (tokens[0].equals("help")) {
                    System.out.println(help);
                } else {
                    System.out.println("Invalid command. Type 'help' for more info");
                }
            }


        } catch (Exception e) {
            System.out.println("Exception in RMIClient.java main(): "+ e);
        }

    }
}
