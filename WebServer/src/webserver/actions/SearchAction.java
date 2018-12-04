package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import webserver.models.SearchModel;
import webserver.services.SearchService;

public class SearchAction extends ActionSupport {

    private static final long serialVersionUID = 11L;

    private SearchModel inputObject =  new SearchModel();
    private SearchService searchService;

    private String results;

    public SearchAction(){
    }

    @Override
    public String execute() {
        setResults(getSearchService().search(getInputObject()));
        return Action.SUCCESS;
    }

    public SearchModel getInputObject() {
        return inputObject;
    }

    public void setInputObject(SearchModel inputObject) {
        this.inputObject = inputObject;
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
