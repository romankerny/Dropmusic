package webserver.services;

import rmiserver.RMIServerInterface;
import webserver.models.LoginModel;
import webserver.models.RegisterModel;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RegisterService {

    public RegisterService() {
        System.out.println("Starting RegisterService()");
    }

    public boolean register(RegisterModel registerModel) throws RemoteException {

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

        rsp = server.register(registerModel.getEmail(), registerModel.getPassword());

        if (rsp.equals("User " + registerModel.getEmail() + " registered successfully"))
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
