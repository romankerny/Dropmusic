import java.util.concurrent.CopyOnWriteArrayList;

public class Artist {

    String               name;
    CopyOnWriteArrayList Albuns;
    public String        details;
    public               CopyOnWriteArrayList<User> notifyIfEdited;

    public Artist(String name) {
        this.name = name;
    }

    public String toString() {
        return name + " " + "Albuns: "+Albuns.toString();
    }

    public void setDetails(String details, User s) {
        this.details = details;
        this.notifyIfEdited.add(s);
    }

}
