package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import shared.Review;
import webserver.services.ReviewService;

import java.util.Map;

public class ReviewAction extends ActionSupport {

    private Review reviewModel;
    private ReviewService reviewService = new ReviewService();

    private double result;

    @Override
    public String execute() {
        result = reviewService.addReview(reviewModel);
        System.out.println(result);
        return Action.SUCCESS;
    }

    public Review getReviewModel() {
        return reviewModel;
    }

    public void setReviewModel(Review reviewModel) {
        this.reviewModel = reviewModel;
    }

    public ReviewService getReviewService() {
        return reviewService;
    }

    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
