package love.flicli.model;
import android.graphics.Bitmap;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import love.flicli.MVC;
import love.flicli.view.View;

import static android.os.Build.VERSION_CODES.M;
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

    public void cutFactorization(LinkedList<FlickModel> l) {
        synchronized (flickers) {
            LinkedList<FlickModel> temp = (LinkedList<FlickModel>) l.clone();
            mvc.model.freeFlickers();
            for (int i = 0; i < 50; i++) {
                flickers.add(temp.get(i));
            }
        }
    }

    public LinkedList<FlickModel> getLastAuthorImage() {
        LinkedList<FlickModel> f = new LinkedList<>();
        synchronized (flickers) {
            for (int i = 50; i < flickers.size(); i++) {
                f.add(flickers.get(i));
            }
        }
        return f;
    }

    public LinkedList<FlickModel> getLastAuthorImage10() {
        LinkedList<FlickModel> f = new LinkedList<>();
        synchronized (flickers) {
            for (int i = 0; i < 10; i++) {
                f.add(flickers.get(i));
            }
        }
        return f;
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
