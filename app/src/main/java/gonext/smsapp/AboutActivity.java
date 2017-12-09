package gonext.smsapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_about);
        TextView version = (TextView) findViewById(R.id.version);
        version.setText("Version: "+getString(R.string.version_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

}
