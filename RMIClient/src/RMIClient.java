import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient extends UnicastRemoteObject implements Remote {

    public RMIClient() throws RemoteException {
        super();
    }

    public static void main(String args[]) {


		try {
		    RMIServerInterface serverInterface = (RMIServerInterface) LocateRegistry.getRegistry(7000).lookup("rmiserver");
            System.out.println(serverInterface.register("diogo", "cona"));
        } catch (Exception e) {
            System.out.println("Exception in RMIClient.java main(): "+ e);
        }

    }
}
