package love.flicli.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.view.View;

import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;

/**
 * Created by tommaso on 03/06/17.
 */

public class ImageViewFragment extends Fragment implements AbstractFragment {
    private final static String TAG = ImageViewFragment.class.getName();
    private MVC mvc;
    WebView webView = null;

    @Override @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable @Override @UiThread
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_fragment, container, false);
        webView = (WebView) view.findViewById(R.id.imageContent);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);

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
        //FlickerService.Flick f = mvc.model.getLastFlick();
        //webView.loadUrl(f.getImgUrl());
        //webView.getSettings().setBuiltInZoomControls(true);
    }
}