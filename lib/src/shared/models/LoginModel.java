package shared.models;

import shared.RMICall;
import shared.RMIServerInterface;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class LoginModel {

    private String email;
    private String password;

    public LoginModel(String email, String password) {
        setEmail(email);
        setPassword(password);
    }

    public boolean login() throws RemoteException
    {

        boolean r = false, exit = false;
        RMIServerInterface server = null;
        String rsp;


        server = RMICall.waitForServer();

        while(!exit) {

            try {
                if (getEmail() != null && getPassword() != null && getEmail() != "" && getPassword() != "") {

                    rsp = server.login(getEmail(), getPassword());
                    if (rsp.equals("Logged in successfully " + getEmail())) {
                        r = true;
                    } else {
                        r = false;
                    }
                    exit = true;
                }
            } catch (ConnectException e) {
                System.out.println("RMI server down, retrying...");
            } catch (RemoteException tt) {
                System.out.println("RMI server down, retrying...");
            }
            server = RMICall.waitForServer();
        }

        return r;
    }

    public boolean register(LoginModel loginModel) throws RemoteException {

        boolean r = false, exit = false;
        RMIServerInterface server = null;
        String rsp;

        server = RMICall.waitForServer();

        while(!exit)
        {

            try
            {

                if(getEmail() != null && getPassword() != null && getEmail() != "" && getPassword() != "") {
                    rsp = server.register(loginModel.getEmail(), loginModel.getPassword());

                    if (rsp.equals("User " + loginModel.getEmail() + " registered successfully")) {
                        r = true;
                    } else {
                        r = false;
                    }
                }

            } catch (ConnectException e) {
                System.out.println("RMI server down, retrying...");
            } catch (RemoteException tt) {
                System.out.println("RMI server down, retrying...");
            }
            server = RMICall.waitForServer();
        }

        return r;
    }

    public LoginModel()
    {
        this(null, null);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
