package love.flicli.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import love.flicli.R;

//TODO c'erano problemi con la classe Activity, la MenuBar non veniva visualizzata in nessun modo
public class MainActivity extends AppCompatActivity {
    // TODO
    public int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
