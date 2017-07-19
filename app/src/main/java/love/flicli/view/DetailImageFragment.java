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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.File;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.Util;
import love.flicli.model.FlickModel;
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

    private Menu menu;
    private MVC mvc;
    private final static String TAG = DetailImageFragment.class.getName();
    private CommentFragment commentFragment;
    private ImageViewFragment imageViewFragment;
    private File tempFile = null;

    @Override
    @UiThread
    public void onStart() {
        super.onStart();
        commentFragment = (CommentFragment) getChildFragmentManager().findFragmentById(R.id.comment_fragment);
        imageViewFragment = (ImageViewFragment) getChildFragmentManager().findFragmentById(R.id.view_fragment);
    }

    @Override
    @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    @UiThread
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_image_fragment, container, false);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.view_fragment, new ImageViewFragment());
        ft.replace(R.id.comment_fragment, new CommentFragment());
        ft.addToBackStack(null);
        ft.commit();

        return view;
    }

    @Override
    @UiThread
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mvc = ((FlicliApplication) getActivity().getApplication()).getMVC();
        onModelChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share_menu, menu);
        this.menu = menu;

    }

    @Override
    @UiThread
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_author) {
            showAuthorLastImage(mvc.model.getDetailFlicker().getOwner());
        } else if (item.getItemId() == R.id.version) {
            mvc.controller.showVersion();
        } else if (item.getItemId() == R.id.menu_item_share) {
            if (mvc.model.getDetailFlicker().getBitmap_url_hd() != null)
                actionShare();
            else {
                Toast toast = Toast.makeText(getActivity().getApplication(), "Immage non pronta aspettare il caricamento", Toast.LENGTH_LONG);
                toast.show();
            }
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

    @Override
    @UiThread
    public void onModelChanged() {

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

    public void actionShare() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        tempFile = Util.getImageUri(getActivity().getApplication(), mvc.model.getDetailFlicker().getBitmap_url_hd());
        Uri r = getUriForFile(getActivity().getApplication(), "love.flicli.fileprovider", tempFile);
        intent.putExtra(Intent.EXTRA_STREAM, r);
        startActivity(Intent.createChooser(intent, "..."));
    }
}