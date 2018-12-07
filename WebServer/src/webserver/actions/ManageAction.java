package webserver.actions;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import shared.manage.ManageModel;
import webserver.services.manage.ManageService;

import java.util.Map;

public class ManageAction extends ActionSupport {


    private Map<String, Object> session;

    private ManageService manageService;
    private ManageModel manageModel;

    @Override
    public String execute()
    {
        session = ActionContext.getContext().getSession();
        boolean r = getManageService().add(getManageModel(), (String) session.get("email"));
        if(r) {
            return "success";
        } else {
            return "failed";
        }
    }

    public ManageService getManageService() {
        return manageService;
    }

    public void setManageService(ManageService manageService) {
        this.manageService = manageService;
    }

    public ManageModel getManageModel() {
        return manageModel;
    }

    public void setManageModel(ManageModel manageModel) {
        this.manageModel = manageModel;
    }
}
