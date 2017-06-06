package love.flicli.model;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

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
    private FlickModel detailFlicker;

    public void setMVC(MVC mvc) {
        this.mvc = mvc;
    }

    public void storeFactorization(LinkedList<FlickModel> flickers) {
        this.flickers = flickers;
        mvc.forEachView(View::onModelChanged);
    }

    public LinkedList<FlickModel> getFlickers() {
        if (flickers != null)
            return flickers;
        else
            return new LinkedList<FlickModel>();
    }

    public void storeDetailFlicker(FlickModel flickModel) {
        this.detailFlicker = flickModel;
    }

    public FlickModel getDetailFlicker()  {
        return this.detailFlicker;
    }

    public void storeFlickDinamically(FlickModel flickModel) {
        flickers.add(flickModel);
        mvc.forEachView(View::onModelChanged);
    }

    public void freeFlickers() {
        this.flickers = new LinkedList<FlickModel>();
    }
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
