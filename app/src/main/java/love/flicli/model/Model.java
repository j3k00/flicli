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

    @GuardedBy("Itself")
    private final LinkedList<FlickModel> flickers = new LinkedList<>();

    public void setMVC(MVC mvc) {
        this.mvc = mvc;
    }

    public void storeFactorization(FlickModel flick) {
        synchronized (flickers) {
            for (FlickModel currentFlick : this.flickers) {
                if (currentFlick.getId() == flick.getId()) {
                    return;
                }
            }

            this.flickers.add(flick);
        }
        mvc.forEachView(View::onModelChanged);
    }

    public void storeDetail(String photo_id, int favs, ArrayList<Comment> comments, Bitmap bitmap_z ) {

        synchronized (flickers) {
            for (FlickModel flick : mvc.model.getFlickers()) {
                if (flick.getId().compareTo(photo_id) == 0) {
                    flick.setComments(comments);
                    flick.setFavourities(String.valueOf(favs));
                    flick.setBitmap_url_h(bitmap_z);
                }
            }
        }

        mvc.forEachView(View::onModelChanged);
    }

    public LinkedList<FlickModel> getFlickers() {
        synchronized (flickers) {
            return this.flickers;
        }
    }

    public void freeFlickers() {
        synchronized (flickers) {
        this.flickers.clear();
        }
    }
}
