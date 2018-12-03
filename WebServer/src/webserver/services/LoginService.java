package webserver.services;
import rmiserver.RMIServerInterface;
import webserver.models.LoginModel;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class LoginService {

    public LoginService () {
        System.out.println("Starting LoginService()");
    }

    public boolean login(LoginModel loginModel) throws RemoteException {

        boolean r;
        RMIServerInterface server = null;
        String rsp;

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
