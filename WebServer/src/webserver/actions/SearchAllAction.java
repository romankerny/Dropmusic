package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import shared.models.SearchModel;
import shared.models.manage.Album;
import shared.models.manage.Artist;
import shared.models.manage.Music;

import java.util.ArrayList;

public class SearchAllAction extends ActionSupport {

    private SearchModel model;

    private ArrayList<Artist> artistResults;
    private ArrayList<Album> albumResults;
    private ArrayList<Music> musicResults;

    @Override
    public String execute() {
        setResults(getModel().searchAll());
        return Action.SUCCESS;
    }

    public void setResults(ArrayList<Object> objects) {
        ArrayList<Artist> artists = new ArrayList<>();
        ArrayList<Album> albums = new ArrayList<>();
        ArrayList<Music> songs = new ArrayList<>();
        for (Object o : objects) {
            if (o instanceof Artist)
                artists.add((Artist) o);

            if (o instanceof Album)
                albums.add((Album) o);

            if (o instanceof Music)
                songs.add((Music) o);
        }

        setArtistResults(artists);
        setAlbumResults(albums);
        setMusicResults(songs);
    }

    public SearchModel getModel() {
        return model;
    }

    public void setModel(SearchModel model) {
        this.model = model;
    }

    public ArrayList<Artist> getArtistResults() {
        return artistResults;
    }

    public void setArtistResults(ArrayList<Artist> artistResults) {
        this.artistResults = artistResults;
    }

    public ArrayList<Album> getAlbumResults() {
        return albumResults;
    }

    public void setAlbumResults(ArrayList<Album> albumResults) {
        this.albumResults = albumResults;
    }

    public ArrayList<Music> getMusicResults() {
        return musicResults;
    }

    public void setMusicResults(ArrayList<Music> musicResults) {
        this.musicResults = musicResults;
    }

}
