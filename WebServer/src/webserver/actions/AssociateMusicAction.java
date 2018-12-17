package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.manage.Music;

import java.util.Map;

public class AssociateMusicAction extends ActionSupport implements SessionAware {

    private Map<String, Object> session;


    private Music model = new Music();

    @Override
    public String execute() {

        if(model.associateMusic(session, model.getArtistName(), model.getAlbumTitle(), model.getTitle(), model.getFileName())) {
            return "success";
        }
        else
        {
            return "failed";
        }

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
