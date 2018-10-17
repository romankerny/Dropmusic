import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class Music {
    public int track;
    public  String title;
    private String compositor;
    private Date   duracao;



    Music(int track, String title) {
        this.track = track;
        this.title = title;
    }

    Music(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return track+ " - "+title;
    }
}

class MusicFile {

    public String uploader;
    public InputStream music_file;
    ArrayList<String> emails;

    MusicFile(String uploader, InputStream m) {
        this.uploader = uploader;
        this.music_file = m;
        this.emails = new ArrayList<String>();
    }

    public void shareWith(String email) {
        emails.add(email);
    }

}