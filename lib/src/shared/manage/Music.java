package shared.manage;

import shared.RMIServerInterface;

import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Map;

public class Music implements Serializable, ManageModel {
    private static final long serialVersionUID = 1234675L;

    private String   track;
    private String title;
    private String lyrics;
    private String albumTitle;
    private String artistName;
    private String fileName;


    public Music(String track, String title, String lyrics, String albumTitle, String artistName) {
        setTrack(track);
        setTitle(title);
        setLyrics(lyrics);
        setAlbumTitle(albumTitle);
        setArtistName(artistName);
    }

    public Music()
    {
        this(null , null, null, null, null);
    }

    public Music(String track, String title, String lyrics) {
        this.track = track;
        this.title = title;
        this.lyrics = lyrics;
        //  this.musicFiles = new ConcurrentHashMap<String, MusicFile>();
    }

    public String getURL(Music inputModel, String email) {
        try {
            RMIServerInterface server = (RMIServerInterface) LocateRegistry.getRegistry(1099).lookup("rmiserver");
            return server.getMusicURL(inputModel.getArtistName(), inputModel.getAlbumTitle(), inputModel.getTitle(), email);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return "fail";
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

    @Override
    public String toString() {
        return track+ " - "+title;
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

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
}



