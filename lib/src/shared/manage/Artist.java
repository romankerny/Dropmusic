package shared.manage;

import shared.User;

import java.io.Serializable;
import java.util.ArrayList;

public class Artist implements Serializable, ManageModel {
    private static final long serialVersionUID = 1234L;

    private String name;
    private String details;
    private ArrayList<Album> albums;

    public Artist(String name, String details) {
       setDetails(details);
       setName(name);
       setAlbums(new ArrayList<Album>());
    }


    public Artist()
    {
        this(null, null);
    }

    public void setDetails(String details, User s) {
        this.details = details;
    }



    @Override
    public String toString() {
        return "Artist: "+name + "\nBio: "+details+"\n";
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}