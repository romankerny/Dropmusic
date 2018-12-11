package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webserver.services.AssociateDropBoxTokenService;

import java.util.Map;

public class AssociateDropBoxTokenAction extends ActionSupport implements SessionAware {

    private AssociateDropBoxTokenService service = new AssociateDropBoxTokenService();
    private String oauth_token;
    private String code;
    private Map<String, Object> session;

    @Override
    public String execute() {
        String rsp;

        if(getService().setUserToken(session, getCode()))
        {
            return "success";
        }
        else
        {
            return "failed";
        }

    }

    public AssociateDropBoxTokenService getService() {
        return service;
    }

    public void setService(AssociateDropBoxTokenService service) {
        this.service = service;
    }

    public String getOauth_token() {
        return oauth_token;
    }

    public void setOauth_token(String oauth_token) {
        this.oauth_token = oauth_token;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
