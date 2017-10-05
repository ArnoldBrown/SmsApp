package gonext.smsapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

import gonext.smsapp.R;
import gonext.smsapp.contacts.SMSData;

public class MessagesListAdapter extends ArrayAdapter<SMSData> {
    private final Context context;
    private final List<SMSData> smsList;

    public MessagesListAdapter(Context context, List<SMSData> smsList) {
        super(context, R.layout.contact_list_item, smsList);
        this.context = context;
        this.smsList = smsList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.contact_list_item, parent, false);
        ((TextView) rowView.findViewById(R.id.number)).setText(((SMSData) this.smsList.get(position)).getNumber());
        return rowView;
    }
}
