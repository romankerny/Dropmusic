package webserver.services.search;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.manage.AlbumModel;
import shared.models.manage.ManageModel;

import java.rmi.RemoteException;

/**
 * Service to search for an album, uses RMI
 */

public class AlbumSearchService implements SearchService {

    private RMIServerInterface server;

    /**
     * Sets a valid RMIServerInterface
     */

    public AlbumSearchService() {
        this.server = RMICall.waitForServer();
    }

    /**
     * Calls RMI's .searchAlbum() to get an AlbumModel
     *
     * @param searchModel gets casted to an AlbumModel
     * @return returns an AlbumModel as Object
     */

    @Override
    public Object search(ManageModel searchModel) {
        AlbumModel albumModel = (AlbumModel) searchModel;
        Object result = null;
        boolean exit = false;

        while (!exit) {
            try {
                result = server.searchAlbum(albumModel.getArtist(), albumModel.getTitle());
                exit = true;
            } catch (RemoteException e) {
                this.server = RMICall.waitForServer();
            }
        }

        return result;
    }
}
