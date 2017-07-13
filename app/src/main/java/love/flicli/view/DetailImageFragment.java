package love.flicli.view;
import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.Util;
import love.flicli.model.FlickModel;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * Created by tommaso on 03/06/17.
 */

public class DetailImageFragment extends Fragment implements AbstractFragment {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private MVC mvc;
    ShareActionProvider mShareActionProvider = null;
    private FlickModel flickModel;
    private final static String TAG = DetailImageFragment.class.getName();
    private CommentFragment commentFragment;
    private ImageViewFragment imageViewFragment;
    private File tempFile = null;

    @Override @UiThread
    public void onStart() {
        super.onStart();
        commentFragment = (CommentFragment) getChildFragmentManager().findFragmentById(R.id.comment_fragment);
        imageViewFragment = (ImageViewFragment) getChildFragmentManager().findFragmentById(R.id.view_fragment);
    }

    @Override @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        verifyStoragePermissions(getActivity());
    }

    @Override @UiThread
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

        if (myShareActionProvider!= null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            tempFile = Util.getImageUri(getActivity().getApplication(), mvc.model.getDetailFlicker().getBitmap_url_hd());
            Uri contentUri = getUriForFile(getActivity().getApplication(), "love.flicli.fileprovider", tempFile);

            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
            myShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @UiThread
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override @UiThread
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_author) {
            showAuthorLastImage(mvc.model.getDetailFlicker().getOwner());
        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Delete the temporary file for share image with other application
        if (tempFile != null)
            tempFile.delete();
    }

    @Override @UiThread
    public void onModelChanged() {

        //Ho dovuto implementare in questa maniere perchè il getChildFragment siccome mActivity è null ritornava sempre
        //null nel richiamare i frammenti figli con il metodo findby....

        //codice vecchio, riscriveva sempre i due frammenti da capo
        /*FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.view_fragment, new ImageViewFragment());
        ft.replace(R.id.comment_fragment, new CommentFragment());
        ft.addToBackStack(null);
        ft.commit();*/

        if (commentFragment != null && imageViewFragment != null) {
            commentFragment.onModelChanged();
            imageViewFragment.onModelChanged();
        }
    }

    @UiThread
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

    @UiThread
    public void showAuthorLastImage(String author) {
        mvc.controller.lastAuthorImage(getActivity(), author);
    }
}
