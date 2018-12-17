package webserver.services;
import shared.RMICall;
import shared.RMIServerInterface;

import java.rmi.RemoteException;
import java.util.Map;

/**
 * This service calls the RMI methods that allow a user to associate his account with Dropbox, and Login.
 */
public class AssociateDropBoxTokenService {


    public AssociateDropBoxTokenService() {
        System.out.println("Starting AssociateDropBoxService()");
    }

    /**
     * Asks the RMI server if the user with the given code is allowed to login into the application.
     * @param code
     * @return boolean successful / failed
     */

    public String canLogin(String code) {

        String r = "";
        boolean exit = false;
        RMIServerInterface server = RMICall.waitForServer();

        System.out.println("LoginDropboxService - canLogin()");

        while(!exit) {

            try {
                return server.canLogin(code);

            } catch (RemoteException e) {
                server = RMICall.waitForServer();
            }
        }

        return r;

    }

    /**
     * When the user is associating this method calls RMI Server to save the token and the user Dropbox's email in DB.
     * @param session
     * @param code
     * @return boolean - successful / failed
     */


    public boolean setUserToken(Map<String, Object> session, String code) {

        boolean r = false, exit = false;
        RMIServerInterface server = RMICall.waitForServer();

        while(!exit) {
            try {
                if (server.setToken((String) session.get("email"), code)) {
                    r = true;
                    exit = true;
                }
            } catch (RemoteException e) {
                server = RMICall.waitForServer();
            }
        }

        return r;

    }
}
