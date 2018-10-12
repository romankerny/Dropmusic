import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerInterface extends Remote {
    public void printOnServer(String s) throws RemoteException;
    public String sendPila() throws java.rmi.RemoteException;
    public String register(String name, String password) throws RemoteException;
    public void subscribe(RMIClientInterface client) throws RemoteException;
}
