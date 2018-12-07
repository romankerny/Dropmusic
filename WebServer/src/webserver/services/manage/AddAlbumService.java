package webserver.services.manage;

import shared.RMIServerInterface;
import shared.manage.Album;
import shared.manage.Artist;
import shared.manage.ManageModel;
import ws.WebSocketAnnotation;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class AddAlbumService implements ManageService {

    public boolean add(ManageModel manageModel, String email) {

        boolean r;
        RMIServerInterface server = null;
        String rsp;

        try {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");

            if (manageModel instanceof Album)
            {
                Album album = (Album) manageModel;

                rsp = server.addAlbum(album.getArtist(), album.getTitle(), album.getDescription(), album.getGenre(), album.getLaunchDate(),album.getEditorLabel(),email);
                if(rsp.equals("Album info added with success")) {
                    ArrayList<String> editors = new ArrayList<>();
                    editors = server.getEditors(album.getArtist());
                    for (String ed : editors) {
                        System.out.println("EDITOR " + ed);
                        WebSocketAnnotation.sendNotification(ed, "Album `" + album.getTitle() + "` by " + album.getArtist() + " was edited");
                    }
                    return true;
                } else {
                    return false;
                }

            }



        } catch(NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

}
