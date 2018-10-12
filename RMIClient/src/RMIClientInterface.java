import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientInterface extends Remote {
    public void printOnClient(String msg) throws RemoteException;
}
