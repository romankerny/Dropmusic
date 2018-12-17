package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import shared.models.SearchModel;
import shared.models.manage.AlbumModel;
import shared.models.manage.ArtistModel;
import shared.models.manage.MusicModel;

import java.util.ArrayList;

public class SearchAllAction extends ActionSupport {

    private SearchModel model;

    private ArrayList<ArtistModel> artistResults;
    private ArrayList<AlbumModel> albumResults;
    private ArrayList<MusicModel> musicResults;

    @Override
    public String execute() {
        setResults(getModel().searchAll());
        return Action.SUCCESS;
    }

    public void setResults(ArrayList<Object> objects) {
        ArrayList<ArtistModel> artists = new ArrayList<>();
        ArrayList<AlbumModel> albums = new ArrayList<>();
        ArrayList<MusicModel> songs = new ArrayList<>();
        for (Object o : objects) {
            if (o instanceof ArtistModel)
                artists.add((ArtistModel) o);

            if (o instanceof AlbumModel)
                albums.add((AlbumModel) o);

            if (o instanceof MusicModel)
                songs.add((MusicModel) o);
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

    public ArrayList<ArtistModel> getArtistResults() {
        return artistResults;
    }

    public void setArtistResults(ArrayList<ArtistModel> artistResults) {
        this.artistResults = artistResults;
    }

    public ArrayList<AlbumModel> getAlbumResults() {
        return albumResults;
    }

    public void setAlbumResults(ArrayList<AlbumModel> albumResults) {
        this.albumResults = albumResults;
    }

    public ArrayList<MusicModel> getMusicResults() {
        return musicResults;
    }

    public void setMusicResults(ArrayList<MusicModel> musicResults) {
        this.musicResults = musicResults;
    }

}
