package webserver.services;

import shared.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;

public class LoginDropboxService {

    public String canLogin(String code) {

        String r = "";
        RMIServerInterface server = null;

        System.out.println("LoginDropboxService - execute()");

        try
        {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");
        }
        catch(NotBoundException | RemoteException e) {
            e.printStackTrace();
        }

        try {
            return server.canLogin(code);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return r;

    }
}
