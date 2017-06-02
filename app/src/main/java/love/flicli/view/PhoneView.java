package love.flicli.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;

/**
 * Created by tommaso on 28/05/17.
 */

public class PhoneView extends FrameLayout implements View {
    private MVC mvc;

    private FragmentManager getFragmentManager() {
        return ((Activity) getContext()).getFragmentManager();
    }

    private AbstractFragment getFragment() {
        return (AbstractFragment) getFragmentManager().findFragmentById(R.id.phone_view);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mvc = ((FlicliApplication) getContext().getApplicationContext()).getMVC();
        mvc.register(this);

        // at the beginning, call FragmentManager and add to the phone_view StartFragment
        if (getFragment() == null)
            getFragmentManager().beginTransaction()
                    .add(R.id.phone_view, new StartFragment())
                    .commit();
    }

    @Override
    public void showImage() {
        /*
        getFragmentManager().beginTransaction()
                .replace(R.id.phone_view, new DoubleView())
                .addToBackStack(null)
                .commit();
         */
    }

    @Override
    protected void onDetachedFromWindow() {
        mvc.unregister(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void onModelChanged() {
        getFragment().onModelChanged();
    }

    @Override
    public void showHistory() {
        /*
        getFragmentManager().beginTransaction()
                .replace(R.id.phone_view, new ListViewFragment())
                .addToBackStack(null)
                .commit();
    */
    }

    /**
     * These two constructors must exist to let the view be recreated at
     * configuration change or inflated from XML.
     */

    public PhoneView(Context context) {
        super(context);
    }

    public PhoneView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}