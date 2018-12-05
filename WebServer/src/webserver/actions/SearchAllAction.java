package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import shared.SearchModel;
import webserver.services.AlbumSearchService;
import webserver.services.ArtistSearchService;
import webserver.services.MusicSearchService;

import java.util.ArrayList;

public class SearchAllAction extends ActionSupport {

    private SearchModel inputObject;
    private ArtistSearchService artistSearchService;
    private AlbumSearchService albumSearchService;
    private MusicSearchService musicSearchService;

    private ArrayList<Object> artistResults;
    private ArrayList<Object> albumResults;
    private ArrayList<Object> musicResults;


    public SearchAllAction() {
        inputObject = new SearchModel();
        artistSearchService = new ArtistSearchService();
        albumSearchService = new AlbumSearchService();
        musicSearchService = new MusicSearchService();
    }

    @Override
    public String execute() {
        setArtistResults(getArtistSearchService().search(inputObject));
        setAlbumResults(getAlbumSearchService().search(inputObject));
        setMusicResults(getMusicSearchService().search(inputObject));
        return Action.SUCCESS;
    }

    public ArtistSearchService getArtistSearchService() {
        return artistSearchService;
    }

    public void setArtistSearchService(ArtistSearchService artistSearchService) {
        this.artistSearchService = artistSearchService;
    }

    public AlbumSearchService getAlbumSearchService() {
        return albumSearchService;
    }

    public void setAlbumSearchService(AlbumSearchService albumSearchService) {
        this.albumSearchService = albumSearchService;
    }

    public MusicSearchService getMusicSearchService() {
        return musicSearchService;
    }

    public void setMusicSearchService(MusicSearchService musicSearchService) {
        this.musicSearchService = musicSearchService;
    }

    public ArrayList<Object> getArtistResults() {
        return artistResults;
    }

    public void setArtistResults(ArrayList<Object> artistResults) {
        this.artistResults = artistResults;
    }

    public ArrayList<Object> getAlbumResults() {
        return albumResults;
    }

    public void setAlbumResults(ArrayList<Object> albumResults) {
        this.albumResults = albumResults;
    }

    public ArrayList<Object> getMusicResults() {
        return musicResults;
    }

    public void setMusicResults(ArrayList<Object> musicResults) {
        this.musicResults = musicResults;
    }

    public SearchModel getInputObject() {
        return inputObject;
    }

    public void setInputObject(SearchModel inputObject) {
        this.inputObject = inputObject;
    }

}
