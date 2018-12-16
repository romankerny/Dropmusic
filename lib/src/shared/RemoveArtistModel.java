package shared;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RemoveArtistModel  {

    private String artist;

    public RemoveArtistModel(String artist) {
        setArtist(artist);
    }

    public RemoveArtistModel()
    {
        this(null);
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean removeArtist() {

        boolean r = false;
        RMIServerInterface server = null;
        String rsp;

        try
        {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");



            if(getArtist() != null && getArtist() != null)
            {
                if (server.removeArtist(getArtist()) ) {
                    r = true;
                } else {
                    r = false;
                }
            }

        }
        catch(NotBoundException |RemoteException e) {
            e.printStackTrace();
        }

        return r;
    }
}
