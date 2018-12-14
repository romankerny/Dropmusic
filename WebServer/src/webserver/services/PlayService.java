package webserver.services;

import shared.RMIServerInterface;
import shared.manage.Music;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class PlayService {

    private RMIServerInterface server;

    public PlayService() {
        try {
            server = (RMIServerInterface) LocateRegistry.getRegistry(1099).lookup("rmiserver");
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public String getURL(Music inputModel, String email) {
        try {
            return server.getMusicURL(inputModel.getArtistName(), inputModel.getAlbumTitle(), inputModel.getTitle(), email);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "fail";
    }
}
