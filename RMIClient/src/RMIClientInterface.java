import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote Interface of client
 * getEmail() was used for debugging only
 * printOnClient() is used by the RMIServer to print notifications in the clients command line.
 */
public interface RMIClientInterface extends Remote {
    public void printOnClient(String msg) throws RemoteException;
    public String getEmail() throws RemoteException;
}
