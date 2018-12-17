package webserver.services.search;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.manage.ManageModel;
import shared.models.manage.MusicModel;

import java.rmi.RemoteException;

public class MusicSearchService implements SearchService {
    private RMIServerInterface server;

    public MusicSearchService() {
        server = RMICall.waitForServer();

    }

    @Override
    public Object search(ManageModel searchModel) {
        MusicModel musicModel = (MusicModel) searchModel;
        Object result = null;
        boolean exit = false;

        while (!exit) {
            try {
                result = server.searchAlbum(musicModel.getArtistName(), musicModel.getTitle());
                exit = true;
            } catch (RemoteException e) {
                server = RMICall.waitForServer();
            }
        }
        return result;

    }
}
