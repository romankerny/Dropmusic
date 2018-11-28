
package webserver.model;
import rmiserver.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


public class LoginBean {
	private RMIServerInterface server;
	private String email; // email and password supplied by the user
	private String password;

	public LoginBean() {
		try {
			server = (RMIServerInterface) LocateRegistry.getRegistry("localhost", 1099).lookup("rmiserver");
		}
		catch(NotBoundException|RemoteException e) {
			e.printStackTrace(); // what happens *after* we reach this line?
		}
	}



	public boolean getUserMatchesPassword() throws RemoteException {

		boolean r;
		String rsp = server.login(email, password);

		if (rsp.equals("Logged in successfully " + email))
		{
			r = true;
		} else
		{
			r = false;
		}

		return r;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
