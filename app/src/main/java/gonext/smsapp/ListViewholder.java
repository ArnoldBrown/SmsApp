package gonext.smsapp;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by muthulakshmi on 27/01/18.
 */

public class ListViewholder extends RecyclerView.ViewHolder {

    public TextView textView;

    public ListViewholder(View itemView) {
        super(itemView);

        textView = (TextView) itemView.findViewById(R.id.textview);
    }

}
