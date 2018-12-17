package webserver.services.manage;

import shared.models.manage.ManageModel;

/**
 * Interface for the ManageService. Hold by the ManageAction.
 */

public interface ManageService {

    boolean add(ManageModel manageModel, String email);

}
