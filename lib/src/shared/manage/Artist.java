package shared.manage;

import shared.User;

import java.io.Serializable;

public class Artist implements Serializable, ManageModel {
    private static final long serialVersionUID = 1234L;

    private String name;
    private String details;

    public Artist(String name, String details) {
       setDetails(details);
       setName(name);
    }

    public Artist()
    {
        this(null, null);
    }

    public void setDetails(String details, User s) {
        this.details = details;
    }


/*
    @Override
    public String toString() {
        return "Artist: "+name + "\nBio: "+details+"\n";
    }
    */

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