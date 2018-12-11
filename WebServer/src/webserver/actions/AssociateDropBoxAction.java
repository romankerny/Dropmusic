package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webserver.services.AssociateDropBoxService;

import java.util.Map;

public class AssociateDropBoxAction extends ActionSupport implements SessionAware {

    private Map<String, Object> session;
    private AssociateDropBoxService service = new AssociateDropBoxService();
    private String urlOauth;


    @Override
    public String execute() throws Exception {

        System.out.println("Executing AssociateDropBoxAction - execute()");

        setUrlOauth(getService().associateDropBox(getSession()));
        System.out.println("vou dar redirect p " + urlOauth);
        return "redirect";

    }

    public String getUrlOauth() {
        return urlOauth;
    }

    public void setUrlOauth(String urlOauth) {
        this.urlOauth = urlOauth;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public AssociateDropBoxService getService() {
        return service;
    }

    public void setService(AssociateDropBoxService service) {
        this.service = service;
    }
}
