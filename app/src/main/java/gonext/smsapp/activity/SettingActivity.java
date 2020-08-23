package gonext.smsapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import gonext.smsapp.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imgBack;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initViews();

        initOnClick();
    }

    private void initViews() {
        imgBack = findViewById(R.id.id_back);
    }

    private void initOnClick() {
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_back:
                finish();
                break;
        }
    }
}
