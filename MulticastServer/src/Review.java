public class Review {

    public String critic;
    public int    rating;
    public String email;

    public Review(String critic, int rating, String email) {
        this.critic = critic;
        this.rating = rating;
        this.email = email;
    }

    public String toString() {
        return "email: " + email + " rating: " + rating + " critic: " + critic;
    }


}
