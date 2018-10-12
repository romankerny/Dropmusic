import java.util.concurrent.CopyOnWriteArrayList;

public class Album {
    public          String  tittle;
    private         String  description;
    private         Music[] tracks;
    public          double  ratingC;                    // += every single rate
    public          int     nCritics;                   // to count n

    private CopyOnWriteArrayList<Review> reviews;


    public void addCritic(String critica, int rate) {

        reviews.add(new Review(critica, rate));
        ratingC += rate;
        nCritics++;

    }

    public double overallRating() {
        return ratingC / nCritics;
    }

    public String toString() {
        return tittle + " " + description + " " + ratingC;
    }

}
