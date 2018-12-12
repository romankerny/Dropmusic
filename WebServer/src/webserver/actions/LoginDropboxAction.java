package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webserver.services.LoginDropboxService;

import java.util.Map;

public class LoginDropboxAction extends ActionSupport implements SessionAware {

    private LoginDropboxService service = new LoginDropboxService();
    private String oauth_token;
    private String code;
    private Map<String, Object> session;


    @Override
    public String execute() {
        String rsp;

        System.out.println("LoginDropboxAction - execute()");
        System.out.println("Code " + code);
        System.out.println("OAuth_token " + oauth_token);

        String email = getService().canLogin(getCode());
        if(!email.equals("null"))
        {
            // set Session
            System.out.println("Sucess in LoginDropboxAction()");
            session.put("email", email);
            session.put("loggedin", true);
            return "success";

        }
        else
        {
            System.out.println("Failed LoginDropboxAction()");
            return "failed";
        }

    }

    public LoginDropboxService getService() {
        return service;
    }

    public void setService(LoginDropboxService service) {
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

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
