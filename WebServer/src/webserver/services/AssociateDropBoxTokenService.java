package webserver.services;
import shared.RMICall;
import shared.RMIServerInterface;

import java.rmi.RemoteException;
import java.util.Map;

public class AssociateDropBoxTokenService {


    public AssociateDropBoxTokenService() {
        System.out.println("Starting AssociateDropBoxService()");
    }

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
