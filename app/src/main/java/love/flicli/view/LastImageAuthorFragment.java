package love.flicli.view;

import android.app.Fragment;
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
import java.util.LinkedList;

import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.model.FlickModel;

import static android.content.ContentValues.TAG;
import static love.flicli.R.drawable.user;

/**
 * Created by tommaso on 23/07/17.
 */

public class LastImageAuthorFragment extends Fragment implements AbstractFragment {

    private TextView authorName = null;
    private TextView informationAuthor = null;
    private MVC mvc = null;
    private ListView list = null;
    private ImageView author_image;
    private String user = "";
    private int position = 0;

    @Override @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override @UiThread
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.last_image_author_fragmnet, container, false);
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
        new DownloadInformation().execute(mvc.model.getFlickers().get(position).getOwner());
        onModelChanged();
    }

    @Override
    public void onModelChanged() {
        author_image.setImageResource(R.drawable.user);

        //listView
        list.setAdapter(new FlickAdapter());
    }


    private class FlickAdapter extends ArrayAdapter<FlickModel> {
        private final LinkedList<FlickModel> flickers = mvc.model.getLastAuthorImage();

        //TODO trovare la soluzione, altrimenti stampa troppe foto
        private FlickAdapter() {
            super(getActivity(), R.layout.layout_author, mvc.model.getLastAuthorImage10());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.layout_author, parent, false);
            }

            if (flickers!= null) {
                if ((position*5) < flickers.size()) {
                    ((ImageView) row.findViewById(R.id.image1)).setImageBitmap(flickers.get(position * 5).getBitmap_url_s());

                    if ((position * 5) + 1 < flickers.size())
                        ((ImageView) row.findViewById(R.id.image2)).setImageBitmap(flickers.get((position * 5) + 1).getBitmap_url_s());

                    if ((position * 5) + 2 < flickers.size())
                        ((ImageView) row.findViewById(R.id.image3)).setImageBitmap(flickers.get((position * 5) + 2).getBitmap_url_s());

                    if ((position * 5) + 3 < flickers.size())
                        ((ImageView) row.findViewById(R.id.image4)).setImageBitmap(flickers.get((position * 5) + 3).getBitmap_url_s());

                    if ((position * 5) + 4 < flickers.size())
                        ((ImageView) row.findViewById(R.id.image5)).setImageBitmap(flickers.get((position * 5) + 4).getBitmap_url_s());
                }
            }
            return row;
        }
    }

    @ThreadSafe
    class DownloadInformation extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @WorkerThread @Override
        protected JSONObject doInBackground(String... urls) {
            String SERVER = "https://api.flickr.com/services/rest/?method=flickr.people.getInfo&api_key=c3474bf29107156411982633c3e020ab&user_id=" + urls[0] +"&format=json&nojsoncallback=1";
            String response = "";
            String line = "";
            BufferedReader in = null;
            JSONObject json = null;

            try {
                URL url = new URL(SERVER);
                URLConnection conn = url.openConnection();
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((line = in.readLine()) != null) {
                    Log.d(TAG, "STARTING SEARCH OF" + line);
                    response += line + "\n";
                }

                in.close();
            }catch (MalformedURLException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                json = new JSONObject(response);
            } catch (JSONException e) {}

            return json;
        }

        @UiThread
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                JSONObject name = jsonObject.getJSONObject("person");
                name = name.getJSONObject("username");
                authorName.setText(name.getString("_content"));

                name = jsonObject.getJSONObject("person");
                name = name.getJSONObject("location");
                informationAuthor.setText(name.getString("_content"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
