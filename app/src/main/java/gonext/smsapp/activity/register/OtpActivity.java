package gonext.smsapp.activity.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import gonext.smsapp.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class OtpActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    private TextView tvSubmit;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        initViews();

        initOnClick();
    }

    private void initViews() {
        imgBack = findViewById(R.id.id_back);
        tvSubmit = findViewById(R.id.id_submit);
    }

    private void initOnClick() {
        imgBack.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_back:
                finish();
                break;
            case R.id.id_submit:
                Intent intent = new Intent(OtpActivity.this, PersonalDetailActivity.class);
                startActivity(intent);
                break;
        }
    }
}
