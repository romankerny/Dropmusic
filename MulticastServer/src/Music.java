
import javax.sound.sampled.AudioInputStream;
import java.io.InputStream;
import java.util.Date;

public class Music {
    public  String title;
    private String compositor;
    private Date   duracao;
    public InputStream music_file;

    Music(String title) {
        this.title = title;
    }
}
