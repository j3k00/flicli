package love.flicli.view;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;

/**
 * Created by tommaso on 29/05/17.+
 * Managde tablet screen, split in two fragment the device
 */

public class TabletView extends LinearLayout implements View {
    private MVC mvc;

    private FragmentManager getFragmentManager() {
        return ((Activity) getContext()).getFragmentManager();
    }

    private AbstractFragment getSearchFragment() {
        return (AbstractFragment) getFragmentManager().findFragmentById(R.id.search_fragment);
    }

    private AbstractFragment getChangeFragment() {
        return (AbstractFragment) getFragmentManager().findFragmentById(R.id.changedFragment);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mvc = ((FlicliApplication) getContext().getApplicationContext()).getMVC();
        mvc.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        mvc.unregister(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void onModelChanged() {
        getSearchFragment().onModelChanged();
        getChangeFragment().onModelChanged();
    }

    @Override
    public void showHistory() {
        getFragmentManager().beginTransaction()
                .replace(R.id.changedFragment, new ListViewFragment())
                .addToBackStack(null)
                .commit();
    }

    public void showImage() {
        getFragmentManager().beginTransaction()
                .replace(R.id.changedFragment, new DetailImageFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showAuthor() {
        getFragmentManager().beginTransaction()
                .replace(R.id.changedFragment, new AuthorFragment())
                .addToBackStack(null)
                .commit();
    }

    public TabletView(Context context) {
        super(context);
    }

    public TabletView(Context context, AttributeSet attrs) { super(context, attrs); }
}