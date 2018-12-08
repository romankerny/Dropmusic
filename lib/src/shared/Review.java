package shared;

import java.io.Serializable;

public class Review implements Serializable {

    private String critic;
    private int rating;
    private String email;
    private String artist;
    private String album;

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
}
