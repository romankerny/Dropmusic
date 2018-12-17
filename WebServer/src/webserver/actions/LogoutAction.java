package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * This action simply cleans the session and returns the user to the index.jsp.
 */
public class LogoutAction extends ActionSupport implements SessionAware {

    private Map<String, Object> session;


    @Override
    public String execute() {
        this.session.clear();
        return SUCCESS;
    }


    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
