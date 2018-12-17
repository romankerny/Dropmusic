package shared.models.manage;
import shared.models.Review;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable, ManageModel {
    private static final long serialVersionUID = 1123124L;
    private          String  title;
    private          String  artist;
    private          String  description;
    private          String   genre;
    private          String launchDate;
    private          String editorLabel;
    private          float avgRating;
    private          ArrayList<Review> reviews;
    private         ArrayList<Music> songs;

    public Album(String title, String description, String genre, String artist, String launchDate, String editorLabel) {
        setTitle(title);
        setArtist(artist);
        setLaunchDate(launchDate);
        setEditorLabel(editorLabel);
        setDescription(description);
        setGenre(genre);
        setReviews(new ArrayList<Review>());
        setSongs(new ArrayList<Music>());
    }

    public Album()
    {
        this(null, null, null, null, null, null);
    }

    public Album(String title, String launchDate) {
        setTitle(title);
        setLaunchDate(launchDate);
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
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

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<Music> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Music> songs) {
        this.songs = songs;
    }
}