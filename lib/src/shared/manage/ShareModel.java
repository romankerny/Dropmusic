package shared.manage;

import shared.RMIServerInterface;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ShareModel implements Serializable {

    private String email;
    private String albumTitle;
    private String artistName;
    private String musicTitle;

    public ShareModel(String email, String albumTitle, String artistName, String musicTitle) {

        setEmail(email);
        setMusicTitle(musicTitle);
        setAlbumTitle(albumTitle);
        setArtistName(artistName);
    }

    public ShareModel()
    {
        this(null , null, null, null);
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public boolean shareMusic(String emailToShare, String artist, String album, String title, String email) throws RemoteException {

        boolean r = false;
        RMIServerInterface server = null;
        String rsp;

        try
        {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");

            if (server.shareMusic(emailToShare, artist, album, title, email))
            {
                r = true;
            }
            else
            {
                r = false;
            }
        }
        catch(NotBoundException | RemoteException e) {
            e.printStackTrace();
        }


        return r;
    }
}
