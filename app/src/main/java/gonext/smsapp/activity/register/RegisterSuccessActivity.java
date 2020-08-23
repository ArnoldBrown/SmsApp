package gonext.smsapp.activity.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import gonext.smsapp.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class RegisterSuccessActivity extends AppCompatActivity {

    private Button btnDone;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);

        initViews();

       btnDone.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(RegisterSuccessActivity.this, StatusActivity.class);
               startActivity(intent);
           }
       });
    }

    private void initViews() {
        btnDone = findViewById(R.id.id_done);
    }


}
