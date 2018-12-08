package webserver.services.manage;

import shared.manage.Artist;
import shared.manage.ManageModel;
import shared.RMIServerInterface;
import ws.WebSocketAnnotation;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class AddArtistService implements ManageService {


    public boolean add(ManageModel manageModel, String email) {

        boolean r;
        RMIServerInterface server = null;
        String rsp;

        try {
            server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");

            if (manageModel instanceof Artist)
            {
                Artist artist = (Artist) manageModel;

                rsp = server.addArtist(artist.getName(), artist.getDetails(), email);
                if(rsp.equals("Artist created") || rsp.equals("Artist `" + artist.getName() + "` was edited") || rsp.equals("Artist info added with success")) {
                    ArrayList<String> editors = new ArrayList<>();
                    System.out.println("NO ADD CRL");
                    editors = server.getEditors(artist.getName());
                    for (String ed : editors)
                        WebSocketAnnotation.sendNotification(ed, "Artist `\" + artist.getName() + \"` was edited\") by " + email);

                    return true;
                } else {
                    return false;
                }

            }



        } catch(NotBoundException |RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
}
