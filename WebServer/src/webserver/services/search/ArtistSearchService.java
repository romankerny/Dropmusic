package webserver.services.search;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.manage.ArtistModel;
import shared.models.manage.ManageModel;

import java.rmi.RemoteException;

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
                result = server.searchArtist(((ArtistModel) searchModel).getName());
                exit = true;
            } catch (RemoteException e) {
                server = RMICall.waitForServer();
            }
        }
        return result;

    }
}
