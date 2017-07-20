package love.flicli.view;
import android.app.ListFragment;
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
import java.util.LinkedList;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.Util;
import love.flicli.model.FlickModel;
import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * Created by tommaso on 29/05/17.
 */


public class ListViewFragment extends ListFragment implements AbstractFragment {

    private MVC mvc;
    private final static String TAG = ListViewFragment.class.getName();

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
                onClickRow(position);
                ((MainActivity) getActivity()).position = position;
            }
        });

        registerForContextMenu(this.getListView());
        onModelChanged();
    }

    @Override @UiThread
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_start, menu);
        MenuItem item = menu.findItem(R.id.menu_item);
        item.setVisible(false);
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

        //Botttone di condivisione
        if(item.getTitle()=="Condividi"){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            FlickModel model = mvc.model.getFlickers().get(info.position);
            new DownloadImage().execute(model.getUrl_z());

        //bottone che visualizza le ultime foto dell'autore
        } else if(item.getTitle()=="Ultime foto autore"){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            FlickModel model = mvc.model.getFlickers().get(info.position);
            mvc.controller.lastAuthorImage(getActivity().getApplication(), model.getOwner());

        } else {
            return false;
        }
        return true;
    }

    @Override @UiThread
    public void onModelChanged() {
        if (list == null) {
            list = new HistoryAdapter();
            setListAdapter(list);
        } else
            list.notifyDataSetChanged();
    }


    private class HistoryAdapter extends ArrayAdapter<FlickModel> {
        private final LinkedList<FlickModel> listFlick = mvc.model.getFlickers();

        private HistoryAdapter() {
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
            }
            return row;
        }
    }

    @UiThread
    private void onClickRow(int pos) {
        mvc.controller.getDetailFlicker(getActivity(), pos);
    }

    //implementato download dell'immagine in backGroud, con conseguente
    // chiamata della funzione che apre il
    @ThreadSafe
    class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //eseguito in backGround per scarica l'immagine una volta cliccato il bottone di
        //condivisione del context menu
        @WorkerThread @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap_z = null;
            try {
                String image = urls[0].replace("_z", "_h");
                bitmap_z = BitmapFactory.decodeStream((new URL(image)).openStream());
            } catch (IOException e) {
                return null;
            }
            return bitmap_z;
        }

        @UiThread
        protected void onPostExecute(Bitmap image) {
            startActivityListView(image);
        }
    }

    @UiThread
    //funzione che gestisce la condivisione con l'applicazione
    public void startActivityListView(Bitmap image) {

        if (tempFile != null)
            tempFile.delete();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        tempFile = Util.getImageUri(getActivity().getApplication(), image);
        Uri r = getUriForFile(getActivity().getApplication(), "love.flicli.fileprovider", tempFile);
        intent.putExtra(Intent.EXTRA_STREAM, r);
        startActivity(Intent.createChooser(intent, "..."));
    }
}