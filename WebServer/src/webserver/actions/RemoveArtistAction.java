package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import shared.RemoveArtistModel;

public class RemoveArtistAction extends ActionSupport {

    RemoveArtistModel model = new RemoveArtistModel();


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


    public RemoveArtistModel getModel() {
        return model;
    }

    public void setModel(RemoveArtistModel model) {
        this.model = model;
    }
}
