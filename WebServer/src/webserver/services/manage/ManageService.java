package webserver.services.manage;

import shared.models.manage.ManageModel;

public interface ManageService {

    boolean add(ManageModel manageModel, String email);

}
