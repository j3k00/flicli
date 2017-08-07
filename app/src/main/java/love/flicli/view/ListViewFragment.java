package love.flicli.view;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.jcip.annotations.ThreadSafe;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.Util;
import love.flicli.model.CommentModel;
import love.flicli.model.FlickModel;
import static android.support.v4.content.FileProvider.getUriForFile;
import static love.flicli.R.id.comments;

/**
 * Created by tommaso on 29/05/17.
 */


public class ListViewFragment extends ListFragment implements AbstractFragment {

    private MVC mvc;

    private HistoryAdapter list;
    private File tempFile;

    @Override @UiThread
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mvc = ((FlicliApplication) getActivity().getApplication()).getMVC();
        setHasOptionsMenu(true);

        //OnclickListener,  attacca alle liste della tabella la funzione che apre il dettaglio dell'immagine
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity) getActivity()).position = position;
                onClickRow(position);
            }
        });

        registerForContextMenu(this.getListView());
        onModelChanged();
    }

    @Override @UiThread
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_menu, menu);
    }

    @Override @UiThread
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.version) {
            mvc.controller.showVersion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Delete the temporary file for share image with other application
        if (tempFile != null)
            tempFile.delete();
    }

    //creazione del contextMenu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Condividi");
        menu.add(0, v.getId(), 0, "Ultime foto autore");
        registerForContextMenu(getListView());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //Botttone di condivisione
        if(item.getTitle() == "Condividi"){
            mvc.controller.getImageDetailFlicker(getActivity().getApplication(), info.position);

        //bottone che visualizza le ultime foto dell'autore
        } else if(item.getTitle() == "Ultime foto autore"){
            FlickModel model = mvc.model.getFlickers().get(info.position);
            ((MainActivity) getActivity()).position = info.position;
            mvc.controller.lastAuthorImage(getActivity().getApplication(), model.getOwner());

        }

        return false;
    }

    @Override @UiThread
    public void onModelChanged() {
        if (list == null) {
            list = new HistoryAdapter(getActivity().getApplication(),  mvc.model.getFlickers());
            setListAdapter(list);
        } else
            list.notifyDataSetChanged();
    }

    private class HistoryAdapter extends ArrayAdapter<FlickModel> {
        public HistoryAdapter(Context context, ArrayList<FlickModel> flikers) {
            super(getActivity(), R.layout.history_fragment, flikers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                row = getActivity().getLayoutInflater().inflate(R.layout.history_fragment, parent, false);
            }

            FlickModel flick = getItem(position);

            if (flick.getBitmap_url_s() == null) {
                ((ImageView) row.findViewById(R.id.icon)).setImageResource(R.drawable.image);
            } else
                ((ImageView) row.findViewById(R.id.icon)).setImageBitmap(flick.getBitmap_url_s());

            ((TextView) row.findViewById(R.id.description)).setText(flick.getTitle());
            ((TextView) row.findViewById(R.id.url)).setText(flick.getId());

            return row;
        }
    }

    @UiThread
    private void onClickRow(int pos) {
        mvc.controller.getDetailFlicker(getActivity(), pos);
    }
}