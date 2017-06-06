package love.flicli.view;

import android.app.ListFragment;
import android.content.res.Resources;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.controller.ApiController;
import love.flicli.model.FlickModel;

/**
 * Created by tommaso on 29/05/17.
 */


public class ListViewFragment extends ListFragment implements AbstractFragment {
    private MVC mvc;
    private final static String TAG = ListViewFragment.class.getName();
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

    private class HistoryAdapter extends ArrayAdapter<FlickModel> {
        private final LinkedList<FlickModel> listFlick = mvc.model.getFlickers();

        private HistoryAdapter() {
            /*mvc.model.getFlickers()*/
            super(getActivity(), R.layout.history_fragment, mvc.model.getFlickers());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.history_fragment, parent, false);
            }

            if (listFlick != null) {
                FlickModel flick = listFlick.get(position);

                if (flick.getBitmap_url_s() == null) {
                    ((ImageView) row.findViewById(R.id.icon)).setImageResource(R.drawable.image);
                } else
                    ((ImageView) row.findViewById(R.id.icon)).setImageBitmap(flick.getBitmap_url_s());

                ((TextView) row.findViewById(R.id.description)).setText(flick.getTitle());
                ((TextView) row.findViewById(R.id.url)).setText(flick.getId());

                row.setOnClickListener(__ -> onClickRow(flick));

                /*
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

    private void onClickRow(FlickModel image) {
        mvc.controller.getImageDetail(getActivity(), image);
    }
}