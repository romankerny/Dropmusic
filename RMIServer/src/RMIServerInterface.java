import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerInterface extends Remote {
    public void    printOnServer(String s) throws RemoteException;

    // 1.
    public String register(String name, String password) throws RemoteException;
    public String login(String email, String password, RMIClientInterface client) throws RemoteException;
    public String logout(String email) throws RemoteException;

    // 3.
    public String search(String param, String keyword) throws RemoteException;

    // 5.
    public String rateAlbum(int stars, String albumName, String review, String email) throws RemoteException;

    // 6.
    public String regularToEditor(String editor, String regular) throws RemoteException;

    // 10.
    public int uploadMusic(String title, String email) throws RemoteException;

    // 11.

    // 12.
    public int downloadMusic(String title, String email) throws RemoteException;
}
