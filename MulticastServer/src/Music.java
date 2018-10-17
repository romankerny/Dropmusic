
import javax.sound.sampled.AudioInputStream;
import java.util.Date;

public class Music {
    public  String title;
    private String compositor;
    private Date   duracao;
    public AudioInputStream music_file;

    Music(String title) {
        this.title = title;
    }
}
