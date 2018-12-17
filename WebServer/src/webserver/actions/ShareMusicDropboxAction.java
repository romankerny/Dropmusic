package webserver.actions;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.models.manage.MusicModel;

import java.util.Map;

/**
 *
 * This action controls the execution of shareMusic.
 * shareMusic() is contained in the MusicModel and accesses the RMI Server.
 *
 */
public class ShareMusicDropboxAction extends ActionSupport implements SessionAware {

    private MusicModel model;
    private String email;
    private Map<String, Object> session;


    @Override
    public String execute()  {
        System.out.println("ShareMusicDropboxAction - execute() "+email);
        String rsp;

        if(getModel().shareMusic(getEmail() ,(String) session.get("email")))
        {
            rsp = "success";
        }
        else
        {
            rsp = "failed";
        }


        return rsp;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MusicModel getModel() {
        return model;
    }

    public void setModel(MusicModel model) {
        this.model = model;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

}
