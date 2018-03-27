package com.fase.core.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.fase.FaseApp;
import com.fase.R;
import com.fase.ui.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import timber.log.Timber;

public class FaseNotificationService extends FirebaseMessagingService {

    private static final String PRIMARY_CHANNEL = "default";
    private static final String PRIMARY_GROUP = "defaultGroup";
    private static final int STACK_ID = 1515;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Timber.d("Message received: %s", message.toString());
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            initChannel(notificationManager);
            // notification.body and notification.title ignored.
            if (message.getData().size() > 0) {
                Map<String, String> data = message.getData();
                showNotification(notificationManager, data.get("title"), data.get("body"));
            }
        } else {
            Timber.e("Notification service == null");
        }
    }

    private void showNotification(NotificationManager notificationManager, String title, String body) {
        final int id = (int) new Date().getTime() % Integer.MAX_VALUE;
        Notification.Builder notificationBuilder = Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                new Notification.Builder(getApplicationContext()) : new Notification.Builder(getApplicationContext(), PRIMARY_CHANNEL);

        Intent intent = new Intent(FaseApp.getApplication(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)// FIXME:
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))// FIXME:
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (!TextUtils.isEmpty(body)) {
            notificationBuilder.setContentText(body);
        }
        if (!TextUtils.isEmpty(title)) {
            notificationBuilder.setContentTitle(title);
        } else {
            notificationBuilder.setContentTitle(getString(R.string.app_name));
        }
        if (Build.VERSION.SDK_INT >= 26) {
            notificationBuilder.setGroup(PRIMARY_GROUP);
        }

        notificationManager.notify(id, notificationBuilder.build());

        if (Build.VERSION.SDK_INT >= 26) {
            ArrayList<StatusBarNotification> groupedNotifications = new ArrayList<>();

            for (StatusBarNotification statusBarNotification : notificationManager.getActiveNotifications()) {
                if (statusBarNotification.getId() != STACK_ID) {
                    groupedNotifications.add(statusBarNotification);
                }
            }

            if (groupedNotifications.size() > 1) {
                notificationManager.notify(PRIMARY_GROUP, STACK_ID, new NotificationCompat.Builder(this, PRIMARY_CHANNEL)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.you_have_unread_messages))
                        .setSmallIcon(R.mipmap.ic_launcher)// FIXME:
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // FIXME:
                        .setGroup(PRIMARY_GROUP)
                        .setGroupSummary(true)
                        .setAutoCancel(true)
                        .build());
            }
        }
    }

    protected void initChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        if (notificationManager != null) {
            if (notificationManager.getNotificationChannel(PRIMARY_CHANNEL) != null) {
                return;
            }
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
