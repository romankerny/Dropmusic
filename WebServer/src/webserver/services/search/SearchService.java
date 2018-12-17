package webserver.services.search;

import shared.models.manage.ManageModel;

/**
 * All search services must implement this interface
 */

public interface SearchService {
    Object search(ManageModel searchModel);
}
