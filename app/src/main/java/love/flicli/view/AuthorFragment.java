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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import love.flicli.model.AuthorModel;
import love.flicli.model.CommentModel;
import love.flicli.model.FlickModel;

import static android.content.ContentValues.TAG;
import static love.flicli.R.drawable.user;

/**
 * Created by tommaso on 23/07/17.
 */

public class AuthorFragment extends Fragment implements AbstractFragment {

    private TextView authorName = null;
    private TextView informationAuthor = null;
    private MVC mvc = null;
    private ListView list = null;
    private ImageView author_image;
    private String user = "";
    private int position = 0;
    private AuthorModel author = null;
    private Bitmap mIcon_val;

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
        list = (ListView) view.findViewById(R.id.listView);
        position = ((MainActivity) getActivity()).position;

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mvc = ((FlicliApplication) getActivity().getApplication()).getMVC();

        author = mvc.model.getAuthorModel();
        onModelChanged();
    }

    @Override
    public void onModelChanged() {
        authorName.setText(author.getRealname());

        try {
            mIcon_val = BitmapFactory.decodeStream(new URL(author.getBuddyIcon()).openConnection() .getInputStream());
            author_image.setImageBitmap(mIcon_val);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //listView
        list.setAdapter(new FlickAdapter(getActivity().getApplication(), author.getFlickers()));
    }

    private class FlickAdapter extends ArrayAdapter<FlickModel> {
        public FlickAdapter(Context context, ArrayList<FlickModel> flickers) {
            super(getActivity(), R.layout.layout_author, flickers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                row = getActivity().getLayoutInflater().inflate(R.layout.layout_author, parent, false);
            }

            FlickModel flicker = getItem(position);

            ((ImageView) row.findViewById(R.id.image3)).setImageBitmap(flicker.getBitmap_url_s());

            return row;
        }
    }
}
