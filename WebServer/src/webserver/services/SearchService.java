package webserver.services;

import shared.SearchModel;

import java.util.ArrayList;

public interface SearchService {
    ArrayList<Object> search(SearchModel searchModel);
}
