package webserver.services;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import shared.RMIServerInterface;
import uc.sd.apis.DropBoxApi2;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;

public class AssociateDropBoxTokenService {

    String userToken;

    public AssociateDropBoxTokenService() {
        System.out.println("Starting AssociateDropBoxService()");
        String userToken = "";
    }


    public boolean setUserToken(Map<String, Object> session, String code) {



        boolean r = false;
        RMIServerInterface server = null;

        try
        {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");
        }
        catch(NotBoundException | RemoteException e) {
            e.printStackTrace();
        }

        try {
            if(server.setToken((String) session.get("email"), code)) {
                r = true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return r;

    }
}
