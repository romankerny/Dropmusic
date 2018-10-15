public class Review {

    private String critic;
    private int    rating;
    private String email;

    public Review(String critic, int rating, String email) {
        this.critic = critic;
        this.rating = rating;
        this.email = email;
    }

    public String toString() {
        return "email: " + email + " rating: " + rating + " critic: " + critic;
    }


}
