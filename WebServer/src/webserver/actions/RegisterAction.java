
package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webserver.models.RegisterModel;
import webserver.services.RegisterService;


import java.rmi.RemoteException;
import java.util.Map;

public class RegisterAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;


    private RegisterService registerService;

    private RegisterModel inputObject;

    @Override
    public String execute() {

        System.out.println("Executing RegisterAction - execute()");
        String r = "";

        try
        {
            if(getRegisterService().register(getInputObject()))
            {
                r = "success";
            }
            else
            {
                r = "register";
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return r;
    }


    public RegisterService getRegisterService() {
        return registerService;
    }

    public void setRegisterService(RegisterService registerService) {
        this.registerService = registerService;
    }

    public RegisterModel getInputObject() {
        return inputObject;
    }

    public void setInputObject(RegisterModel inputObject) {
        this.inputObject = inputObject;
    }

    public void setRegisterBean(RegisterModel RegisterModel) {
        this.session.put("RegisterModel", RegisterModel);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
