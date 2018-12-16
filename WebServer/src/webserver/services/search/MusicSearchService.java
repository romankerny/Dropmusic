package webserver.services.search;

import shared.RMIServerInterface;
import shared.SearchModel;
import shared.manage.ManageModel;
import shared.manage.Music;
import webserver.services.search.SearchService;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class MusicSearchService implements SearchService {
    private RMIServerInterface server;

    public MusicSearchService() {
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
        Music musicModel = (Music) searchModel;
        Object result = null;
        try {
            result = server.searchMusic(musicModel.getArtistName(), musicModel.getAlbumTitle(), musicModel.getTitle());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;

    }
}
