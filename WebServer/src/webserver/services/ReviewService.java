package webserver.services;

import shared.RMICall;
import shared.RMIServerInterface;
import shared.models.ReviewModel;

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
                if (!review.getCritic().equals("") || !review.getRating().equals("")) {
                    rr = server.rateAlbum(review.getRating(), review.getArtist(), review.getAlbum(), review.getCritic(), review.getEmail());
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
