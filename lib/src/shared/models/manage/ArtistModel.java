package shared.models.manage;

import shared.RMICall;
import shared.RMIServerInterface;


import java.io.Serializable;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * An artist model folowing JavaBean convention
 * Contains an arraylist of albums (AlbumModel)
 * Connects to RMI
 */

public class ArtistModel implements Serializable, ManageModel {
    private static final long serialVersionUID = 1234L;

    private String name;
    private String details;
    private ArrayList<AlbumModel> albums;

    public ArtistModel(String name, String details) {
       setDetails(details);
       setName(name);
       setAlbums(new ArrayList<AlbumModel>());
    }


    public ArtistModel()
    {
        this(null, null);
    }

    /**
     * Removes an artist from DB
     * Calls RMI's .removeArtist()
     * If RemoteException then tries again
     *
     * @return
     */

    public boolean removeArtist() {

        boolean r = false,  exit = false;
        RMIServerInterface server = RMICall.waitForServer();
        String rsp;


        while(!exit)
        {

            try
            {
                if(getName() != null && getName() != "")
                {
                    if (server.removeArtist(getName()) )
                    {
                        r = true;
                    } else {
                        r = false;
                    }
                }
                exit = true;

            } catch (ConnectException e) {
                System.out.println("RMI server down, retrying...");
            } catch (RemoteException tt) {
                System.out.println("RMI server down, retrying...");
            }
            server = RMICall.waitForServer();
        }

        return r;
    }


    @Override
    public String toString() {
        return "ArtistModel: "+name + "\nBio: "+details+"\n";
    }

    public ArrayList<AlbumModel> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<AlbumModel> albums) {
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