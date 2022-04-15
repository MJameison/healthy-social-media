package com.squadrant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squadrant.postboxnotification.R;
import com.squadrant.model.StoredNotification;

import java.util.List;

public class StoredNotificationAdapter extends RecyclerView.Adapter<StoredNotificationAdapter.StoredNotificationViewHolder> {

    private Context context;
    private List<StoredNotification> notifications;

    public StoredNotificationAdapter(Context context, List<StoredNotification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public StoredNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stored_notification_item, parent, false);
        return new StoredNotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoredNotificationViewHolder holder, int position) {
        StoredNotification storedNotification = notifications.get(position);
        if (storedNotification == null) return;

        // TODO: Update params?
        holder.titleView.setText(storedNotification.notificationTitle);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class StoredNotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;

        public StoredNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.notification_title);
        }
    }
}
