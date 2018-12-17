
package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.models.LoginModel;


import java.rmi.RemoteException;
import java.util.Map;

/**
 * Action that controls the resgister operation.
 * If successful returns the user to the index.jsp page, so that he can login.
 */

public class RegisterAction extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;

    private LoginModel inputObject = new LoginModel();

    @Override
    public String execute() {

        System.out.println("Executing RegisterAction - execute()");
        String r;

        // register method is in the LoginModel Bean
        if(getInputObject().register(getInputObject()))
        {
            r = "success";
        }
        else
        {
            r = "register";
        }

        return r;
    }


    public LoginModel getInputObject() {
        return inputObject;
    }

    public void setInputObject(LoginModel inputObject) {
        this.inputObject = inputObject;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
