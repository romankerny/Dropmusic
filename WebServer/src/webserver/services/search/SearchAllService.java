package webserver.services.search;

import shared.RMIServerInterface;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class SearchAllService {

    public SearchAllService() {

    }

    public ArrayList<Object> searchAll(String keyword) {
        ArrayList<Object> results = new ArrayList<>();
        try {
            RMIServerInterface server = (RMIServerInterface) LocateRegistry.getRegistry(1099).lookup("rmiserver");
            results = server.search(keyword);
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return results;
    }


}
