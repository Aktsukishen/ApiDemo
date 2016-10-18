package com.lishensong.apidemo.app;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.lishensong.apidemo.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by li.shensong on 2016/10/10.
 */
public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";
    public static final String ACTION_FOREGROUND = "com.lss.app.service.FOREGROUND";
    public static final String ACTION_BACKGROUND = "com.lss.app.service.BACKGROUND";

    private static final Class<?>[] mSetForegroundSignature = new Class[]{boolean.class};
    private static final Class<?>[] mStartForegroundSignature = new Class[]{int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[]{boolean.class};

    private NotificationManager mNM;
    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try{
            mStartForeground = getClass().getMethod("startForeground",mSetForegroundSignature);
            mStopForeground = getClass().getMethod("stopForeground",mStopForegroundSignature);
            return;
        }catch (NoSuchMethodException e){
            mStartForeground = mStopForeground = null;
        }
        try {
            mSetForeground = getClass().getMethod("setForeground",mSetForegroundSignature);
        }catch (NoSuchMethodException e){
            throw new IllegalStateException("OS doesn't have Service.startForeground OR Service.setForeground");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        return START_STICKY;
    }

    void handleCommand(Intent intent){
        if(ACTION_FOREGROUND.equals(intent.getAction())){
            CharSequence text = getText(R.string.foreground_service_started);
            PendingIntent contentIntent = PendingIntent.getActivity(this,0,
                    new Intent(this,Controller.class),0);
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.icon)
                    .setTicker(text)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(getText(R.string.alarm_service_label))
                    .setContentText(text)
                    .setContentIntent(contentIntent)
                    .build();
            startForegroundCompat(R.string.foreground_service_started,notification);
        }else if(ACTION_BACKGROUND.equals(intent.getAction())){
            stopForegroundCompat(R.string.foreground_service_started);
        }
    }

    void startForegroundCompat(int id,Notification notification){
        if(mStartForeground != null){
            mStartForegroundArgs[0] = Integer.valueOf(id);
            mStartForegroundArgs[1] = notification;
            invokeMethod(mStartForeground,mSetForegroundArgs);
            return;
        }
        mSetForegroundArgs[0] = Boolean.TRUE;
        invokeMethod(mSetForeground,mSetForegroundArgs);
        mNM.notify(id,notification);
    }

    void stopForegroundCompat(int id){
        if(mStopForeground != null){
            mStopForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mStopForeground,mSetForegroundArgs);
            return;
        }
        mNM.cancel(id);
        mSetForegroundArgs[0] = Boolean.FALSE;
        invokeMethod(mSetForeground,mSetForegroundArgs);
    }

    void invokeMethod(Method m,Object[]args){
        try {
            m.invoke(this,args);
        }catch (InvocationTargetException e){
            Log.w(TAG, "invokeMethod: unable to invoke method",e );
        }catch (IllegalAccessException e){
            Log.w(TAG, "invokeMethod: unable to invoke method",e );
        }

    }

    public static class Controller extends Activity{
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.foreground_service_controller);
            Button button = (Button) findViewById(R.id.start_foreground);
            button.setOnClickListener(mForegroundListener);
            button = (Button) findViewById(R.id.start_background);
            button.setOnClickListener(mBackgroundListener);
            button = (Button) findViewById(R.id.stop);
            button.setOnClickListener(mStopListener);
        }

        private View.OnClickListener mForegroundListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForegroundService.ACTION_FOREGROUND);
                intent.setClass(Controller.this,ForegroundService.class);
                startService(intent);
            }
        };

        private View.OnClickListener mBackgroundListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForegroundService.ACTION_BACKGROUND);
                intent.setClass(Controller.this,ForegroundService.class);
                startService(intent);
            }
        };

        private View.OnClickListener mStopListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                stopService(new Intent(Controller.this,ForegroundService.class));
            }
        };

    }
}
