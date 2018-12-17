package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import shared.models.manage.Album;
import shared.models.manage.Artist;
import shared.models.manage.Music;
import webserver.services.search.SearchAllService;

import java.util.ArrayList;

public class SearchAllAction extends ActionSupport {

    private String keyword;

    private ArrayList<Artist> artistResults;
    private ArrayList<Album> albumResults;
    private ArrayList<Music> musicResults;

    private SearchAllService service = new SearchAllService();

    @Override
    public String execute() {
        setResults(getService().searchAll(getKeyword()));
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

    public SearchAllService getService() {
        return service;
    }

    public void setService(SearchAllService service) {
        this.service = service;
    }
}
