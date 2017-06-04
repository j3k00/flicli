package love.flicli.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.model.FlickModel;

/**
 * Created by tommaso on 03/06/17.
 */

public class DetailImageFragment extends Fragment implements AbstractFragment {
    private MVC mvc;
    ShareActionProvider mShareActionProvider = null;
    private FlickModel flickModel;


    public DetailImageFragment() {
        
    }
    @Override @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_image_fragment, container, false);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.view_fragment, new ImageViewFragment());
        ft.replace(R.id.comment_fragment, new CommentFragment());
        ft.addToBackStack(null);
        ft.commit();

        return view;
    }

    @Override @UiThread
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mvc = ((FlicliApplication) getActivity().getApplication()).getMVC();
        onModelChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share_menu, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        ShareActionProvider myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        //TODO SCARICARE FOTO

        if (myShareActionProvider!= null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            //shareIntent.putExtra(Intent.EXTRA_TEXT, mvc.model.getLastFlick().getImgUrl());
            shareIntent.setType("text/plain");
            myShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override @UiThread
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_item_author) {
            //showAuthorLastImage(mvc.model.getLastFlick().getUser_id());
        }
        return false;
    }


    @Override
    public void onModelChanged() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.view_fragment, new ImageViewFragment());
        ft.replace(R.id.comment_fragment, new CommentFragment());
        ft.addToBackStack(null);
        ft.commit();
    }


    public void showAuthorLastImage(String author) {
        //mvc.controller.showAuthorLastImage(getActivity(), author);
    }
}
