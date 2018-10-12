import java.util.concurrent.CopyOnWriteArrayList;

public class Artist {

    String               name;
    CopyOnWriteArrayList Albuns;

    public String toString() {
        return name + " " + Albuns.toString();
    }

}
