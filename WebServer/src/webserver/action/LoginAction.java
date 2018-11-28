/**
 * Raul Barbosa 2014-11-07
 */
package webserver.action;

import com.opensymphony.xwork2.ActionSupport;
import webserver.model.LoginBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

public class LoginAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private String username = null, password = null;

	@Override
	public String execute() {
		// any username is accepted without confirmation (should check using RMI)
		if(this.username != null && !username.equals("")) {

			this.getLoginBean().setUsername(this.username);
			this.getLoginBean().setPassword(this.password);

			try {
				if(this.getLoginBean().getUserMatchesPassword()) {

					session.put("username", username);
					session.put("loggedin", true); // this marks the user as logged in
					return SUCCESS;

				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

		return INPUT;
	}
	
	public void setUsername(String username) {
		this.username = username; // will you sanitize this input? maybe use a prepared statement?
	}

	public void setPassword(String password) {
		this.password = password; // what about this input? 
	}
	
	public LoginBean getLoginBean() {
		if(!session.containsKey("loginBean"))
			this.setLoginBean(new LoginBean());
		return (LoginBean) session.get("loginBean");
	}

	public void setLoginBean(LoginBean loginBean) {
		this.session.put("loginBean", loginBean);
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
