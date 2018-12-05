package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Music implements Serializable {
    private int track;
    private String title;
    private ConcurrentHashMap<String, MusicFile> musicFiles;



    Music(int track, String title) {
        this.track = track;
        this.title = title;
        this.musicFiles = new ConcurrentHashMap<String, MusicFile>();
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

    public ConcurrentHashMap<String, MusicFile> getMusicFiles() {
        return musicFiles;
    }

    public void setMusicFiles(ConcurrentHashMap<String, MusicFile> musicFiles) {
        this.musicFiles = musicFiles;
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

