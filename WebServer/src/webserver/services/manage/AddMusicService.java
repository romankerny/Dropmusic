package webserver.services.manage;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.manage.ManageModel;
import shared.models.manage.MusicModel;
import ws.WebSocketAnnotation;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class AddMusicService implements ManageService {

    @Override
    public boolean add(ManageModel manageModel, String email) {

        boolean r = false, exit = false;
        RMIServerInterface server = RMICall.waitForServer();
        String rsp;
        while (!exit) {
            try {

                if (manageModel instanceof MusicModel) {
                    MusicModel music = (MusicModel) manageModel;

                    if (music.getTitle() != "" && music.getTrack() != "" && music.getAlbumTitle() != "" && music.getLyrics() != "" && music.getArtistName() != "") {

                        rsp = server.addMusic(music.getTitle(), music.getTrack(), music.getAlbumTitle(), email, music.getLyrics(), music.getArtistName());
                        if (rsp.equals("MusicModel info added with success")) {
                            ArrayList<String> editors;
                            editors = server.getEditors(music.getArtistName());
                            for (String ed : editors) {
                                WebSocketAnnotation.sendNotification(ed, "An AlbumModel from " + music.getArtistName() + " has been edited!");
                            }
                            r = true;
                        } else {
                            r = false;
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
