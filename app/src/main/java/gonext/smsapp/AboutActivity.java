package gonext.smsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
