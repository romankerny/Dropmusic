package webserver.services.search;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.manage.ArtistModel;
import shared.models.manage.ManageModel;

import java.rmi.RemoteException;

/**
 * Service to search an Artist, connects to RMI
 */

public class ArtistSearchService implements SearchService {

    private RMIServerInterface server;

    /**
     * Sets a valid RMIServerInterface
     */

    public ArtistSearchService() {
        server = RMICall.waitForServer();
    }

    /**
     * Calls RMI's .searchArtist() to get an ArtistModel
     *
     * @param searchModel gets casted to an ArtistModel
     * @return returns an ArtistModel as Object
     */

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
