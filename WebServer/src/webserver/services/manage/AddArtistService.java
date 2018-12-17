package webserver.services.manage;

import shared.RMICall;
import shared.models.manage.ArtistModel;
import shared.models.manage.ManageModel;
import shared.RMIServerInterface;
import ws.WebSocketAnnotation;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Service that implements a Service to add Artists.
 * Notifies users that previously added or edited the Artist.
 */

public class AddArtistService implements ManageService {

    /**
     * Checks for user input, if some field is empty then automatically fails operation.
     * If not, calls the RMI method that adds the artist Then gets the Editors of the artist
     * from RMI .getEditors() and sends the notification.
     *
     * @param manageModel - model holding input from user
     * @param email       - gotten from the session of the user that requested the ManageAction
     * @return boolean indicating if the user has been successful.
     */

    @Override
    public boolean add(ManageModel manageModel, String email) {

        boolean exit = false;
        RMIServerInterface server = RMICall.waitForServer();
        String rsp;

        while(!exit) {

            try {
                if (manageModel instanceof ArtistModel)
                {
                    ArtistModel artist = (ArtistModel) manageModel;
                    if (artist.getName() != "" && artist.getDetails() != "")
                    {
                        rsp = server.addArtist(artist.getName(), artist.getDetails(), email);
                        if (rsp.equals("Artist created") || rsp.equals("Artist `" + artist.getName() + "` was edited") || rsp.equals("Artist info added with success"))
                        {
                            ArrayList<String> editors = new ArrayList<>();
                            editors = server.getEditors(artist.getName());
                            for (String ed : editors)
                                WebSocketAnnotation.sendNotification(ed, "Artist " + artist.getName() + " was edited by " + email);

                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                exit = true;

            } catch (RemoteException e) {
                server = RMICall.waitForServer();
            }
        }
        return false;
    }
}
