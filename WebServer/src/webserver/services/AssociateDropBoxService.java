package webserver.services;


import shared.RMICall;
import shared.RMIServerInterface;
import java.rmi.RemoteException;

/**
 * This service calls the RMI associateDropBox method which returns an url to redirect the user to.
 * The service is called in the login and when the user is associating is dropbox account with DropMusic.
 */
public class AssociateDropBoxService {

    public AssociateDropBoxService() {
        System.out.println("Starting AssociateDropBoxService()");
    }
    public String associateDropBox()  {

        // This service is meant to generate an URL to Dropbox auth

        boolean r = false, exit = false;
        String url = "";
        RMIServerInterface server = RMICall.waitForServer();

        while (!exit) {
            try {
                url = server.associateDropBox();
                exit = true;

            } catch (RemoteException e) {
                server = RMICall.waitForServer();
            }
        }

        return url;
    }
}
