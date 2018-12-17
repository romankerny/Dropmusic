package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import shared.models.ReviewModel;
import webserver.services.ReviewService;
import ws.WebSocketAnnotation;

public class ReviewAction extends ActionSupport {

    private ReviewModel reviewModel;
    private ReviewService service = new ReviewService();

    private double result;

    @Override
    public String execute() {
        result = getService().addReview(reviewModel);
        if (result != -1)
            return Action.SUCCESS;
        else
            return Action.ERROR;
    }

    public ReviewModel getReviewModel() {
        return reviewModel;
    }

    public void setReviewModel(ReviewModel reviewModel) {
        this.reviewModel = reviewModel;
    }

    public ReviewService getService() {
        return service;
    }

    public void setService(ReviewService service) {
        this.service = service;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
