package love.flicli.view;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.model.Comment;
import love.flicli.model.FlickModel;

import static android.R.attr.value;

/**
 * Created by tommaso on 03/06/17.
 */

public class ImageViewFragment extends Fragment implements AbstractFragment {
    private final static String TAG = ImageViewFragment.class.getName();
    private MVC mvc;
    ImageView imageView = null;
    TextView viewTextView = null;
    TextView commentTextView = null;
    TextView favTextView = null;
    ProgressBar progress = null;
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
        viewTextView = (TextView) view.findViewById(R.id.views);
        commentTextView = (TextView) view.findViewById(R.id.comments);
        favTextView = (TextView) view.findViewById(R.id.favourite);
        progress = (ProgressBar) view.findViewById(R.id.indeterminateBar);
        progress.setVisibility(View.VISIBLE);
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

        //Hide progress bar
        if (flickModel.getBitmap_url_hd() != null)
            progress.setVisibility(View.INVISIBLE);

        imageView.setImageBitmap(flickModel.getBitmap_url_hd());

        viewTextView.setText(mvc.model.getDetailFlicker().getViews());

        commentTextView.setText(String.valueOf(mvc.model.getDetailFlicker().getComments().size()));
        favTextView.setText(flickModel.getFavourities());
    }
}