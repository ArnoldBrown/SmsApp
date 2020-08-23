package gonext.smsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by muthulakshmi on 27/01/18.
 */

public class ListAdapter extends RecyclerView.Adapter<ListViewholder> {

    private Context context;
    private List<String> items;

    public ListAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ListViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListViewholder(view);
    }

    @Override
    public void onBindViewHolder(ListViewholder holder, int position) {

            holder.textView.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}

