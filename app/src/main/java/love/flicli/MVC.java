package love.flicli;

import android.os.Handler;
import android.os.Looper;

import net.jcip.annotations.ThreadSafe;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import love.flicli.controller.Controller;
import love.flicli.model.Model;
import love.flicli.view.View;

/**
 * Created by tommaso on 18/05/17.
 */

@ThreadSafe
public class MVC {
    public final Model model;
    public final Controller controller;
    private final List<View> views = new CopyOnWriteArrayList<>();

    public MVC(Model model, Controller controller) {
        this.model = model;
        this.controller = controller;

        model.setMVC(this);
        controller.setMVC(this);
    }

    //register the view on our List views
    public void register(View view) {
        views.add(view);
    }

    //unregister the view on our List views
    public void unregister(View view) {
        views.remove(view);
    }

    //Send in process the current view
    public interface ViewTask {
        void process(View view);
    }

    public void forEachView(ViewTask task) {
        // run a Runnable in the UI thread
        new Handler(Looper.getMainLooper()).post(() -> {
            for (View view : views)
                task.process(view);
        });
    }


}
