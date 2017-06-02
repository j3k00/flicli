package love.flicli.model;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;

import love.flicli.MVC;

/**
 * Created by tommaso on 18/05/17.
 */

@ThreadSafe
public class Model {
    private MVC mvc;
    public static final String API = "7d915cac1d6d251a1014bc8e00a9bf2e";
    // I really need that info?
    public static final String SECRET_KEY = "10fde0314d4d1aef";

    private MVC mvc;

    @GuardedBy("Itself")
    private FlickerService.Flick[] flickers;
    private FlickerService.Flick lastFlick;
    private FlickerService.Comments[] comments;

    private final static int MAX_FLICK = 50;

    public void setMVC(MVC mvc) {
        this.mvc = mvc;
    }

    public void storeFactorization(FlickerService.Flick[] f) {
        flickers = f.clone();
        mvc.forEachView(View::onModelChanged);
    }

    public void storeComments(FlickerService.Comments[] c) {
        comments = c.clone();
        mvc.forEachView(View::onModelChanged);
    }

    public void storeLastFlick(FlickerService.Flick flick) {
        lastFlick = flick;
    }

    public FlickerService.Comments[] getComments() {
        if (comments != null)
            return comments.clone();
        else
            return new FlickerService.Comments[0];
    }

    public FlickerService.Flick getLastFlick() { return lastFlick; }

    //return the list of the search Flick
    public FlickerService.Flick[] getFlickers() {
        if (flickers != null)
            return flickers.clone();
        else
            return new FlickerService.Flick[0];
    }
}
