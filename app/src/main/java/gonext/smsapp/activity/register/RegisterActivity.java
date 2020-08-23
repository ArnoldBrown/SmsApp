package gonext.smsapp.activity.register;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import gonext.smsapp.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvLogType;
    private LinearLayout llEmail, llMobile;
    private ImageView imgBack;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        initOnClick();
    }

    private void initViews() {
        tvLogType = findViewById(R.id.id_log_with);
        llEmail = findViewById(R.id.id_email_ll);
        llMobile = findViewById(R.id.id_mobile_ll);
        imgBack = findViewById(R.id.id_back);
    }

    private void initOnClick() {
        tvLogType.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_log_with:
                if(tvLogType.getText().toString().contentEquals("I would like to use email to register.")){
                    tvLogType.setText("I would like to use mobile number to register.");
                    llEmail.setVisibility(View.VISIBLE);
                    llMobile.setVisibility(View.GONE);
                }else{
                    tvLogType.setText("I would like to use email to register.");
                    llMobile.setVisibility(View.VISIBLE);
                    llEmail.setVisibility(View.GONE);
                }
                break;
            case R.id.id_back:
                finish();
                break;
        }
    }
}
