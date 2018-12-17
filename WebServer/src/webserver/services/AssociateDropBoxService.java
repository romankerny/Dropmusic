package webserver.services;


import shared.RMICall;
import shared.RMIServerInterface;
import java.rmi.RemoteException;

public class AssociateDropBoxService {

    public AssociateDropBoxService() {
        System.out.println("Starting AssociateDropBoxService()");
    }
    public String associateDropBox()  {

        // This service is meant to generate an URL to Dropbox auth
        // is used by 2 actions
        // AssociateDropBoxAction - loggedin == true
        // LoginDropBoxAction - loggedin == false

        boolean r = false, exit = false;
        RMIServerInterface server = RMICall.waitForServer();

        while (!exit) {
            try {
                return server.associateDropBox();

            } catch (RemoteException e) {
                server = RMICall.waitForServer();
            }
        }
        System.out.println("vou dar pritn de erro");
        return "localhost:8080/restricted/error.jsp";
    }
}
