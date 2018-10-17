import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.io.InputStream;
import java.io.FileInputStream;
import sun.audio.*;

public class RMIClient extends UnicastRemoteObject implements RMIClientInterface {

    private static String email = "";

    public RMIClient() throws RemoteException {
        super();
    }

    public void printOnClient(String msg) throws RemoteException {
        System.out.println(msg);
    }

    public String getEmail() throws RemoteException {
        return email;
    }



    public static void main(String args[]) {

        Scanner sc = new Scanner(System.in);
        Scanner scanner;
        String userInput;
        String[] tokens;
        int portTCP = 6565;
        boolean exit = false;
        String help = "Commands:\n"+
                    "- register [email] [password]\n"+
                    "- login [email] [password]\n"+
                    "- logout (no arguments)\n"+
                    "- search {art, alb} keyword"+
                    "- rate [album name] [1-5] [review] (max 300 chars)\n"+
                    "- upload music_file\n"+
                    "Editor-specific:\n"+
                    "- promote [email]";

		try {
		    RMIServerInterface serverInterface = (RMIServerInterface) Naming.lookup("rmiserver");
            System.out.println(help);
            while(!exit) {
                userInput = sc.nextLine();
                tokens = userInput.split(" ");

                if (tokens[0].equals("register")) {
                    if (tokens.length == 3) {
                        System.out.println(serverInterface.register(tokens[1], tokens[2]));
                    } else {
                        System.out.println("Usage: register [email] [password]");

                    }
                } else if (tokens[0].equals("login")) {
                    if (tokens.length == 3) {
                        System.out.println(serverInterface.login(tokens[1], tokens[2], new RMIClient()));
                        email = tokens[2];
                    } else {
                        System.out.println("Usage: login [email] [password]");
                    }
                } else if (tokens[0].equals("logout")) {
                    System.out.println(serverInterface.logout(email));
                } else if (tokens[0].equals("search")) {
                    if (tokens.length >= 3) {

                        String keyword = "";

                        for (int i = 2; i < tokens.length; i++)
                            keyword = keyword + tokens[i] +" ";
                        keyword = keyword.replaceFirst(".$", "");

                        System.out.println(serverInterface.search(tokens[1], keyword));
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


                        System.out.println(serverInterface.rateAlbum(stars, albumName, review, email));

                    } else {
                        System.out.println("Usage: rate [album name] [1-5] [review] (max 300 chars)");
                    }
                } else if (tokens[0].equals("promote")) {
                    if (tokens.length == 2) {
                        System.out.println(serverInterface.regularToEditor(email, tokens[1]));
                    } else {
                        System.out.println("Usage: promote [user]");
                    }
                } else if(tokens[0].equals("upload")) {
                        // serverInterface.upload(email, portTCP);

                        // criar socket e esperar q o cliente se ligue




                } else if (tokens[0].equals("help")) {
                    System.out.println(help);
                } else {
                    System.out.println("Invalid command. Type 'help' for more info");
                }
            }


            /*
            testes fudidos
            InputStream test = new FileInputStream("/home/diogo/Desktop/spaceDiscoMusic.mp3");

            AudioStream audioStream = new AudioStream(test);

            // play the audio clip with the audioplayer class
            AudioPlayer.player.start(audioStream);
            */
        } catch (Exception e) {
            System.out.println("Exception in RMIClient.java main(): "+ e);
        }

    }
}
