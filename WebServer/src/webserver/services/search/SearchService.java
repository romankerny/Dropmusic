package webserver.services.search;

import shared.SearchModel;
import shared.manage.ManageModel;

import java.util.ArrayList;

public interface SearchService {
    Object search(ManageModel searchModel);
}
