
package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webserver.models.LoginModel;
import webserver.services.LoginService;

import java.rmi.RemoteException;
import java.util.Map;

public class LoginAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;

	private LoginService loginService;

	private LoginModel inputObject;

	public LoginAction() {

	}


	@Override
	public String execute() {
		String r = "";

		System.out.println("Executing LoginAction - execute()");
		try {
			if(getLoginService().login(getInputObject()))
			{ // if true then user can log
				r = "success";
			}
			else
			{// if false make user log
				r = "login";
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return r;
	}


	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}


	public LoginModel getInputObject() {
		/*
		System.out.println("No getInputObject");

		if(!session.containsKey("loginModel"))
			this.setInputObject(new LoginModel());
		return (LoginModel) session.get("loginModel");
		*/
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
