package webserver.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import shared.Review;
import webserver.services.ReviewService;

public class ReviewAction extends ActionSupport {

    private Review reviewModel;
    private ReviewService reviewService;

    @Override
    public String execute() {

        return Action.SUCCESS;
    }



}
