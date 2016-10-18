package com.lishensong.apidemo.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.lishensong.apidemo.R;

/**
 * Created by li.shensong on 2016/10/9.
 */
public class LocalService extends Service {
    private static final String TAG = "LocalService";

    private NotificationManager mNM;

    private int NOTIFICATION = R.string.local_service_started;

    public class LocalBinder extends Binder{
        LocalService getService(){
            return LocalService.this;
        }
    }

    private final LocalBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification();
    }

    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);
        Toast.makeText(this,R.string.local_service_stopped,Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: Receive start id " + startId + ":" + intent);
        return START_NOT_STICKY;
    }

    private void showNotification(){
        CharSequence text = getText(R.string.local_service_started);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,
                new Intent(this,LocalServiceActivities.Controller.class),0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getText(R.string.local_service_label))
                .setContentText(text)
                .setContentIntent(contentIntent)
                .build();
        mNM.notify(NOTIFICATION,notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
