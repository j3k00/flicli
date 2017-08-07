package love.flicli.view;

import android.support.annotation.UiThread;


public interface AbstractFragment {

    @UiThread
    void onModelChanged();
}
