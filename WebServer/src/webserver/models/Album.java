package webserver.models;
import java.util.concurrent.CopyOnWriteArrayList;

public class Album {
    private          String  title;
    private         String  description;
    private          String   genre;
    private           CopyOnWriteArrayList<Music> tracks;
    private           double  ratingC;                    // += every single rate
    private           int     nCritics;                   // to count n
    private           String  details;
    private           CopyOnWriteArrayList<Review> reviews;

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
    @Override
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

    public CopyOnWriteArrayList<Music> getTracks() {
        return tracks;
    }

    public void setTracks(CopyOnWriteArrayList<Music> tracks) {
        this.tracks = tracks;
    }

    public double getRatingC() {
        return ratingC;
    }

    public void setRatingC(double ratingC) {
        this.ratingC = ratingC;
    }

    public int getnCritics() {
        return nCritics;
    }

    public void setnCritics(int nCritics) {
        this.nCritics = nCritics;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public CopyOnWriteArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(CopyOnWriteArrayList<Review> reviews) {
        this.reviews = reviews;
    }
}