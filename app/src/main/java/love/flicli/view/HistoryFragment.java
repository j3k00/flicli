package love.flicli.view;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.model.Flick;

import static android.content.ContentValues.TAG;

/**
 * Created by tommaso on 29/05/17.
 */

public class HistoryFragment extends ListFragment implements AbstractFragment {
    private MVC mvc;

    @Override @UiThread

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mvc = ((FlicliApplication) getActivity().getApplication()).getMVC();
        onModelChanged();
    }

    //TODO fix contextMenu, funziona solo cliccando una determinata parte della View
    //quando invece dovrebbe funzionare indistintamente su tutta la riga
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Action 1");
        menu.add(0, v.getId(), 0, "Action 2");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()=="Action 1"){
        } else if(item.getTitle()=="Action 2"){
        } else {
            return false;
        }
        return true;
    }

    @Override @UiThread
    public void onModelChanged() {
        setListAdapter(new HistoryAdapter());
    }

    private class HistoryAdapter extends ArrayAdapter<Flick> {
        private final Flick[] listFlick = null;//mvc.model.getFlickers();

        private HistoryAdapter() {
            /*mvc.model.getFlickers()*/
            super(getActivity(), R.layout.history_fragment, new Flick[10]);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.history_fragment, parent, false);
            }

            if (listFlick != null) {
                Flick flick = listFlick[position];

                /*
                ((WebView) row.findViewById(R.id.icon)).loadUrl(flick.getThumbNail());
                Log.d(TAG, "flick.getThumbNail()");
                ((TextView) row.findViewById(R.id.description)).setText(flick.getTitle());
                ((TextView) row.findViewById(R.id.url)).setText(flick.getImgUrl());
                row.setOnClickListener(__ -> onClickRow(flick));


                row.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        registerForContextMenu(v);
                        Toast.makeText(getActivity(), "LONG PRESS", Toast.LENGTH_LONG).show();
                        //previene l'handle del Click
                        return true;
                    }
                });
                */
            }
            return row;
        }
    }

    private void onClickRow(Flick image) {
        //mvc.controller.getImageView(getActivity(), image);
    }
}