import java.awt.*;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

public class Album implements Serializable {
    public          String  title;
    public         String  description;
    public         String   genre;
    public          CopyOnWriteArrayList<Music> tracks;
    public          double  ratingC;                    // += every single rate
    public          int     nCritics;                   // to count n
    public          String  details;
    public          CopyOnWriteArrayList<Review> reviews;

    Album(String title, String description, String genre) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.tracks = new CopyOnWriteArrayList<Music>();
        this.reviews = new CopyOnWriteArrayList<Review>();
    }

    public void addAlbum(int track, String title) {
        this.tracks.add(new Music(track, title));
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void addCritic(String critica, int rate, String email) {

        reviews.add(new Review(critica, rate, email));
        ratingC += rate;
        nCritics++;

    }

    public void setDetails(String details, User s) {
        this.details = details;
    }

    public double overallRating() {
        if (nCritics > 0)
            return ratingC/nCritics;
        else
            return 0.0;
    }

    public String toString() {

        String rsp;
        rsp = "Title: " + title + "\nGenre: "+ genre + "\nDescription: " + description + "\n";

        if (this.tracks.size() > 0) {
            rsp += "Track listing: \n";
            for (Music m : this.tracks)
                rsp += m.toString() +"\n";
        }
        rsp += "\t===== Critics ===== \n Average rating: "+overallRating() +"\n";
        if (this.reviews.size() > 0) {
            for (Review r : reviews) {
                rsp += r.toString();
            }
        } else {
            rsp += "No critics yet\n";
        }

        return rsp;
    }

}
