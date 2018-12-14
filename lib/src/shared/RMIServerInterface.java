package shared;

import shared.manage.Music;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This class contains all remote methods from shared.RMIServer class.
 * <p>
 * the isAlive() method is the only one that is used only between RMIServers, all the others are called by clients.
 * <p>
 * <p>
 * the subscribe() method is called either when a clients logs in [runs in the login() method in shared.RMIServer]
 * or when a client loses it's connection to the server and has to re-send it's interface [runned in the waitforServer() method
 * in the RMIClient code].
 */

public interface RMIServerInterface extends Remote {

    // 1.
    public String register(String name, String password) throws RemoteException;
    public String login(String email, String password, RMIClientInterface client) throws RemoteException;
    String login(String email, String password) throws RemoteException;
    public String logout(String email) throws RemoteException;

    // 2.
    public String addArtist(String artist, String details, String email) throws RemoteException;
    public String addAlbum(String artist, String albumTitle, String description, String genre, String email, String launchDate, String editorLabel) throws RemoteException;
    public String addMusic(String musicTitle, String track, String albumTitle, String email, String lyrics, String artistName) throws  RemoteException;

    // 3.
    public String search(String param, String keyword) throws RemoteException;
    ArrayList<Object> searchArtist(String keyword) throws RemoteException;
    ArrayList<Object> searchAlbum(String keyword) throws RemoteException;
    ArrayList<Object> searchMusic(String keyword) throws RemoteException;
    String getMusicURL(String artist, String album, String title, String email) throws RemoteException;
    public ArrayList<String> getEditors(String artistName)  throws RemoteException;


    public String associateDropBox() throws RemoteException;
    public String associateDropBoxBeforeLogin() throws RemoteException;
    public boolean setToken(String email, String code) throws RemoteException;
    public String canLogin(String code) throws RemoteException;
    public boolean associateMusic(String email, String artist, String album, String musicTitle, String fileName) throws RemoteException;



    // 5.
    public String rateAlbum(int stars, String artistName, String albumName, String review, String email) throws RemoteException;

    // 6.
    public String regularToEditor(String editor, String regular) throws RemoteException;

    // 10.
    public String uploadMusic(String artistName, String albumName, String track, String uploader) throws RemoteException;

    // 11.
    public String share(String artist, String album, String track, String shareTo, String uploader) throws RemoteException;
    // 12.
    public String downloadMusic(String title, String uploader, String email, String albumName, String artistName) throws RemoteException;

    // RMIBackup Test

    public void subscribe(String email, RMIClientInterface clientInterface) throws RemoteException;
    public boolean isAlive() throws RemoteException;

}
