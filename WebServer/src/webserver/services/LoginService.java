package webserver.services;

import rmiserver.RMIServerInterface;
import webserver.models.LoginModel;
import webserver.services.interfaces.LoginServiceInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class LoginService implements LoginServiceInterface {

    public LoginService () {
        System.out.println("no construtor do login service");
    }

    public boolean login(LoginModel loginModel) throws RemoteException {

        boolean r;
        RMIServerInterface server = null;
        String rsp;
        System.out.println("Login Service");

        try {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");
        }
        catch(NotBoundException |RemoteException e) {
            e.printStackTrace();
        }

        rsp = server.login(loginModel.getEmail(), loginModel.getPassword());
        if (rsp.equals("Logged in successfully " + loginModel.getEmail()))
        {
            r = true;
        } else
        {
            r = false;
        }

        return r;
    }



}
