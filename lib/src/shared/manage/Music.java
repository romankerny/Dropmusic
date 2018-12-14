package shared.manage;

import shared.RMIServerInterface;

import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class Music implements Serializable, ManageModel {
    private static final long serialVersionUID = 1234675L;

    private String   track;
    private String title;
    private String lyrics;
    private String albumTitle;
    private String artistName;


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

    /*
    public ConcurrentHashMap<String, MusicFile> getMusicFiles() {
        return musicFiles;
    }

    public void setMusicFiles(ConcurrentHashMap<String, MusicFile> musicFiles) {
        this.musicFiles = musicFiles;
    }
    */

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
}
