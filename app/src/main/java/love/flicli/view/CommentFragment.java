package love.flicli.view;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;

/**
 * Created by tommaso on 03/06/17.
 */

public class CommentFragment extends ListFragment implements AbstractFragment  {
    private MVC mvc;

    @Override @UiThread
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mvc = ((FlicliApplication) getActivity().getApplication()).getMVC();
        onModelChanged();
    }

    @Override @UiThread
    public void onModelChanged() {
        /*setListAdapter(new CommentAdapter());*/
    }
/*
    private class CommentAdapter extends ArrayAdapter<FlickerService.Comments> {
        private final FlickerService.Comments[] messages = mvc.model.getComments();

        private CommentAdapter() {
            super(getActivity(), R.layout.comment_fragment, mvc.model.getComments());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.comment_fragment, parent, false);
            }

            if (messages != null) {
                FlickerService.Comments message = messages[position];
                String date = getCalendarDate(message.getDate());

                ((TextView) row.findViewById(R.id.author)).setText(message.getAuthor());

                //create date test
                String util = "\t";
                util = util.concat(date);

                ((TextView) row.findViewById(R.id.date)).setText(util);
                ((TextView) row.findViewById(R.id.message)).setText(message.getComments());
            }

            return row;
        }
    }

    private String getCalendarDate(Long date) {
        java.util.Date d = new java.util.Date(date*1000L);
        String itemDateStr = new SimpleDateFormat("dd-MMM HH:mm").format(d);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        Calendar currentDate = Calendar.getInstance();

        if (date == 0)
            return "";
        else if (cal.get(Calendar.YEAR) < currentDate.get(Calendar.YEAR)) {
            //ANNO CORRENTE > ANNO DEL COMMENTO

            String diff = currentDate.get(Calendar.YEAR) - cal.get(Calendar.YEAR) + "";
            if ((currentDate.get(Calendar.YEAR) - cal.get(Calendar.YEAR)) > 1)
                diff = diff.concat(" years ago");
            else
                diff = diff.concat(" year ago");
            return diff;
        } else if(cal.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)) {
            //STESSO ANNO DELL'ANNO CORRENTE E STESSO MESE

            if (currentDate.get(Calendar.DAY_OF_MONTH) > cal.get(Calendar.DAY_OF_MONTH)){
                // SE IL MESE CORRENTE E' UGUALE CONFRONTO I GIORNI

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
                if (currentDate.get(Calendar.HOUR) > cal.get(Calendar.HOUR)) {
                    int diff = currentDate.get(Calendar.HOUR) - cal.get(Calendar.HOUR);
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
    */
}
