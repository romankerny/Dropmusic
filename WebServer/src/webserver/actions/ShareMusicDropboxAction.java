package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.manage.ShareModel;
import webserver.services.ShareMusicDropboxService;

import java.rmi.RemoteException;
import java.util.Map;

public class ShareMusicDropboxAction extends ActionSupport implements SessionAware {

    private ShareModel model = new ShareModel();



    private Map<String, Object> session;
    ShareMusicDropboxService service = new ShareMusicDropboxService();

    @Override
    public String execute()  {

        System.out.println("ShareMusicDropboxAction - execute()");
        String rsp = "failed";

        try {

            if(getService().shareMusic(model.getEmail(), model.getArtistName(), model.getAlbumTitle(), model.getMusicTitle(), (String) session.get("email"))) {
                rsp = "success";
            }
            else
            {
                rsp = "failed";
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return rsp;

    }

    public ShareModel getModel() {
        return model;
    }

    public void setModel(ShareModel model) {
        this.model = model;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public ShareMusicDropboxService getService() {
        return service;
    }

    public void setService(ShareMusicDropboxService service) {
        this.service = service;
    }
}
