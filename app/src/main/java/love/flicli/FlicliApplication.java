package love.flicli;
import love.flicli.controller.Controller;
import love.flicli.model.Model;
import android.app.Application;

/**
 * Created by tommaso on 28/05/17.
 */

public class FlicliApplication extends Application {
    private MVC mvc;
    private FlickerAPI flickerAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        mvc = new MVC(new Model(), new Controller());
        flickerAPI = new FlickerAPI();
    }

    public MVC getMVC() {
        return mvc;
    }

    public FlickerAPI getFlickerAPI() {
        return flickerAPI;
    }
}