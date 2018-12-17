package shared.models.manage;
import shared.models.ReviewModel;

import java.io.Serializable;
import java.util.ArrayList;

public class AlbumModel implements Serializable, ManageModel {
    private static final long serialVersionUID = 1123124L;
    private          String  title;
    private          String  artist;
    private          String  description;
    private          String   genre;
    private          String launchDate;
    private          String editorLabel;
    private          float avgRating;
    private          ArrayList<ReviewModel> reviews;
    private         ArrayList<MusicModel> songs;

    public AlbumModel(String title, String description, String genre, String artist, String launchDate, String editorLabel) {
        setTitle(title);
        setArtist(artist);
        setLaunchDate(launchDate);
        setEditorLabel(editorLabel);
        setDescription(description);
        setGenre(genre);
        setReviews(new ArrayList<ReviewModel>());
        setSongs(new ArrayList<MusicModel>());
    }

    public AlbumModel()
    {
        this(null, null, null, null, null, null);
    }

    public AlbumModel(String title, String launchDate) {
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
            for (ReviewModel r : reviews) {
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

    public ArrayList<ReviewModel> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<ReviewModel> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<MusicModel> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<MusicModel> songs) {
        this.songs = songs;
    }
}