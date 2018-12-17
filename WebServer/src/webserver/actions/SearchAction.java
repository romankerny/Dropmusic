package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import shared.models.manage.ManageModel;
import webserver.services.search.SearchService;

/**
 * Generic search Action that is injected with appropriate model (ArtistModel, AlbumModel or MusicModel) and
 * service (ArtistSearchService, AlbumSearchService, MusicSearchService) by Spring
 * Variable `result` is an Object that holds the resulting ArtistModel, AlbumModel or MusicModel.
 */

public class SearchAction extends ActionSupport {

    private ManageModel inputObject;
    private SearchService searchService;

    private Object result;

    public SearchAction(){
    }

    @Override
    public String execute() {
        setResult(getSearchService().search(getInputObject()));
        System.out.println(result);

        return Action.SUCCESS;
    }

    public ManageModel getInputObject() {
        return inputObject;
    }

    public void setInputObject(ManageModel inputObject) {
        this.inputObject = inputObject;
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
