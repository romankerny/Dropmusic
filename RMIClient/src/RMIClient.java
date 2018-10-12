import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient extends UnicastRemoteObject implements RMIClientInterface {

    public RMIClient() throws RemoteException {
        super();
    }

    public void printOnClient(String msg) throws RemoteException {
        System.out.println(msg);
    }

    public static void main(String args[]) {


		try {
		    RMIServerInterface serverInterface = (RMIServerInterface) LocateRegistry.getRegistry(7000).lookup("rmiserver");

		    // Need to send interface to server first
		    serverInterface.subscribe((RMIClientInterface) new RMIClient());


            System.out.println(serverInterface.register("diogo", "cona"));
            System.out.println(serverInterface.register("roman", "conassa"));
        } catch (Exception e) {
            System.out.println("Exception in RMIClient.java main(): "+ e);
        }

    }
}
