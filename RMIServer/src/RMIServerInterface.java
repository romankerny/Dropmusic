import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * This class contains all remote methods from RMIServer class.
 *
 * the isAlive() method is the only one that is used only between RMIServers, all the others are called by clients.
 *
 *
 * the subscribe() method is called either when a clients logs in [runs in the login() method in RMIServer]
 * or when a client loses it's connection to the server and has to re-send it's interface [runned in the waitforServer() method
 * in the RMIClient code].
 *
 */

public interface RMIServerInterface extends Remote {

    // 1.
    public String register(String name, String password) throws RemoteException;
    public String login(String email, String password, RMIClientInterface client) throws RemoteException;
    public String logout(String email) throws RemoteException;

    // 2.
    public String addArtist(String artist, String details, String email) throws RemoteException;
    public String addAlbum(String artist, String albumTitle, String description, String genre, String email) throws  RemoteException;
    public String addMusic(String musicTitle, String track, String albumTitle , String email) throws  RemoteException;

        // 3.
    public String search(String param, String keyword) throws RemoteException;

    // 5.
    public String rateAlbum(int stars, String artistName, String albumName, String review, String email) throws RemoteException;

    // 6.
    public String regularToEditor(String editor, String regular) throws RemoteException;

    // 10.
    public String uploadMusic(String title, String uploader) throws RemoteException;

    // 11.
    public String share(String title, String shareTo, String uploader) throws RemoteException;
    // 12.
    public String downloadMusic(String title, String uploader, String email) throws RemoteException;

    // RMIBackup Test

    public void subscribe(String email, RMIClientInterface clientInterface) throws RemoteException;
    public boolean isAlive() throws RemoteException;

}
