package love.flicli.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import love.flicli.R;

public class MainActivity extends AppCompatActivity {

    public int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
