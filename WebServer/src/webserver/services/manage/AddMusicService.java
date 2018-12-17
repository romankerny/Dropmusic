package webserver.services.manage;

import shared.RMIServerInterface;
import shared.models.manage.ManageModel;
import shared.models.manage.Music;
import ws.WebSocketAnnotation;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class AddMusicService implements ManageService {

    public boolean add(ManageModel manageModel, String email) {

        boolean r = false;
        RMIServerInterface server;
        String rsp;

        try {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");

            if (manageModel instanceof Music)
            {
                Music music = (Music) manageModel;

                if(music.getTitle() != "" && music.getTrack() != "" && music.getAlbumTitle() != "" &&  music.getLyrics() != "" && music.getArtistName() != "") {

                    rsp = server.addMusic(music.getTitle(), music.getTrack(), music.getAlbumTitle(), email, music.getLyrics(), music.getArtistName());
                    if (rsp.equals("Music info added with success")) {
                        ArrayList<String> editors;
                        editors = server.getEditors(music.getArtistName());
                        for (String ed : editors) {
                            WebSocketAnnotation.sendNotification(ed, "An Album from " + music.getArtistName() + " has been edited!");
                        }
                        r = true;
                    } else {
                        r = false;
                    }
                }

            }



        } catch(NotBoundException | RemoteException e) {
            e.printStackTrace();
        }

        return r;
    }
}
