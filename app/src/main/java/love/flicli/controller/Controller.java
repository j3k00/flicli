package love.flicli.controller;

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
}
