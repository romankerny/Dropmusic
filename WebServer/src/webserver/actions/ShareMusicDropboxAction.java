package webserver.actions;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.models.manage.Music;
import java.util.Map;

public class ShareMusicDropboxAction extends ActionSupport implements SessionAware {

    private Music model = new Music();
    private Map<String, Object> session;


    @Override
    public String execute()  {
        System.out.println("ShareMusicDropboxAction - execute()");
        String rsp;


        if(getModel().shareMusic(model.getEmail(), model.getArtistName(), model.getAlbumTitle(), model.getTitle(), (String) session.get("email")))
        {
            rsp = "success";
        }
        else
        {
            rsp = "failed";
        }


        return rsp;

    }

    public Music getModel() {
        return model;
    }

    public void setModel(Music model) {
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
