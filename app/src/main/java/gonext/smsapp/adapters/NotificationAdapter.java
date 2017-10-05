package gonext.smsapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gonext.smsapp.R;
import gonext.smsapp.db.NotificationEntity;


/**
 * Created by ramse on 25-03-2016.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.HomeImageViewHolder> {
    private Context context = null;
    private List<NotificationEntity> list = new ArrayList<>();
    public NotificationAdapter(Context context, List<NotificationEntity> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public HomeImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.notification_list_item, parent, false);
        return new HomeImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HomeImageViewHolder holder, int position) {
        NotificationEntity notificationEntity = list.get(position);
        holder.title.setText(notificationEntity.getTitle());
        holder.message.setText(notificationEntity.getMessage());
        holder.date.setText(notificationEntity.getDateTime());
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public  class HomeImageViewHolder extends RecyclerView.ViewHolder {
        protected TextView title,message,date;
        public HomeImageViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            message = (TextView) v.findViewById(R.id.message);
            date = (TextView) v.findViewById(R.id.time);
        }
    }
    public void refresh(List<NotificationEntity> notificationEntities){
        this.list = notificationEntities;
        notifyDataSetChanged();
    }
}
