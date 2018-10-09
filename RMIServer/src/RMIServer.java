import java.net.MalformedURLException;
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

    public String sayHello() throws RemoteException {
        System.out.println("print do lado do servidor...!.");

        return "Hello, World!";
    }

    // =========================================================
    public static void main(String args[]) {

        try {
            RMIServer h = new RMIServer();
            Registry r = LocateRegistry.createRegistry(6000);
            r.rebind("objrmiserver", h);
            System.out.println("Hello Server ready.");
        } catch (RemoteException re) {
            System.out.println("Exception in RMIServer.main: " + re);
        }

    }
}
