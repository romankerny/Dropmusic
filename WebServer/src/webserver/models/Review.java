package webserver.models;

import java.io.Serializable;

public class Review implements Serializable {

    private String critic;
    private int rating;
    private String email;

    public Review(String critic, int rating, String email) {
        this.critic = critic;
        this.rating = rating;
        this.email = email;
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

    @Override
    public String toString() {
        return "email: " + email + " rating: " + rating + " critic: " + critic;
    }
}
