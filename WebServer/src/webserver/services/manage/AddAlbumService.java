package webserver.services.manage;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.manage.Album;
import shared.models.manage.ManageModel;
import ws.WebSocketAnnotation;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class AddAlbumService implements ManageService {

    @Override
    public boolean add(ManageModel manageModel, String email) {

        boolean r = false;
        boolean exit = false;
        RMIServerInterface server;
        String rsp;

        server = RMICall.waitForServer();

        while (!exit) {
            try {
                if (manageModel instanceof Album) {
                    Album album = (Album) manageModel;
                    if (album.getArtist() != "" && album.getTitle() != "" && album.getDescription() != "" && album.getGenre() != "" && album.getLaunchDate() != "" && album.getEditorLabel() != "") {
                        rsp = server.addAlbum(album.getArtist(), album.getTitle(), album.getDescription(), album.getGenre(), album.getLaunchDate(), album.getEditorLabel(), email);
                        if (rsp.equals("Album info added with success")) {
                            ArrayList<String> editors;
                            editors = server.getEditors(album.getArtist());
                            for (String ed : editors) {
                                WebSocketAnnotation.sendNotification(ed, "Album `" + album.getTitle() + "` by " + album.getArtist() + " was edited");
                            }
                            r = true;
                        }
                    }

                }
                exit = true;

            } catch (RemoteException e) {
                server = RMICall.waitForServer();
            }
        }

        return r;
    }

}
