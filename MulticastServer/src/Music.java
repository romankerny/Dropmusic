import java.io.InputStream;
import java.util.Date;

public class Music {
    public int track;
    public  String title;
    private String compositor;
    private Date   duracao;
    public InputStream music_file;

    public Music(int track, String title) {
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
