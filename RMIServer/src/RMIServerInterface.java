import java.rmi.Remote;

public interface RMIServerInterface extends Remote {
    public void printOnServer(String s) throws java.rmi.RemoteException;
    public String sendPila() throws java.rmi.RemoteException;
    public String register(String name, String password) throws java.rmi.RemoteException;
}
