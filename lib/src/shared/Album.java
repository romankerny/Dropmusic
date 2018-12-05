package shared;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

public class Album implements Serializable {
    private static final long serialVersionUID = 1123124L;
    private          String  title;
    private         String  description;
    private          String   genre;
    private         String launchDate;
    private         String editorLabel;
    private         float avgRating;
    private           CopyOnWriteArrayList<Review> reviews;

    public Album(String title, String description, String genre) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.reviews = new CopyOnWriteArrayList<Review>();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {

        String rsp;
        rsp = "Title: " + title + "\nGenre: "+ genre + "\nDescription: " + description + "\n";

        rsp += "\t===== Critics ===== \n Average rating: " + avgRating +"\n";
        if (this.reviews.size() > 0) {
            for (Review r : reviews) {
                rsp += r.toString();
            }
        } else {
            rsp += "No critics yet\n";
        }

        return rsp;
    }

    public String getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(String launchDate) {
        this.launchDate = launchDate;
    }

    public String getEditorLabel() {
        return editorLabel;
    }

    public void setEditorLabel(String editorLabel) {
        this.editorLabel = editorLabel;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public CopyOnWriteArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(CopyOnWriteArrayList<Review> reviews) {
        this.reviews = reviews;
    }
}