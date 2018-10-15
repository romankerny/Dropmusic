import java.util.concurrent.CopyOnWriteArrayList;

public class Album {
    public          String  tittle;
    private         String  description;
    private         Music[] tracks;
    public          double  ratingC;                    // += every single rate
    public          int     nCritics;                   // to count n
    public          String  details;
    public          CopyOnWriteArrayList<User> notifyIfEdited;


    private CopyOnWriteArrayList<Review> reviews;


    public void addCritic(String critica, int rate, String email) {

        reviews.add(new Review(critica, rate, email));
        ratingC += rate;
        nCritics++;

    }

    public void setDetails(String details, User s) {
        this.details = details;
        this.notifyIfEdited.add(s);
    }

    public double overallRating() {
        return ratingC / nCritics;
    }

    public String toString() {
        return tittle + " " + description + " " + ratingC;
    }

}
