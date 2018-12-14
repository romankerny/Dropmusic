package webserver.services;

import shared.LoginModel;
import shared.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ShareMusicDropboxService {

    public boolean shareMusic(String emailToShare, String artist, String album, String title, String email) throws RemoteException {

        boolean r;
        RMIServerInterface server = null;
        String rsp;

        try
        {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");

            if (server.shareMusic(emailToShare, artist, album, title, email))
            {
                r = true;
            }
            else
            {
                r = false;
            }
        }
        catch(NotBoundException |RemoteException e) {
            e.printStackTrace();
        }


        return r;
    }


}
