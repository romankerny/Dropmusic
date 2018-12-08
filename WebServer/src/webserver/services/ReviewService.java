package webserver.services;

import shared.RMIServerInterface;
import shared.Review;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ReviewService {
    private RMIServerInterface server;

    public ReviewService() {
        try {
            server = (RMIServerInterface) LocateRegistry.getRegistry(1099).lookup("rmiserver");
        } catch (
                AccessException e) {
            e.printStackTrace();
        } catch (
                RemoteException e) {
            e.printStackTrace();
        } catch (
                NotBoundException e) {
            e.printStackTrace();
        }
    }

    public boolean addReview(Review review) {
        try {
            server.rateAlbum(review.getRating(), review.getArtist(), review.getAlbum(), review.getCritic(), review.getEmail());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }
}
