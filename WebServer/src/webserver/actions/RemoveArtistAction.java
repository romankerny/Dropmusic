package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import shared.manage.Artist;

public class RemoveArtistAction extends ActionSupport {

    Artist model = new Artist();

    @Override
    public String execute() {
        if(getModel().removeArtist())
        {
            return "success";
        }
        else
        {
            return "failed";
        }
    }


    public Artist getModel() {
        return model;
    }

    public void setModel(Artist model) {
        this.model = model;
    }
}
