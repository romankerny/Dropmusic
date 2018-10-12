import java.util.concurrent.CopyOnWriteArrayList;

public class Album {
    public String tittle;
    private String description;
    private Music[] tracks;
    private double rating;
    private CopyOnWriteArrayList<Review> reviews;


    public String toString() {
        return tittle + " " + description + " " + rating;
    }

}
