package webserver.services;

import rmiserver.RMIServerInterface;
import webserver.models.Album;
import webserver.models.SearchModel;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

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

    public ArrayList<Object> search(SearchModel searchModel) {
        ArrayList<Object> results = null;
        try {
            results = server.searchAlbum(searchModel.getKeyword());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return results;
    }
}
