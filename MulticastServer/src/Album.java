import java.util.concurrent.CopyOnWriteArrayList;

public class Album {
    public          String  tittle;
    private         String  description;
    private         Music[] tracks;
    public          double  ratingC;                    // += every single rate
    public          int     nCritics;                   // to count n
    public          String  details;
    public          CopyOnWriteArrayList<User> notifyIfEdited;
    public CopyOnWriteArrayList<Review> reviews;

    Album(String tittle, String description) {
        this.tittle = tittle;
        this.description = description;
        this.reviews = new CopyOnWriteArrayList<Review>();
        this.notifyIfEdited = new CopyOnWriteArrayList<User>();
    }


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

        String rsp;
        rsp = "Tittle: " + tittle + "\n" + "Description: " + description + "\n" + "Track-list: ";
        for (Music m : this.tracks)
            rsp += m.title + " ";
        rsp += "Critics: ";
        for (Review r : reviews) {
            rsp += r.toString();
        }

        return rsp;
    }

}
