package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webserver.services.ShareMusicDropboxService;

import java.rmi.RemoteException;
import java.util.Map;

public class ShareMusicDropboxAction extends ActionSupport implements SessionAware {

    private String email;
    private String albumTitle;
    private String artistName;
    private String musicTitle;
    private Map<String, Object> session;
    ShareMusicDropboxService service = new ShareMusicDropboxService();

    @Override
    public String execute()  {

        System.out.println("ShareMusicDropboxAction - execute()");
        String rsp = "failed";

        try {

            if(getService().shareMusic(email, artistName, albumTitle, musicTitle, (String) session.get("email"))) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
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
