import java.rmi.Remote;

public interface RMIServerInterface extends Remote {
    public void printOnServer(String s) throws java.rmi.RemoteException;
}
