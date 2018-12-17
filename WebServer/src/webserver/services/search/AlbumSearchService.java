package webserver.services.search;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.manage.Album;
import shared.models.manage.ManageModel;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class AlbumSearchService implements SearchService {

    private RMIServerInterface server;

    public AlbumSearchService() {
        this.server = RMICall.waitForServer();
    }

    @Override
    public Object search(ManageModel searchModel) {
        Album albumModel = (Album) searchModel;
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
