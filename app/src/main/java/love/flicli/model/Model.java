package love.flicli.model;
import android.graphics.Bitmap;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedList;

import love.flicli.MVC;
import love.flicli.view.View;

import static love.flicli.R.id.comments;

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

    public void storeDetail(String photo_id, int favs, ArrayList<Comment> comments, Bitmap bitmap_z ) {

        for (FlickModel flick :  mvc.model.getFlickers()) {
            if (flick.getId().compareTo(photo_id) == 0) {
                flick.setComments(comments);
                flick.setFavourities(String.valueOf(favs));
                flick.setBitmap_url_h(bitmap_z);
            }
        }

        mvc.forEachView(View::onModelChanged);
    }

    public LinkedList<FlickModel> getFlickers() {
        return this.flickers;
    }

    public FlickModel getDetailFlicker()  { return this.flick; }

    public void storeDetailFlicker(FlickModel flickModel) { this.flick = flickModel; }

    public void freeFlickers() { this.flickers.clear(); }

    public FlickModel getFlick(String photo_id) {
        for (FlickModel flick :  mvc.model.getFlickers()) {
            if (flick.getId().compareTo(photo_id) == 0) {
                return flick;
            }
        }

        return null;
    }

    public FlickModel getFlick(int pos) {
        if (this.flickers != null)
            return this.flickers.get(pos);
        return null;
    }
}
