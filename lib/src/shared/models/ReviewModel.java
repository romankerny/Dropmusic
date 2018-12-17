package shared.models;

import java.io.Serializable;

/**
 * Model of a review, following JavaBean convention
 */

public class ReviewModel implements Serializable {

    private String critic;
    private String rating;
    private String email;
    private String artist;
    private String album;

    public ReviewModel(String rating, String critic, String email, String artist, String album) {
        this.critic = critic;
        this.rating = rating;
        this.email = email;
        this.artist = artist;
        this.album = album;
    }

    public ReviewModel() {

    }

    public String getCritic() {
        return critic;
    }

    public void setCritic(String critic) {
        this.critic = critic;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
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
