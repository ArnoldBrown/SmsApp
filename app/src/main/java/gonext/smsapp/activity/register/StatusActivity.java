package gonext.smsapp.activity.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import gonext.smsapp.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class StatusActivity extends AppCompatActivity {

    private TextView tvStart;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        initViews();

        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusActivity.this, QuestionActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initViews() {
        tvStart = findViewById(R.id.id_start);
    }

}
