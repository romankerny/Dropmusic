package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import webserver.models.SearchBean;

import java.rmi.RemoteException;
import java.util.Map;

public class SearchAction extends ActionSupport implements SessionAware {
    private String keyword = null;
    Map<String, Object> session;

    @Override
    public String execute() {
        if (this.keyword != null) {
            SearchBean sb = this.getSearchBean();
            sb.setKeyword(keyword);
            try {
                if (sb.details()) {
                    return SUCCESS;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return Action.INPUT;
    }

    public SearchBean getSearchBean() {
        if (!this.session.containsKey("searchBean"))
            this.setSearchBean(new SearchBean());
        return (SearchBean) this.session.get("searchBean");
    }

    public void setKeyword(String keyword) {this.keyword = keyword;}

    public void setSearchBean(SearchBean searchBean) {
        this.session.put("searchBean", searchBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
