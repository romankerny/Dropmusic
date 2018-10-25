import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Music {
    public int track;
    public String title;
    public ConcurrentHashMap<String, MusicFile> musicFiles;
    public CopyOnWriteArrayList<User> notifyIfEdited;



    Music(int track, String title) {
        this.track = track;
        this.title = title;
        this.notifyIfEdited = new CopyOnWriteArrayList<>();
        this.musicFiles = new ConcurrentHashMap<String, MusicFile>();
    }

    @Override
    public String toString() {
        return track+ " - "+title;
    }
}

class MusicFile {

    public String filename;
    public byte[] rawData = null;
    public ArrayList<String> emails;

    MusicFile(String filename, byte[] rawData) {
        this.filename = filename;
        this.rawData = rawData;
        this.emails = new ArrayList<String>();
    }

    public void shareWith(String email) {
        emails.add(email);
    }

}