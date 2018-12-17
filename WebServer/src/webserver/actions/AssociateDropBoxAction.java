package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import webserver.services.AssociateDropBoxService;

import java.util.Map;

/**
 * This action is used to return an url to the user's web browser so that he can give his credentials
 * to Dropbox, and allow our app, DropMusic, the privileges to edit the user's Dropbox content.
 *
 */

public class AssociateDropBoxAction extends ActionSupport {

    private AssociateDropBoxService service = new AssociateDropBoxService();
    private String urlOauth = "https://www.dropbox.com/1/oauth2/authorize?client_id=wbwulmkt4ykv4ry&response_type=code&redirect_uri=https://10.16.0.108:8443/associateDropBoxTokenAction&force_reapprove=false";



    /**
     * Calls the service for an URL to return to the user
     * @return Dropbox Page to the user
     * @throws Exception
     */
    @Override
    public String execute() throws Exception {

        System.out.println("Executing AssociateDropBoxAction - execute()");

        //setUrlOauth(getService().associateDropBox());
        System.out.println("vou dar redirect p/ " + urlOauth);
        return "redirect";

    }

    public String getUrlOauth() {
        return urlOauth;
    }

    public void setUrlOauth(String urlOauth) {
        this.urlOauth = urlOauth;
    }

    public AssociateDropBoxService getService() {
        return service;
    }

    public void setService(AssociateDropBoxService service) {
        this.service = service;
    }
}
