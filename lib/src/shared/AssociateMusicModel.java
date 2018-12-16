package shared;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;

public class AssociateMusicModel implements Serializable {


        private String fileName;
        private String albumTitle;
        private String artistName;
        private String musicTitle;

        public AssociateMusicModel(String fileName, String albumTitle, String artistName, String musicTitle) {

            setFileName(fileName);
            setMusicTitle(musicTitle);
            setAlbumTitle(albumTitle);
            setArtistName(artistName);
        }

        public AssociateMusicModel()
        {
            this(null , null, null, null);
        }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public boolean associateMusic(Map<String, Object> session, String artist, String album, String musicTitle, String fileName) {

        String r = "";
        RMIServerInterface server = null;

        System.out.println("AssociateMusicService - execute()");

        try
        {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");
        }
        catch(NotBoundException | RemoteException e) {
            e.printStackTrace();
        }

        try {
            return server.associateMusic((String) session.get("email"), artist, album, musicTitle, fileName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;

    }
    }


