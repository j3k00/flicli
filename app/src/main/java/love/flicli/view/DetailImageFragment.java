package love.flicli.view;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.appcompat.BuildConfig;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.model.FlickModel;

import static android.R.attr.bitmap;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.support.v4.content.FileProvider.getUriForFile;
import static java.io.File.createTempFile;

/**
 * Created by tommaso on 03/06/17.
 */

public class DetailImageFragment extends Fragment implements AbstractFragment {
/*
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
*/
    private MVC mvc;
    ShareActionProvider mShareActionProvider = null;
    private FlickModel flickModel;
    private final static String TAG = DetailImageFragment.class.getName();

    @Override @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //verifyStoragePermissions(getActivity());
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

            //File f = new File(Environment.getExternalStorageDirectory()+ File.separator + "temp.jpeg");
            //File dir = new File(Environment.getExternalStorageDirectory()+ File.separator + "images");
            //dir.mkdirs();
            //File f = new File(dir, "temp.jpeg");

            File imagePath = new File(getActivity().getApplication().getFilesDir(), "images");
            File newFile = new File(imagePath, "default_image.jpg");

            try {
                Bitmap bmp = mvc.model.getDetailFlicker().getBitmap_url_s();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                imagePath.mkdir();
                new FileOutputStream(newFile).write(stream.toByteArray());

            } catch (IOException e) {
                e.getMessage();
            }

            Uri contentUri = getUriForFile(getActivity().getApplication(), "love.flicli.fileprovider", newFile);

            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
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

/*
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
*/

    public void showAuthorLastImage(String author) {
        //mvc.controller.showAuthorLastImage(getActivity(), author);
    }
}
