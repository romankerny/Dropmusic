package webserver.services;

import rmiserver.RMIServerInterface;
import webserver.models.SearchModel;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

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
    public String search(SearchModel searchModel) {
        String result="";
        try {
            result = server.search("mus", searchModel.getKeyword());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return result;
    }
}
