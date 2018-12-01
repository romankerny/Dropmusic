package webserver.models;

import rmiserver.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class SearchBean {
    private RMIServerInterface server;
    private String keyword;
    private String result;

    public SearchBean() {
        try {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");
        }
        catch(NotBoundException | RemoteException e) {
            e.printStackTrace(); // what happens *after* we reach this line?
        }
    }

    public boolean details() throws RemoteException {
        this.result = server.search("art", keyword);
        return true;
    }

    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getResult() {
        return result;
    }
}
