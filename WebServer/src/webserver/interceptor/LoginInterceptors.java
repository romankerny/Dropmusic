package webserver.interceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import java.util.Map;

/**
 * This interceptor is added to the default Stack interceptor. It's only deactivated in the login and register actions.
 * It prevents user that haven't logged in from accessing pages that are only meant to see by logged users.
 * If the user isn't 'loggedin' then he's redirected to the index.jsp page.
 * If he is then invocation.invoke() allows the user to continue.
 *
 */
public class LoginInterceptors implements Interceptor {

    private static final long serialVersionUID = 189237412378L; // isto é preciso?


    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> session = invocation.getInvocationContext().getSession();


        System.out.println("Intercepting . . .");

        if(session.get("loggedin") != null)
        {
            // then user is logged
            System.out.println("[" + session.get("email") + "] has a session.");
            return invocation.invoke();
        }
        else {
             // user is not logged
            System.out.println("Isn't logged, redirect");
            return "login";
        }

    }



    @Override
    public void init() { }

    @Override
    public void destroy() { }



}
