package com.lishensong.apidemo.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.lishensong.apidemo.R;

import java.util.ArrayList;

/**
 * Created by li.shensong on 2016/10/10.
 */
public class MessengerService extends Service {
    /** For showing and hiding our notification. */
    NotificationManager mNM;
    /** Keeps track of all current registered clients.*/
    ArrayList<Messenger> mClients = new ArrayList<>();
    /** Holds last value set by a client*/
    int mValue = 0;

    /**
     * Command to the service to register a client,receiving callbacks
     * from the service .The Message's replyTO field must be a Messenger of
     * the client where callbacks should be sent.
     */
    static final int MSG_REGISTER_CLIENT = 1;

    /***
     * Command to the service to unregister a client, to stop receiving callbacks from the
     * service. The Message's replyTo field must be a Messenger of
     * the client ad previously given with MSG_REGISTER_CLIENT;
     */
    static final int MSG_UNREGISTER_CLIENT = 2;

    /***
     * Command to service to set a new value. This can be sent to the
     * service to supply a new value, and will be sent by the service to
     * any registered clients with the new value
     */
    static final int MSG_SET_VALUE = 3;

    /**
     * Handler of incoming messages from client
     *
     */
    class IncomingHandler extends Handler{
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what){
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SET_VALUE:
                    mValue = msg.arg1;
                    for(int i=mClients.size() -1 ; i>= 0; i--){
                        try {
                            mClients.get(i).send(Message.obtain(null,MSG_SET_VALUE,mValue,0));
                        }catch (RemoteException e){
                            mClients.remove(i);
                        }
                    }
                default:
                        super.dispatchMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        mNM.cancel(R.string.remote_service_started);
        Toast.makeText(this,R.string.remote_service_stopped,Toast.LENGTH_SHORT).show();
    }

    private void showNotification(){
        CharSequence text = getText(R.string.remote_service_started);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,
                new Intent(this, LocalServiceActivities.Controller.class),0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getText(R.string.local_service_label))
                .setContentText(text)
                .setContentIntent(contentIntent)
                .build();
        mNM.notify(R.string.remote_service_started,notification);
    }
}
