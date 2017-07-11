package love.flicli.view;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.view.View;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.model.Comment;

/**
 * Created by tommaso on 03/06/17.
 */

public class CommentFragment extends ListFragment implements AbstractFragment  {
    private final static String TAG = CommentFragment.class.getName();
    private MVC mvc;

    @Override @UiThread
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mvc = ((FlicliApplication) getActivity().getApplication()).getMVC();
        onModelChanged();
    }

    @Override @UiThread
    public void onModelChanged() {
        setListAdapter(new CommentAdapter());
    }

    private class CommentAdapter extends ArrayAdapter<Comment> {
        private final ArrayList<Comment> messages = mvc.model.getDetailFlicker().getComments();

        private CommentAdapter() {
            super(getActivity(), R.layout.comment_fragment, mvc.model.getDetailFlicker().getComments());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.comment_fragment, parent, false);
            }

            if (messages != null) {
                Comment message = messages.get(position);
                String date = "";
                String text = "";
                String author = "";

                if (message.get_content().compareTo("No Comments") == 0) {
                    date = getCalendarDate((long) 0);
                    author = "Nessun Commento";
                    text = "Nessun Commento";
                } else {
                    date = getCalendarDate(Long.parseLong(message.getDatecreate()));
                    author = message.getAuthorname();
                    text = message.get_content();
                }

                ((TextView) row.findViewById(R.id.author)).setText(author);

                //create date test
                String util = "\t";
                util = util.concat(date);

                ((TextView) row.findViewById(R.id.date)).setText(util);
                ((TextView) row.findViewById(R.id.message)).setText(text);
            }
            return row;
        }
    }

    @UiThread
    private String getCalendarDate(Long date) {
        java.util.Date d = new java.util.Date(date*1000L);
        String itemDateStr = new SimpleDateFormat("dd-MMM HH:mm").format(d);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        long time= System.currentTimeMillis();

        Calendar currentDate = Calendar.getInstance();
        d = new java.util.Date(time);
        currentDate.setTime(d);

        if (date == 0)
            return "";
        else if (cal.get(Calendar.YEAR) < currentDate.get(Calendar.YEAR)) {
            //ANNO DEL COMMENTO < DELL'ANNO CORRENTE

            String diff = currentDate.get(Calendar.YEAR) - cal.get(Calendar.YEAR) + "";
            if ((currentDate.get(Calendar.YEAR) - cal.get(Calendar.YEAR)) > 1)
                diff = diff.concat(" years ago");
            else
                diff = diff.concat(" year ago");
            return diff;
        } else if(cal.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)) {
            //STESSO ANNO DELL'ANNO CORRENTE E STESSO MESE

            if (currentDate.get(Calendar.DAY_OF_MONTH) > cal.get(Calendar.DAY_OF_MONTH)){
                // SE IL MESE CORRENTE E' UGUALE CONFRONTO I GIORNI, SE IL GIORNO CORRENTE E MAGGIORE DI QUELLO
                //DEL COMMENTO ALLORA RITORNO LA DIFFERENZA DEI GIORNI

                int diff = currentDate.get(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_MONTH);
                String difference = diff + "";

                if (diff == 1)
                    difference = difference.concat(" day ago");
                else
                    difference = difference.concat(" days ago");

                return difference;
            } else {
                //STESSO GIORNO DEL GIORNO CORRENTE

                //CONFRONTO L'ORA DI CARICAMENTO DEL COMMENTO
                if (currentDate.get(Calendar.HOUR_OF_DAY) > cal.get(Calendar.HOUR_OF_DAY)) {
                    int diff = currentDate.get(Calendar.HOUR_OF_DAY) - cal.get(Calendar.HOUR_OF_DAY);
                    String difference = diff + "";

                    if (diff == 1)
                        difference = difference.concat(" hour ago");
                    else
                        difference = difference.concat(" hours ago");

                    return difference;
                } else {
                    //STESSA ORA RITORNO LA DIFFERENZA DEI MINUTI
                    int diff = currentDate.get(Calendar.MINUTE) - cal.get(Calendar.MINUTE);
                    String difference = diff + "";
                    difference = difference.concat(" minutes ago");
                    return difference;
                }
            }
        } else {
            //SE I MESI SONO DIVERSI, RITORNO LA DIFFERENZA DI MESI
            int diff = currentDate.get(Calendar.MONTH) - cal.get(Calendar.MONTH);
            String difference = diff + "";
            if (diff > 1)
                difference = difference.concat(" months ago");
            else
                difference = difference.concat(" month ago");
            return difference;
        }
    }
}
