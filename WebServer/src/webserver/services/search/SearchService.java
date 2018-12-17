package webserver.services.search;

import shared.models.manage.ManageModel;

public interface SearchService {
    Object search(ManageModel searchModel);
}
