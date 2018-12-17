package webserver.services.search;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.manage.ManageModel;
import shared.models.manage.MusicModel;

import java.rmi.RemoteException;

/**
 * Service to search a Music, connects to RMI
 */

public class MusicSearchService implements SearchService {
    private RMIServerInterface server;

    /**
     * Sets a valid RMIServerInterface
     */
    public MusicSearchService() {
        server = RMICall.waitForServer();

    }

    /**
     * Calls RMI's .searchMusic() to get an MusicModel
     *
     * @param searchModel gets casted to an MusicModel
     * @return returns an MusicModel as Object
     */

    @Override
    public Object search(ManageModel searchModel) {
        MusicModel musicModel = (MusicModel) searchModel;
        Object result = null;
        boolean exit = false;

        while (!exit) {
            try {
                result = server.searchMusic(musicModel.getArtistName(), musicModel.getAlbumTitle(), musicModel.getTitle());
                exit = true;
            } catch (RemoteException e) {
                server = RMICall.waitForServer();
            }
        }
        return result;

    }
}
