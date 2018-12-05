package webserver.services;

import webserver.models.SearchModel;

import java.util.ArrayList;

public interface SearchService {
    ArrayList<Object> search(SearchModel searchModel);
}
