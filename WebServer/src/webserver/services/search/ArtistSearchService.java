package webserver.services.search;

import shared.RMIServerInterface;
import shared.SearchModel;
import shared.manage.Artist;
import shared.manage.ManageModel;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class ArtistSearchService implements SearchService {

    private RMIServerInterface server;

    public ArtistSearchService() {
        try {
            server = (RMIServerInterface) LocateRegistry.getRegistry(1099).lookup("rmiserver");
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object search(ManageModel searchModel) {
        Object result = null;
        try {
            result = server.searchArtist(((Artist) searchModel).getName());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;

    }
}
