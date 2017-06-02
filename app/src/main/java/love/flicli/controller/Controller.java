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
        FlickerService.flicker(context, n);
        showHistory();
    }
    @UiThread
    public void recent(Context context) {
        //passiamo al servizio il contensto
        FlickerService.recent(context);
        showHistory();
    }

    @UiThread
    public void popular(Context context) {
        //passiamo al servizio il contensto
        FlickerService.popular(context);
        showHistory();
    }
    @UiThread
    public void comment(Context context, String image) {
        FlickerService.comment(context, image);
    }


    @UiThread
    public void getImageView(Context context, FlickerService.Flick image) {
        mvc.model.storeLastFlick(image);
        comment(context, image.getId());
        showImage();
    }

    public void showAuthorLastImage(Context context, String author) {
        FlickerService.lastAuthorImage(context, author);
        showHistory();
    }

    @UiThread
    public void showHistory() {
        mvc.forEachView(View::showHistory);
    }


    @UiThread
    public void showImage() { mvc.forEachView(View::showImage); }

}
