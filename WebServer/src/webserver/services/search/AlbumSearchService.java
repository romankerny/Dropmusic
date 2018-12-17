package webserver.services.search;

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
        Album albumModel = (Album) searchModel;
        Object result = null;
        try {
            result = server.searchAlbum(albumModel.getArtist(), albumModel.getTitle());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return result;
    }
}
