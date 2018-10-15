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

    public RMIClient() throws RemoteException {
        super();
    }

    public void printOnClient(String msg) throws RemoteException {
        System.out.println(msg);
    }

    public static void main(String args[]) {

        Scanner scan = new Scanner(System.in);
        String input, email, password;
        boolean exit = false, go = false;

		try {
		    RMIServerInterface serverInterface = (RMIServerInterface) LocateRegistry.getRegistry(7000).lookup("rmiserver");

		    // Need to send interface to server first
		    serverInterface.subscribe((RMIClientInterface) new RMIClient());
		    

            System.out.println("1. Get an account\n2. Login");
            while(!exit) {
                input = scan.nextLine();
                if(input.equals("1")){
                    System.out.println("Email: ");
                    email = scan.nextLine();
                    System.out.println("Password: ");
                    password = scan.nextLine();
                    serverInterface.register(email, password);
                    go = true;

                } else if(input.equals("2") | go) {
                    System.out.println("Email: ");
                    email = scan.nextLine();
                    System.out.println("Password: ");
                    password = scan.nextLine();
                    // serverInterface.login(email, password);
                    exit = true;
                }
            }
            System.out.println("1.\n2.\n3.\n4.\n5.\n");





            /*
            tstes fudidos
            InputStream test = new FileInputStream("/home/diogo/Desktop/spaceDiscoMusic.mp3");

            AudioStream audioStream = new AudioStream(test);

            // play the audio clip with the audioplayer class
            AudioPlayer.player.start(audioStream);
*/



            System.out.println(serverInterface.register("diogo", "cona"));
            System.out.println(serverInterface.register("roman", "conassa"));
        } catch (Exception e) {
            System.out.println("Exception in RMIClient.java main(): "+ e);
        }

    }
}
