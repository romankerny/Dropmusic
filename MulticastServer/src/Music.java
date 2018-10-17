
public class Music {
    public int track;
    public String title;
    // Falta o .mp3

    public Music(int track, String title) {
        this.track = track;
        this.title = title;
    }

    @Override
    public String toString() {
        return track+ " - "+title;
    }
}
