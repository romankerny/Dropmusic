package webserver.services.manage;

import shared.RMIServerInterface;
import shared.manage.Artist;
import shared.manage.ManageModel;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class AddAlbumService implements ManageService {

    public boolean add(ManageModel manageModel, String email) {

        boolean r;
        RMIServerInterface server = null;
        String rsp;

        try {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");

            if (manageModel instanceof Artist)
            {
                Artist artist = (Artist) manageModel;

                rsp = server.addArtist(artist.getName(), artist.getDetails(), email);
                if(rsp.equals("Artist created")) {
                    return true;
                } else {
                    return false;
                }

            }



        } catch(NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

}
