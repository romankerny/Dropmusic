package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import shared.Review;
import ws.WebSocketAnnotation;

public class ReviewAction extends ActionSupport {

    private Review reviewModel;

    private double result;

    @Override
    public String execute() {
        result = getReviewModel().addReview(reviewModel);
        String msg = result+"#"+reviewModel.getAlbum()+"#"+reviewModel.getRating()+"#"+reviewModel.getEmail()+"#"+reviewModel.getCritic();
        WebSocketAnnotation.updateAlbumRating(msg);
        return Action.SUCCESS;
    }

    public Review getReviewModel() {
        return reviewModel;
    }

    public void setReviewModel(Review reviewModel) {
        this.reviewModel = reviewModel;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
