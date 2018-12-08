package webserver.actions;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webserver.services.TurnIntoEditorService;

import javax.websocket.Session;
import java.rmi.RemoteException;
import java.util.Map;

public class TurnIntoEditorAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;

    private String regular;
    private TurnIntoEditorService turnIntoEditorService = new TurnIntoEditorService();


    @Override
    public String execute() {

        String r = "";
        session = ActionContext.getContext().getSession();

        System.out.println("Executing TurnIntoEditor - execute()");
        try {
            if(getTurnIntoEditorService().regularToEditor((String) session.get("email"), regular))
            {
                System.out.println("sucesso no turn");
                r = "success";
            }
            else
            {
                System.out.println("nao sucesso no turn");
                r = "failed";
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return r;


    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }

    public TurnIntoEditorService getTurnIntoEditorService() {
        return turnIntoEditorService;
    }

    public void setTurnIntoEditorService(TurnIntoEditorService turnIntoEditorService) {
        this.turnIntoEditorService = turnIntoEditorService;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

}
