
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
		System.out.println("no construtor da login action..");
	}


	@Override
	public String execute() {
		String r = "";

		System.out.println("Login Action");
		try {
			if(getLoginService().login(getInputObject()))
			{ // if true then user can log
				r = SUCCESS;
				System.out.printf("sucess");

			}
			else
			{// if false make user log
				System.out.println("failed");
				r = LOGIN;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return r;
	}


	public LoginService getLoginService() {
		System.out.println("No get login service");
		System.out.println(this.loginService);
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
		System.out.println("a dar set ao service");

	}


	public LoginModel getInputObject() {
		System.out.println("no get input object");
		System.out.println(inputObject);
		/*
		System.out.println("No getInputObject");

		if(!session.containsKey("loginModel"))
			this.setInputObject(new LoginModel());
		return (LoginModel) session.get("loginModel");
		*/
		return inputObject;
	}

	public void setInputObject(LoginModel inputObject) {
		System.out.println("no set input object");
		this.inputObject = inputObject;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
