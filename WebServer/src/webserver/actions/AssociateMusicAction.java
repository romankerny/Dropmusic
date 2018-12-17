package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.models.manage.MusicModel;

import java.util.Map;

/**
 *
 *  Action call's the associateMusic method in the Model.
 *
 */
public class AssociateMusicAction extends ActionSupport implements SessionAware {

    private Map<String, Object> session;
    private String email;
    private MusicModel model = new MusicModel();


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
