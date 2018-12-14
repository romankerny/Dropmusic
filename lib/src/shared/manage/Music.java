package shared.manage;

import java.io.Serializable;
import java.util.ArrayList;

public class Music implements Serializable, ManageModel {
    private static final long serialVersionUID = 1234675L;

    private String   track;
    private String title;
    private String lyrics;
    private String albumTitle;
    private String artistName;
    private String fileName;

    // private ConcurrentHashMap<String, MusicFile> musicFiles;



    public Music(String track, String title, String lyrics, String albumTitle, String artistName) {
        setTrack(track);
        setTitle(title);
        setLyrics(lyrics);
        setAlbumTitle(albumTitle);
        setArtistName(artistName);
       //  this.musicFiles = new ConcurrentHashMap<String, MusicFile>();
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



