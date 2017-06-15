package love.flicli.model;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedList;

import love.flicli.MVC;
import love.flicli.view.View;

/**
 * Created by tommaso on 18/05/17.
 */

@ThreadSafe
public class Model {
    private MVC mvc;
    private final static int MAX_FLICK = 50;

    @GuardedBy("Itself")
    private LinkedList<FlickModel> flickers;
    private FlickModel flick;

    public void setMVC(MVC mvc) {
        this.mvc = mvc;
        flickers = new LinkedList<FlickModel>();
    }

    public void storeFactorization(FlickModel flick) {
        for (FlickModel currentFlick: this.flickers) {
            if (currentFlick.getId() == flick.getId()) {
                return;
            }
        }

        this.flickers.add(flick);

        mvc.forEachView(View::onModelChanged);
    }

    public void storeComments(ArrayList<Comment> comments, String photo_id) {
        for (FlickModel flick :  mvc.model.getFlickers()) {
            if (flick.getId() == photo_id) {
                flick.setComments(comments);
                this.flick = flick;
            }
        }
    }

    public LinkedList<FlickModel> getFlickers() {
        return this.flickers;
    }

    public FlickModel getDetailFlicker()  { return this.flick; }

    public void freeFlickers() { this.flickers.clear(); }
//
//    public void storeComments(FlickerService.Comments[] c) {
//        comments = c.clone();
//        mvc.forEachView(View::onModelChanged);
//    }
//
//    public void storeLastFlick(FlickerService.Flick flick) {
//        lastFlick = flick;
//    }
//
//    public FlickerService.Comments[] getComments() {
//        if (comments != null)
//            return comments.clone();
//        else
//            return new FlickerService.Comments[0];
//    }
//
//    public FlickerService.Flick getLastFlick() { return lastFlick; }
//
//    //return the list of the search Flick
//    public FlickerService.Flick[] getFlickers() {
//        if (flickers != null)
//            return flickers.clone();
//        else
//            return new FlickerService.Flick[0];
//    }
}
