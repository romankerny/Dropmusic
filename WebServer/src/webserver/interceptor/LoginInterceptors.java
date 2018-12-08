package webserver.interceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import java.util.Map;

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
            System.out.println("Isn't logged, redirirect");
            return "login";
        }

    }



    @Override
    public void init() { }

    @Override
    public void destroy() { }



}
