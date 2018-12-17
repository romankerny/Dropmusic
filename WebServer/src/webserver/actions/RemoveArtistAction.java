package webserver.actions;

import com.opensymphony.xwork2.ActionSupport;
import shared.models.manage.ArtistModel;

/**
 *  This action controls the execution of the remoteArtist operation.
 *  The remove method is in the ArtistModel Bean.
 */
public class RemoveArtistAction extends ActionSupport {

    private ArtistModel model = new ArtistModel();

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


    public ArtistModel getModel() {
        return model;
    }

    public void setModel(ArtistModel model) {
        this.model = model;
    }
}
