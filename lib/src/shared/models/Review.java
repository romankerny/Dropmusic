package shared.models;

import shared.RMICall;
import shared.RMIServerInterface;

import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Review implements Serializable {

    private String critic;
    private int rating;
    private String email;
    private String artist;
    private String album;

    private RMIServerInterface server;

    public Review(int rating, String critic, String email, String artist, String album) {
        this.critic = critic;
        this.rating = rating;
        this.email = email;
        this.artist = artist;
        this.album = album;
    }

    public Review() {

    }

    public String getCritic() {
        return critic;
    }

    public void setCritic(String critic) {
        this.critic = critic;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public String toString() {
        return "email: " + email + " rating: " + rating + " critic: " + critic;
    }


    public double addReview(Review review) {


        boolean exit = false;
        RMIServerInterface server = RMICall.waitForServer();

        double rr = 0;

        while(!exit)
        {
            try
            {
                rr = server.rateAlbum(review.getRating(), review.getArtist(), review.getAlbum(), review.getCritic(), review.getEmail());
                exit = true;

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
