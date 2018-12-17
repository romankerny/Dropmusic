package webserver.services.manage;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.manage.ManageModel;
import shared.models.manage.MusicModel;
import ws.WebSocketAnnotation;

import java.rmi.RemoteException;
import java.util.ArrayList;
/**
 * Service that implements a Service to add Music.
 * Notifies users that previously added or edited the Artist.
 */

public class AddMusicService implements ManageService {

    /**
     * Checks for user input, if some field is empty then automatically fails operation.
     * If not, calls the RMI method that adds the music. Then gets the Editors of the artist
     * from RMI .getEditors() and sends the notification.
     *
     * @param manageModel - model holding input from user
     * @param email       - gotten from the session of the user that requested the ManageAction
     * @return boolean indicating if the user has been successful.
     */

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
                exit = true;
            } catch (RemoteException e) {
                server = RMICall.waitForServer();
            }
        }

        return r;
    }
}
