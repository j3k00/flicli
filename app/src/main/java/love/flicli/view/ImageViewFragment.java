package love.flicli.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.view.View;

import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.model.FlickModel;

import static android.R.attr.data;
import static android.R.attr.x;

/**
 * Created by tommaso on 03/06/17.
 */

public class ImageViewFragment extends Fragment implements AbstractFragment {
    private final static String TAG = ImageViewFragment.class.getName();
    private MVC mvc;
    WebView webView = null;
    private FlickModel flickModel;

    @Override @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable @Override @UiThread
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_fragment, container, false);
        webView = (WebView) view.findViewById(R.id.imageContent);

        return view;
    }

    @Override @UiThread
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mvc = ((FlicliApplication) getActivity().getApplication()).getMVC();
        onModelChanged();
    }

    @Override
    public void onModelChanged() {
        flickModel = mvc.model.getDetailFlicker();
        webView.loadUrl(flickModel.getImageUrl().replace("_z", "_h"));
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);    }
}