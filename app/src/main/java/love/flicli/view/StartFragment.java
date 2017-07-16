package love.flicli.view;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import java.lang.reflect.MalformedParameterizedTypeException;

import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;

/**
 * Created by tommaso on 28/05/17.
 */

public class StartFragment extends Fragment implements AbstractFragment {
    private final static String TAG = StartFragment.class.getName();
    private MVC mvc;
    private EditText text;
    private TextView errorText;
    private Button sendButton;
    private Button recentButton;
    private Button popularButton;
    private TextView noConnection;

    @Override @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable @Override @UiThread
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_fragment, container, false);
        text = (EditText) view.findViewById(R.id.searchText);
        errorText = (TextView) view.findViewById(R.id.errorText);
        sendButton = (Button) view.findViewById(R.id.sendButton);
        popularButton = (Button) view.findViewById(R.id.populImage);
        recentButton = (Button) view.findViewById(R.id.recentImage);
        noConnection = (TextView) view.findViewById(R.id.noConnection);

        //controlla se la connessione è presente, altrimenti disabilito le oprezioni
        if (isConnected(getActivity().getApplication())) {
            sendButton.setOnClickListener(__ -> flicker(text.getText().toString()));
            popularButton.setOnClickListener(__ -> flickerPopularImage());
            recentButton.setOnClickListener(__ -> flickerRecentImage());
            noConnection.setText("");
        } else {
            noConnection.setText("Connessione non disponibile");
        }
        return view;
    }

    @Override @UiThread
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mvc = ((FlicliApplication) getActivity().getApplication()).getMVC();
        onModelChanged();
    }


    @Override @UiThread
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_start, menu);
    }

    @Override @UiThread
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item) {
            mvc.controller.showHistory();
            return true;
        } else if (item.getItemId() == R.id.version) {
            mvc.controller.showVersion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override @UiThread
    public void onModelChanged() {

    }

    @UiThread private void flicker(String s) {
        try {
            if (s.compareTo("") == 0)
                throw new MalformedParameterizedTypeException();
                mvc.controller.flicker(getActivity(), s);
        }
        catch (MalformedParameterizedTypeException e) {
            errorText.setText("Inserisci una stringa su cui fare ricerca");
        }
    }

    @UiThread private void flickerPopularImage() { mvc.controller.popular(getActivity()); }

    @UiThread private  void flickerRecentImage() {
        mvc.controller.recent(getActivity());
    }


    public static boolean isConnected(Context context){


        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo connection = manager.getActiveNetworkInfo();
        if (connection != null && connection.isConnectedOrConnecting()){
            return true;
        }
        return false;
    }
}

