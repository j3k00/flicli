package love.flicli.controller;

import android.content.Context;
import android.support.annotation.UiThread;

import love.flicli.MVC;
import love.flicli.view.View;

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
        FlickerService.searchFlick(context, n);
        showHistory();
    }

    @UiThread
    public void getDetailFlicker(Context context, int pos) {
        FlickerService.getDetailFlick(context, pos);
        showDetail();
    }

    public void getImageDetailFlicker(Context context, int pos) {
        FlickerService.getImageDetailFLick(context, pos);
    }

   @UiThread
    public void recent(Context context) {
        //passiamo al servizio il contensto
        FlickerService.getRecentFlick(context);
        showHistory();
    }

    @UiThread
    public void popular(Context context) {
        //passiamo al servizio il contensto
        FlickerService.getPopularFlick(context);
        showHistory();
    }

    public void lastAuthorImage(Context context, String author) {
        FlickerService.getFlickByAuthor(context, author);

        showLastImageAuthorFragment();
    }

    @UiThread
    public void showVersion() {
        version();
    }

    @UiThread
    public void showDetail() {
        mvc.forEachView(View::showDetail);
    }

    @UiThread
    public void showHistory() {
        mvc.forEachView(View::showHistory);
    }

    @UiThread
    public void showLastImageAuthorFragment() {
        mvc.forEachView(View::showLastImageAuthor);
    }

    @UiThread
    public void version() {
        mvc.forEachView(View::showAuthor);
    }

}
