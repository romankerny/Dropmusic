package webserver.services.search;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.manage.ManageModel;
import shared.models.manage.Music;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class MusicSearchService implements SearchService {
    private RMIServerInterface server;

    public MusicSearchService() {
        server = RMICall.waitForServer();

    }

    @Override
    public Object search(ManageModel searchModel) {
        Music musicModel = (Music) searchModel;
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
