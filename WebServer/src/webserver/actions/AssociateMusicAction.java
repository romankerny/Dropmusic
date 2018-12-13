package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webserver.services.AssociateMusicService;
import java.util.Map;

public class AssociateMusicAction extends ActionSupport implements SessionAware {

    private String albumTitle;
    private String artistName;
    private String musicTitle;
    private String fileName;
    private Map<String, Object> session;
    private AssociateMusicService service = new AssociateMusicService();

    @Override
    public String execute() {

        if(service.associateMusic(session, artistName, albumTitle, musicTitle, fileName)) {
            return "success";
        }
        else
        {
            return "failed";
        }

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
