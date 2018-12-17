package webserver.services.search;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.manage.Artist;
import shared.models.manage.ManageModel;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ArtistSearchService implements SearchService {

    private RMIServerInterface server;

    public ArtistSearchService() {
        server = RMICall.waitForServer();
    }

    @Override
    public Object search(ManageModel searchModel) {
        Object result = null;
        boolean exit = false;

        while (!exit) {
            try {
                result = server.searchArtist(((Artist) searchModel).getName());
                exit = true;
            } catch (RemoteException e) {
                server = RMICall.waitForServer();
            }
        }
        return result;

    }
}
