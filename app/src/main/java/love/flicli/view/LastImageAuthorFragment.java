package love.flicli.view;

import android.app.Fragment;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.model.Comment;
import love.flicli.model.FlickModel;

import static android.media.CamcorderProfile.get;

/**
 * Created by tommaso on 23/07/17.
 */

public class LastImageAuthorFragment extends Fragment implements AbstractFragment {

    private TextView authorName = null;
    private TextView informationAuthor = null;
    private MVC mvc = null;
    private ListView list = null;
    private ImageView author_image;

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
        author_image.setImageResource(R.drawable.user);

        //Nome dell'autore
        //authorName

        //luogo dell'autore
        //informationAuthor

        //listView
        list.setAdapter(new FlickAdapter());
    }


    private class FlickAdapter extends ArrayAdapter<FlickModel> {
        private final LinkedList<FlickModel> flickers = mvc.model.getLastAuthorImage();

        private FlickAdapter() {
            super(getActivity(), R.layout.layout_author, mvc.model.getLastAuthorImage());
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

}
