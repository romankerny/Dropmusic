package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import shared.manage.Music;
import webserver.services.PlayService;

import java.util.Map;

public class PlayAction extends ActionSupport implements SessionAware {
    private Map<String, Object> session;
    private PlayService service = new PlayService();

    private Music inputModel;

    private String url;

    @Override public String execute() {
        setUrl(getService().getURL(inputModel, (String) session.get("email")));
        return Action.SUCCESS;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    public PlayService getService() {
        return service;
    }

    public void setService(PlayService service) {
        this.service = service;
    }
}
