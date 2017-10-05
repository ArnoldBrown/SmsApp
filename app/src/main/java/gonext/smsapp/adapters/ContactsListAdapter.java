package gonext.smsapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

import gonext.smsapp.R;
import gonext.smsapp.contacts.ContactData;
import gonext.smsapp.contacts.ContactDetails;

public class ContactsListAdapter extends ArrayAdapter<ContactData> {
    private final List<ContactData> contactList;
    private final Context context;

    public ContactsListAdapter(Context context, List<ContactData> smsList) {
        super(context, R.layout.contact_list_item, smsList);
        this.context = context;
        this.contactList = smsList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.contact_list_item, parent, false);
        if(this.contactList.get(position).getContactDetails().size() > 0) {
            ((TextView) rowView.findViewById(R.id.number)).setText(((ContactDetails) ((ContactData) this.contactList.get(position)).getContactDetails().get(0)).getPhoneNo());
        }else{
            ((TextView) rowView.findViewById(R.id.number)).setText("00000");
        }
        return rowView;
    }
}
