package webserver.services;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.ReviewModel;
import ws.WebSocketAnnotation;

import java.rmi.ConnectException;
import java.rmi.RemoteException;

public class ReviewService {

    private RMIServerInterface server;

    public ReviewService() {

    }

    public double addReview(ReviewModel review) {


        boolean exit = false;
        server = RMICall.waitForServer();

        double rr = 0;

        while(!exit)
        {
            try
            {
                if (!review.getCritic().equals("") || !review.getRating().equals("") || review.getCritic().length() <= 300) {
                    rr = server.rateAlbum(review.getRating(), review.getArtist(), review.getAlbum(), review.getCritic(), review.getEmail());
                    String msg = rr+"#"+review.getAlbum()+"#"+review.getRating()+"#"+review.getEmail()+"#"+review.getCritic();
                    WebSocketAnnotation.updateAlbumRating(msg);
                    exit = true;
                } else {
                    return -1;
                }

            } catch (ConnectException e) {
                System.out.println("RMI server down, retrying...");
            } catch (RemoteException tt) {
                System.out.println("RMI server down, retrying...");
            }
            server = RMICall.waitForServer();
        }

        return rr;

    }
}
