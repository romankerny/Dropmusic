import java.util.concurrent.CopyOnWriteArrayList;

public class Artist {

    public String               name;
    public CopyOnWriteArrayList<Album> albums;
    public String        details;
    public               CopyOnWriteArrayList<User> notifyIfEdited;

    public Artist(String name) {
        this.name = name;
        this.albums = new CopyOnWriteArrayList<Album>();
        this.notifyIfEdited = new CopyOnWriteArrayList<User>();
    }

    public void setDetails(String details, User s) {
        this.details = details;
        this.notifyIfEdited.add(s);
    }

    @Override
    public String toString() {
        String discography = "";

        for (Album a : this.albums) {
            discography += a.toString() +"\n";
        }

        return "Artist: "+name + "\n\t===== Albums =====\n"+discography;
    }

}
