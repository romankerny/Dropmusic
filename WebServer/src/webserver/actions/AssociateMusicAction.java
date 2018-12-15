package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.AssociateMusicModel;
import webserver.services.AssociateMusicService;
import java.util.Map;

public class AssociateMusicAction extends ActionSupport implements SessionAware {

    private Map<String, Object> session;

    private AssociateMusicModel model = new AssociateMusicModel();
    private AssociateMusicService service = new AssociateMusicService();

    @Override
    public String execute() {

        if(service.associateMusic(session, model.getArtistName(), model.getAlbumTitle(), model.getMusicTitle(), model.getFileName())) {
            return "success";
        }
        else
        {
            return "failed";
        }

    }


    public AssociateMusicModel getModel() {
        return model;
    }

    public void setModel(AssociateMusicModel model) {
        this.model = model;
    }

    public AssociateMusicService getService() {
        return service;
    }

    public void setService(AssociateMusicService service) {
        this.service = service;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
