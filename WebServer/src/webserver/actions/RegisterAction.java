
package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import webserver.models.RegisterBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

public class RegisterAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String email = null, password = null;

    @Override
    public String execute() {
        // any username is accepted without confirmation (should check using RMI)
        System.out.println("Executing RegisterAction");

        if(this.email != null && !email.equals(""))
        {

            this.getRegisterBean().setEmail(this.email);
            this.getRegisterBean().setPassword(this.password);

            try
            {
                if(this.getRegisterBean().getRegister())
                {
                    //getRegister is the name of the register method
                    System.out.println("Sign up user " + email);
                    // session.put("username", email);
                    // session.put("loggedin", true); // this marks the user as logged in
                    return SUCCESS;

                }
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }

        }
        System.out.println("Failed to sign up " + email);
        return INPUT;
    }

    public void setEmail(String email) {
        this.email = email; // will you sanitize this input? maybe use a prepared statement?
    }

    public void setPassword(String password) {
        this.password = password; // what about this input?
    }

    public RegisterBean getRegisterBean() {
        if(!session.containsKey("RegisterBean"))
            this.setRegisterBean(new RegisterBean());
        return (RegisterBean) session.get("RegisterBean");
    }

    public void setRegisterBean(RegisterBean RegisterBean) {
        this.session.put("RegisterBean", RegisterBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
