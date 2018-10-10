import java.util.concurrent.CopyOnWriteArrayList;

public class Album {
    private String name;
    private String description;
    private Music[] tracks;
    private double rating;
    private CopyOnWriteArrayList<Review> reviews;
}
