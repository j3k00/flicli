package love.flicli.view;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.model.Comment;
import love.flicli.model.FlickModel;

/**
 * Created by tommaso on 03/06/17.
 */

public class ImageViewFragment extends Fragment implements AbstractFragment {
    private final static String TAG = ImageViewFragment.class.getName();
    private MVC mvc;
    ImageView imageView = null;
    TextView views = null;
    TextView comments = null;
    TextView fav = null;
    private FlickModel flickModel;

    @Override @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable @Override @UiThread
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_fragment, container, false);
        imageView = (ImageView) view.findViewById(R.id.imageContent);
        views = (TextView) view.findViewById(R.id.views);
        comments = (TextView) view.findViewById(R.id.comments);
        fav = (TextView) view.findViewById(R.id.favourite);
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
        flickModel = mvc.model.getDetailFlicker();

        imageView.setImageBitmap(flickModel.getBitmap_url_hd());
        views.setText(mvc.model.getDetailFlicker().getViews());

        ArrayList<Comment> comment = mvc.model.getDetailFlicker().getComments();
        String value = "";
        if (comment.size() == 1) {
            Comment comment1 = comment.get(0);
            if (comment1.get_content().compareTo("No Comments") == 0)
                value = "0";
            else
                value = "1";
        } else
            value = String.valueOf(mvc.model.getDetailFlicker().getComments().size());

        comments.setText(value);
        fav.setText((flickModel.getFavourities() == null) ? "0" : flickModel.getFavourities());
    }
}