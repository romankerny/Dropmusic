
package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.models.LoginModel;

import java.rmi.RemoteException;
import java.util.Map;

public class LoginAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;

	private String email = null;
	private String password = null;


	public LoginAction() {

	}


	@Override
	public String execute() {
		String r = "";

		System.out.println("Executing LoginAction - execute()");


		this.getLoginModel().setEmail(this.email);
		this.getLoginModel().setPassword(this.password);

		System.out.println(this.getLoginModel().getEmail());

		try {

			if(this.getLoginModel().login())
			{   // if true then user can log
				// set session parameters
				session.put("email", email);
				session.put("loggedin", true);
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

	public LoginModel getLoginModel() {
		if(!session.containsKey("loginModel"))
			this.setLoginModel(new LoginModel());
		return (LoginModel) session.get("loginModel");
	}


	public void setLoginModel(LoginModel loginModel) {
		this.session.put("loginModel", loginModel);
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}



	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
