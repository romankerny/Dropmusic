package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import shared.SearchModel;
import webserver.services.SearchService;

import java.util.ArrayList;

public class SearchAction extends ActionSupport {

    private SearchModel inputObject =  new SearchModel();
    private SearchService searchService;

    private ArrayList<Object> results;

    public SearchAction(){
    }

    @Override
    public String execute() {
        System.out.println("Searching for "+inputObject.getKeyword());
        setResults(getSearchService().search(getInputObject()));

        System.out.println("Before ACTION>SUCESS");
        System.out.println(results.size() + "Got results");

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

    public ArrayList<Object> getResults() {
        return results;
    }

    public void setResults(ArrayList<Object> results) {
        this.results = results;
    }
}
