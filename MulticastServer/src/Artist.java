import java.util.concurrent.CopyOnWriteArrayList;

public class Artist {

    String               name;
    CopyOnWriteArrayList<Album> albums;
    public String        details;
    public               CopyOnWriteArrayList<User> notifyIfEdited;

    public Artist(String name) {
        this.name = name;
        this.albums = new CopyOnWriteArrayList<Album>();
    }

    public String toString() {
        return name + " " + "Albuns: "+albums.toString();
    }

    public void setDetails(String details, User s) {
        this.details = details;
        this.notifyIfEdited.add(s);
    }

}
