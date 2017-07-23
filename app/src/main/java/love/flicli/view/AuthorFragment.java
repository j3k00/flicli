package love.flicli.view;
import android.view.View;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import love.flicli.R;

/**
 * Created by tommaso on 13/07/17.
 */

public class AuthorFragment extends Fragment implements AbstractFragment {


    private TextView date = null;

    @Override @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override @UiThread
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.author_fragment, container, false);
        date = (TextView) view.findViewById(R.id.dateApplication);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date currentDate = new Date();
        date.setText(dateFormat.format(currentDate));

        return view;
    }

    @Override
    public void onModelChanged() {

    }
}
