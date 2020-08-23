package gonext.smsapp.activity.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import gonext.smsapp.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PersonalDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout llSecOne, llSecTwo, llSecThree, llSecOneTwo;
    private TextView tvStepLabel, tvSubmit;
    private ImageView imgBack;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        initViews();

        initOnClick();

    }

    private void initViews() {
        llSecOne = findViewById(R.id.id_sec_one);
        llSecTwo = findViewById(R.id.id_sec_two);
        llSecThree = findViewById(R.id.id_sec_three);
        llSecOneTwo = findViewById(R.id.id_sec_onetwo);
        tvStepLabel = findViewById(R.id.id_step_label);
        tvSubmit = findViewById(R.id.id_submit);
        imgBack = findViewById(R.id.id_back);
    }

    private void initOnClick() {
        tvSubmit.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_submit:
                if(llSecOne.getVisibility()==View.VISIBLE){
                    tvStepLabel.setText("Step 2 of 3");
                    llSecOne.setVisibility(View.GONE);
                    llSecTwo.setVisibility(View.VISIBLE);
                    imgBack.setVisibility(View.VISIBLE);
                }else if(llSecTwo.getVisibility()==View.VISIBLE){
                    tvStepLabel.setText("Step 3 of 3");
                    llSecTwo.setVisibility(View.GONE);
                    llSecThree.setVisibility(View.VISIBLE);
                    imgBack.setVisibility(View.VISIBLE);
                    llSecOneTwo.setVisibility(View.GONE);
                }else{
                    Intent intent = new Intent(PersonalDetailActivity.this, RegisterSuccessActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.id_back:
                if(llSecTwo.getVisibility()==View.VISIBLE){
                    tvStepLabel.setText("Step 1 of 3");
                    imgBack.setVisibility(View.INVISIBLE);
                    llSecTwo.setVisibility(View.GONE);
                    llSecOne.setVisibility(View.VISIBLE);
                    llSecOneTwo.setVisibility(View.VISIBLE);
                }else{
                    tvStepLabel.setText("Step 2 of 3");
                    llSecThree.setVisibility(View.GONE);
                    llSecTwo.setVisibility(View.VISIBLE);
                    llSecOneTwo.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
