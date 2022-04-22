package com.squadrant.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squadrant.App;
import com.squadrant.model.StoredNotification;
import com.squadrant.postboxnotification.R;
import com.squadrant.util.PackageNameUtils;

import java.util.List;

public class StoredNotificationAdapter extends RecyclerView.Adapter<StoredNotificationAdapter.StoredNotificationViewHolder> {

    private final Context context;
    private final List<StoredNotification> notifications;

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

        //holder.appIconView.setImageResource(PackageNameUtils.getIconResource(storedNotification.notificationPackage));
        holder.appIconView.setImageDrawable(PackageNameUtils.getAppIcon(storedNotification.notificationPackage));
        holder.appNameView.setText(PackageNameUtils.getAppName(storedNotification.notificationPackage));
        holder.titleView.setText(storedNotification.notificationTitle);
        holder.contentView.setText(storedNotification.notificationContent);
        holder.timeSentView.setText(PackageNameUtils.getTimeSent(context, storedNotification.notificationWhen));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class StoredNotificationViewHolder extends RecyclerView.ViewHolder {
        final ImageView appIconView;
        final TextView appNameView;
        final TextView titleView;
        final TextView contentView;
        final TextView timeSentView;

        public StoredNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            appIconView = itemView.findViewById(R.id.notification_image);
            appNameView = itemView.findViewById(R.id.notification_app_name);
            titleView = itemView.findViewById(R.id.notification_title);
            contentView = itemView.findViewById(R.id.notification_content);
            timeSentView = itemView.findViewById(R.id.notification_time_sent);
        }
    }
}
