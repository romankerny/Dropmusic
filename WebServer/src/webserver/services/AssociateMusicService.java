package webserver.services;

import shared.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;

public class AssociateMusicService {

    public boolean associateMusic(Map<String, Object> session, String artist, String album, String musicTitle, String fileName) {

        String r = "";
        RMIServerInterface server = null;

        System.out.println("AssociateMusicService - execute()");

        try
        {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");
        }
        catch(NotBoundException | RemoteException e) {
            e.printStackTrace();
        }

        try {
            return server.associateMusic((String) session.get("email"), artist, album, musicTitle, fileName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;

    }
}
