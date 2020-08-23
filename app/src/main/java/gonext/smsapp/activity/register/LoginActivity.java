package gonext.smsapp.activity.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import gonext.smsapp.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvLogWith, tvForgetPass, tvRegister, tvSignIn;
    private LinearLayout llMobile, llEmail;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        initOnClick();
    }

    private void initViews() {
        tvLogWith = findViewById(R.id.id_log_with);
        tvForgetPass = findViewById(R.id.id_forget_password);
        tvRegister = findViewById(R.id.id_register);
        tvSignIn = findViewById(R.id.id_signin);
        llMobile = findViewById(R.id.id_mobile_ll);
        llEmail = findViewById(R.id.id_email_ll);
    }

    private void initOnClick() {
        tvLogWith.setOnClickListener(this);
        tvForgetPass.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_log_with:
                if(tvLogWith.getText().toString().contentEquals("Login with email")){
                    tvLogWith.setText("Login with mobile number");
                    llEmail.setVisibility(View.VISIBLE);
                    llMobile.setVisibility(View.GONE);
                }else{
                    tvLogWith.setText("Login with email");
                    llMobile.setVisibility(View.VISIBLE);
                    llEmail.setVisibility(View.GONE);
                }
                break;
            case R.id.id_forget_password:
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.id_register:
                Intent intent2 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent2);
                break;
            case R.id.id_signin:
                Intent intent3 = new Intent(LoginActivity.this, OtpActivity.class);
                startActivity(intent3);
                break;

        }
    }
}
