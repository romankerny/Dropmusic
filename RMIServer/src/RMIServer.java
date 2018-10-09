import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.MalformedURLException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public RMIServer() throws RemoteException {
        super();
    }


    public void printOnServer(String s) throws RemoteException {
        System.out.println("> " + s);
    }

    // =========================================================
    public static void main(String args[]) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            RMIServer h = new RMIServer();
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("rmiserver", h);
            System.out.println("RMIServer ready.");
        } catch (RemoteException re) {
            System.out.println("Exception in RMIServer.main: " + re);
        }

    }
}
