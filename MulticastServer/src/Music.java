import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Music {
    public int track;
    public  String title;
    public ConcurrentHashMap<String, MusicFile> musicFiles;


    Music(int track, String title) {
        this.track = track;
        this.title = title;
        this.musicFiles = new ConcurrentHashMap<String, MusicFile>();
    }

    @Override
    public String toString() {
        return track+ " - "+title;
    }
}

class MusicFile {

    public File musicFile;
    ArrayList<String> emails;

    MusicFile(String uploader, File m) {
        this.musicFile = m;
        this.emails = new ArrayList<String>();
    }

    public void shareWith(String email) {
        emails.add(email);
    }

}