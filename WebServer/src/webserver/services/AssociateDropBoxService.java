package webserver.services;


import shared.RMIServerInterface;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;

public class AssociateDropBoxService {

    public AssociateDropBoxService() {
        System.out.println("Starting AssociateDropBoxService()");
    }
    public String associateDropBox(Map<String, Object> session)  {

        // This service is meant to generate an URL to Dropbox auth
        // is used by 2 actions
        // AssociateDropBoxAction - loggedin == true
        // LoginDropBoxAction - loggedin == false

        boolean r = false;
        RMIServerInterface server = null;

        try
        {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");
        }
        catch(NotBoundException |RemoteException e) {
            e.printStackTrace();
        }

        try {
            System.out.println();
            if(session.get("loggedin") != null) {
                return server.associateDropBox();
            } else {
                return server.associateDropBoxBeforeLogin();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return "localhost:8080/restricted/error.jsp";
    }
}
