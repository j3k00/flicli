package love.flicli.view;

import android.support.annotation.UiThread;

/**
 * Created by tommaso on 28/05/17.
 */

public interface AbstractFragment {

    @UiThread
    void onModelChanged();
}
