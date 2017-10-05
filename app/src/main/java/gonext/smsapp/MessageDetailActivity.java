package gonext.smsapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MessageDetailActivity extends Activity {
    private TextView detailMessage;
    private String message = "";

    @TargetApi(11)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.message = bundle.getString("MESSAGE");
        }
        this.detailMessage = (TextView) findViewById(R.id.text);
        this.detailMessage.setText(this.message);
    }
}
