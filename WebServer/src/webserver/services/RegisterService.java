package webserver.services;

import rmiserver.RMIServerInterface;
import webserver.models.LoginModel;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RegisterService {

    public RegisterService() {
        System.out.println("Starting RegisterService()");
    }

    public boolean register(LoginModel loginModel) throws RemoteException {

        boolean r;
        RMIServerInterface server = null;
        String rsp;

        try
        {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");
        }
        catch(NotBoundException |RemoteException e) {
            e.printStackTrace();
        }

        rsp = server.register(loginModel.getEmail(), loginModel.getPassword());

        if (rsp.equals("User " + loginModel.getEmail() + " registered successfully"))
        {
            r = true;
        }
        else
        {
            r = false;
        }

        return r;
    }
}
