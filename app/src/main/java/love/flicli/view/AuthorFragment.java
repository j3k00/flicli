package love.flicli.view;
import android.view.View;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import love.flicli.R;

/**
 * Created by tommaso on 13/07/17.
 */

public class AuthorFragment extends Fragment implements AbstractFragment {


    @Override @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override @UiThread
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.author_fragment, container, false);

        return view;
    }

    @Override
    public void onModelChanged() {

    }
}
