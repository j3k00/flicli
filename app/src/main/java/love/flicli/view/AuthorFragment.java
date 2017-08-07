package love.flicli.view;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.jcip.annotations.ThreadSafe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;

import love.flicli.FlickerAPI;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.controller.FlickerService;
import love.flicli.model.AuthorModel;
import love.flicli.model.CommentModel;
import love.flicli.model.FlickModel;

import static android.content.ContentValues.TAG;
import static love.flicli.FlickerAPI.API_KEY;
import static love.flicli.R.drawable.user;

public class AuthorFragment extends Fragment implements AbstractFragment {
    private final static int SIZE = 270;
    private final static int PADDING = 8;

    private TextView authorName = null;
    private TextView informationAuthor = null;
    private GridView gridview = null;

    private MVC mvc = null;
    private ImageView author_image;
    private ImageAdapter imageAdapter = null;

    @Override @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override @UiThread
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.last_image_author_fragment, container, false);

        authorName = (TextView) view.findViewById(R.id.authorName);
        informationAuthor = (TextView) view.findViewById(R.id.informationAuthor);
        author_image = (ImageView) view.findViewById(R.id.image_author);
        gridview = (GridView) view.findViewById(R.id.gridview);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mvc = ((FlicliApplication) getActivity().getApplication()).getMVC();
        onModelChanged();
    }

    @Override
    public void onModelChanged() {
        if (mvc.model.getAuthorModel() != null) {
            authorName.setText(mvc.model.getAuthorModel().getName());
            informationAuthor.setText(mvc.model.getAuthorModel().getDescription());
            author_image.setImageBitmap(mvc.model.getAuthorModel().getBuddyIconBitmap());

            if (imageAdapter == null) {
                imageAdapter = new ImageAdapter(getActivity().getApplication());
                gridview.setAdapter(imageAdapter);
            } else  {
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mvc.model.getAuthorModel().getFlickers().size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(SIZE, SIZE));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(PADDING, PADDING, PADDING, PADDING);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageBitmap(mvc.model.getAuthorModel().getFlickers().get(position).getBitmap_url_s());
            return imageView;
        }
    }
}
