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
        String userInput = "";
        String[] tokens;
        boolean exit = false;
        String help = "Commands:\n"+
                    "- register [email] [password]\n"+
                    "- login [email] [password]\n"+
                    "- rate [album name] [1-5] [review] (max 300 chars)";

		try {
		    RMIServerInterface serverInterface = (RMIServerInterface) LocateRegistry.getRegistry(7000).lookup("rmiserver");
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
                } else if (tokens[0].equals("rate")) {
                    if (tokens.length >= 4) {
                        int stars = Integer.parseInt(tokens[2]);
                        if (stars <= 5 && stars >= 1) {
                            // Concatenar review
                            String review = "";
                            for (int i = 3; i < tokens.length; i++)
                                review = review + tokens[i] + " " ;

                            System.out.println(serverInterface.rateAlbum(stars, tokens[1], review, email));
                        } else {
                            System.out.println("Rating must be between 1 and 5");
                        }
                    } else {
                        System.out.println("Usage: rate [1-5] [review] (max 300 chars)");
                    }

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
