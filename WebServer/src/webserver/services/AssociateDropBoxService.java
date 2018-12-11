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
            return server.associateDropBox((String) session.get("email"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return "localhost:8080/restricted/error.jsp";
    }
}
