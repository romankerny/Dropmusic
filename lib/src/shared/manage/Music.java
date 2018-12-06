package shared.manage;

import java.io.Serializable;
import java.util.ArrayList;

public class Music implements Serializable, ManageModel {
    private static final long serialVersionUID = 1234675L;

    private int track;
    private String title;
    private String lyrics;
    // private ConcurrentHashMap<String, MusicFile> musicFiles;



    public Music(int track, String title, String lyrics) {
        setTrack(track);
        setTitle(title);
        setLyrics(lyrics);
       //  this.musicFiles = new ConcurrentHashMap<String, MusicFile>();
    }

    public Music()
    {
        this(0, null, null);
    }

    @Override
    public String toString() {
        return track+ " - "+title;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
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

class MusicFile implements Serializable {

    private String filename;
    private byte[] rawData = null;
    private ArrayList<String> emails;

    MusicFile(String filename, byte[] rawData) {
        this.filename = filename;
        this.rawData = rawData;
        this.emails = new ArrayList<String>();
    }

    public void shareWith(String email) {
        emails.add(email);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }



}

