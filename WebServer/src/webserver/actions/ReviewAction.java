package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import shared.models.ReviewModel;
import ws.WebSocketAnnotation;

public class ReviewAction extends ActionSupport {

    private ReviewModel reviewModel;

    private double result;

    @Override
    public String execute() {
        result = getReviewModel().addReview(reviewModel);
        String msg = result+"#"+reviewModel.getAlbum()+"#"+reviewModel.getRating()+"#"+reviewModel.getEmail()+"#"+reviewModel.getCritic();
        WebSocketAnnotation.updateAlbumRating(msg);
        return Action.SUCCESS;
    }

    public ReviewModel getReviewModel() {
        return reviewModel;
    }

    public void setReviewModel(ReviewModel reviewModel) {
        this.reviewModel = reviewModel;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
