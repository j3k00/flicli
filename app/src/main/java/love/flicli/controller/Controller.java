package love.flicli.controller;

import android.content.Context;
import android.support.annotation.UiThread;

import love.flicli.MVC;

/**
 * Created by tommaso on 18/05/17.
 */

public class Controller {
    private final static String TAG = Controller.class.getName();
    private MVC mvc;

    public void setMVC(MVC mvc) {
        this.mvc = mvc;
    }

    @UiThread
    public void flicker(Context context, String n) {
        //passiamo all servizio la stringa della ricerca più il contensto
        ApiController.searchFlick(context, n);
        showHistory();
    }
    @UiThread
    public void recent(Context context) {
        //passiamo al servizio il contensto
        ApiController.getRecentFlick(context);
        showHistory();
    }

    @UiThread
    public void popular(Context context) {
        //passiamo al servizio il contensto
        ApiController.getPopularFlick(context);
        showHistory();
    }
    @UiThread
    public void comment(Context context, String image) {
        ApiController.getCommentFlick(context, image);
    }

    public void lastAuthorImage(Context context, String author) {
        ApiController.getFlickByAuthor(context, author);
        showHistory();
    }

    @UiThread
    public void showHistory() {
        mvc.forEachView(View::showHistory);
    }


    @UiThread
    public void showImage() { mvc.forEachView(View::showImage); }
    
}
