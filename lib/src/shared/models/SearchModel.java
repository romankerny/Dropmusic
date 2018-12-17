package shared.models;

import shared.RMICall;
import shared.RMIServerInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class SearchModel {
    private String keyword;

    public SearchModel() {
        setKeyword(null);
    }

    public SearchModel(String keyword) {
        setKeyword(keyword);
    }

    public ArrayList<Object> searchAll() {

        RMIServerInterface server = RMICall.waitForServer();

        ArrayList<Object> results = new ArrayList<>();
        boolean exit = false;

        while(!exit) {
            try {
                results = server.search(this.keyword);
                exit = true;
            } catch (RemoteException e) {
                server = RMICall.waitForServer();
            }
        }
        return results;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
