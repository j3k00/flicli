package love.flicli.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.model.FlickModel;

import static android.R.attr.bitmap;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.support.v4.content.FileProvider.getUriForFile;
import static java.io.File.createTempFile;

/**
 * Created by tommaso on 03/06/17.
 */

public class DetailImageFragment extends Fragment implements AbstractFragment {
    private MVC mvc;
    ShareActionProvider mShareActionProvider = null;
    private FlickModel flickModel;
    private final static String TAG = DetailImageFragment.class.getName();


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
            shareIntent.setType("image/jpeg");

            PackageManager pm = ((FlicliApplication) getActivity().getApplication()).getPackageManager();

            try {
                Bitmap bmp = mvc.model.getDetailFlicker().getBitmap_url_s();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                String path = MediaStore.Images.Media.insertImage(getActivity().getApplication().getContentResolver(), bmp, "Title", null);
                Uri imageUri = Uri.parse(path);

                @SuppressWarnings("unused")
                PackageInfo info = pm.getPackageInfo("love.flicli", PackageManager.GET_META_DATA);
                shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, imageUri);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


            //shareIntent.putExtra(Intent.EXTRA_STREAM, f.getPath());
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
