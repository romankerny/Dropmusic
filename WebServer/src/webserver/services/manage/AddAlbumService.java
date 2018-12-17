package webserver.services.manage;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.manage.AlbumModel;
import shared.models.manage.ManageModel;
import ws.WebSocketAnnotation;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Service that implements a Service to add Albums.
 * Notifies users that previously added or edited the Album.
 */
public class AddAlbumService implements ManageService {

    /**
     * Checks for user input, if some field is empty then automatically fails operation.
     * If not, calls the RMI method that adds the Album. Then gets the Editors of the album
     * from RMI .getEditors() and sends the notification.
     *
     * @param manageModel - model holding input from user
     * @param email       - gotten from the session of the user that requested the ManageAction
     * @return boolean indicating if the user has been successful.
     */
    @Override
    public boolean add(ManageModel manageModel, String email) {

        boolean r = false;
        boolean exit = false;
        RMIServerInterface server;
        String rsp;

        server = RMICall.waitForServer();

        while (!exit) {
            try {
                if (manageModel instanceof AlbumModel)
                {

                    AlbumModel album = (AlbumModel) manageModel;
                    if (album.getArtist() != "" && album.getTitle() != "" && album.getDescription() != "" && album.getGenre() != "" && album.getLaunchDate() != "" && album.getEditorLabel() != "")
                    {
                        rsp = server.addAlbum(album.getArtist(), album.getTitle(), album.getDescription(), album.getGenre(), album.getLaunchDate(), album.getEditorLabel(), email);
                        if (rsp.equals("Album info added with success"))
                        {
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
