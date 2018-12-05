package shared;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

public class Artist implements Serializable {

    private String               name;
    private CopyOnWriteArrayList<Album> albums;
    private String        details;
    private               CopyOnWriteArrayList<User> notifyIfEdited;

    public Artist(String name, String details) {
        this.name = name;
        this.details = details;
        this.albums = new CopyOnWriteArrayList<Album>();
        this.notifyIfEdited = new CopyOnWriteArrayList<User>();
    }

    public void setDetails(String details, User s) {
        this.details = details;
        this.notifyIfEdited.add(s);
    }

    public void addNotifyIfEdited(User editor) {
        if(!this.notifyIfEdited.contains(editor)) {
            this.notifyIfEdited.add(editor);
        }
    }

    @Override
    public String toString() {
        String discography = "";

        for (Album a : this.albums) {
            discography += "Title: " +a.getTitle()+"\n"+
                    "Genre: "+a.getGenre()+"\n"+
                    "Description: "+a.getDescription()+"\n\n";
        }

        return "Artist: "+name + "\nBio: "+details+"\n\t===== Albums =====\n"+discography;
    }

}