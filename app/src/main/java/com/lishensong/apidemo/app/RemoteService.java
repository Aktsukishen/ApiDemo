package com.lishensong.apidemo.app;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lishensong.apidemo.R;

/**
 * Created by li.shensong on 2016/10/10.
 */
public class RemoteService extends Service{
    private static final String TAG = "RemoteService";

    final RemoteCallbackList<IRemoteServiceCallback> mCallbacks
            = new RemoteCallbackList<>();

    int mValue = 0;
    NotificationManager mNM;

    private static final int REPORT_MSG = 1;

    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REPORT_MSG:{
                    int value = ++mValue;
                    final int N = mCallbacks.beginBroadcast();
                    for(int i =0 ;i<N ; i++){
                        try {
                            mCallbacks.getBroadcastItem(i).valueChanged(value);
                        }catch (RemoteException e){

                        }
                    }
                    mCallbacks.finishBroadcast();
                    sendMessageDelayed(obtainMessage(REPORT_MSG),1 * 1000);
                }
                break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
        mHandler.sendEmptyMessage(REPORT_MSG);
    }

    private void showNotification(){
        CharSequence text = getText(R.string.remote_service_started);
        PendingIntent contentIntent  = PendingIntent.getActivity(this,0,
                new Intent(this,Controller.class),0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(text)
                .setContentText(text)
                .setContentIntent(contentIntent)
                .build();
        mNM.notify(R.string.remote_service_started,notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: Received start id " + startId + ":" + intent);
        return  START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mNM.cancel(R.string.remote_service_started);
        Toast.makeText(this,R.string.remote_service_stopped,Toast.LENGTH_SHORT).show();
        mCallbacks.kill();
        mHandler.removeMessages(REPORT_MSG);
        Intent intent = new Intent(this,RemoteService.class);
        startService(intent);
    }

    private final IRemoteService.Stub mBinder = new IRemoteService.Stub(){
        @Override
        public void registerCallback(IRemoteServiceCallback cb){
            if(cb != null) mCallbacks.register(cb);
        }

        @Override
        public void unregisterCallback(IRemoteServiceCallback cb){
            if(cb != null) mCallbacks.unregister(cb);
        }
    };

    private final ISecondary.Stub mSecondaryBinder = new ISecondary.Stub(){
        @Override
        public int getPid(){
            return Process.myPid();
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                               double aDouble, String aString){

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        if(IRemoteService.class.getName().equals(intent.getAction())){
            return mBinder;
        }
        if(ISecondary.class.getName().equals(intent.getAction())){
            return mSecondaryBinder;
        }
        return null;
    }

    public static class Controller extends Activity{
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.remote_service_controller);
            Button button = (Button)findViewById(R.id.start);
            button.setOnClickListener(mStartListener);
            button = (Button)findViewById(R.id.stop);
            button.setOnClickListener(mStopListener);
        }

        private View.OnClickListener mStartListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(Controller.this,RemoteService.class));
            }
        };

        private View.OnClickListener mStopListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                stopService(new Intent(Controller.this,RemoteService.class));
            }
        };
    }

    //-------------------------------------------------------------------------------------------
    public static class Binding extends Activity{
        /** The primary interface we will be calling on the service.*/
        IRemoteService mService = null;
        /** Another interface we use on the service.*/
        ISecondary mSecondaryService = null;

        private Button mKillButton;
        private TextView mCallbackText;

        private boolean mIsBound ;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.remote_service_binding);

            Button button = (Button)findViewById(R.id.bind);
            button.setOnClickListener(mBindListener);
            button = (Button)findViewById(R.id.unbind);
            button.setOnClickListener(mUnbindListener);
            mKillButton = (Button)findViewById(R.id.kill);
            mKillButton.setOnClickListener(mKillListener);
            mKillButton.setEnabled(false);

            mCallbackText = (TextView)findViewById(R.id.callback);
            mCallbackText.setText("Not attached");

        }

        private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {
            @Override
            public void valueChanged(int value)  {
                mHandler.sendMessage(mHandler.obtainMessage(BUMP_MSG,value,0));
            }
        };

        private static final int BUMP_MSG = 1;
        private Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case BUMP_MSG:
                        mCallbackText.setText("Received from service: " + msg.arg1);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };

        private ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IRemoteService.Stub.asInterface(service);
                mKillButton.setEnabled(true);
                mCallbackText.setText("Attached.");
                try {
                    mService.registerCallback(mCallback);
                }catch (RemoteException e){

                }
                Toast.makeText(Binding.this,R.string.remote_service_connected,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
                mKillButton.setEnabled(false);
                mCallbackText.setText("Disconnected");
                Toast.makeText(Binding.this,R.string.remote_service_disconnected,Toast.LENGTH_SHORT).show();
            }
        };

        private ServiceConnection mSecondaryConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mSecondaryService = ISecondary.Stub.asInterface(service);
                mKillButton.setEnabled(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mSecondaryService = null;
                mKillButton.setEnabled(false);
            }
        };

        private View.OnClickListener mBindListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Binding.this,RemoteService.class);
                intent.setAction(IRemoteService.class.getName());
                bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
                intent.setAction(ISecondary.class.getName());
                bindService(intent,mSecondaryConnection,Context.BIND_AUTO_CREATE);
                mIsBound = true;
                mCallbackText.setText("Binding...");
            }
        };

        private View.OnClickListener mUnbindListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsBound){
                    if(mService != null){
                        try{
                            mService.unregisterCallback(mCallback);
                        }catch (RemoteException e){

                        }
                    }
                    unbindService(mConnection);
                    unbindService(mSecondaryConnection);
                    mKillButton.setEnabled(false);
                    mIsBound = false;
                    mCallbackText.setText("Unbinding...");
                }
            }
        };

        private View.OnClickListener mKillListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mSecondaryService != null){
                    try{
                        int pid = mSecondaryService.getPid();
                        Process.killProcess(pid);
                        mCallbackText.setText("Killed service process");
                    }catch (RemoteException ex){
                        Toast.makeText(Binding.this,R.string.remote_call_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }


}
