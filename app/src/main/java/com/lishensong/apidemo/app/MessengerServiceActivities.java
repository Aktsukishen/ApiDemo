package com.lishensong.apidemo.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lishensong.apidemo.R;

/**
 * Created by li.shensong on 2016/10/10.
 */
public class MessengerServiceActivities {

    public static  class Binding extends Activity{
        /** Messenger for communicating with service.*/
        Messenger mService = null;
        /** Flag indicating whether we have called bind on the service.*/
        boolean mIsBound = false;
        /** Some text view we are using to show state information.*/
        TextView mCallbackText;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.messenger_service_binding);
            //Watch for button clicks
            Button button = (Button) findViewById(R.id.bind);
            button.setOnClickListener(mBindListener);
            button = (Button) findViewById(R.id.unbind);
            button.setOnClickListener(mUnbindListener);

            mCallbackText = (TextView)findViewById(R.id.callback);
            mCallbackText.setText("Not attached");
        }

        private View.OnClickListener mBindListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBindService();
            }
        };

        private View.OnClickListener mUnbindListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                doUnbindService();
            }
        };

        class IncomingHandler extends Handler{
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MessengerService.MSG_SET_VALUE:
                        mCallbackText.setText("Received from service: " + msg.arg1);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }

        final Messenger mMessenger = new Messenger(new IncomingHandler());

        private ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = new Messenger(service);
                mCallbackText.setText("Attached");

                try{
                    Message msg = Message.obtain(null,MessengerService.MSG_REGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                    //Give it some value as an example
                    msg = Message.obtain(null,MessengerService.MSG_SET_VALUE,this.hashCode(),0);
                    mService.send(msg);
                }catch (RemoteException e){

                }

                Toast.makeText(Binding.this,R.string.remote_service_connected,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
                mCallbackText.setText("Disconnected...");
                Toast.makeText(Binding.this,R.string.remote_service_disconnected,Toast.LENGTH_SHORT).show();
            }
        };

        void doBindService(){
            bindService(new Intent(Binding.this,MessengerService.class),mConnection, Context.BIND_AUTO_CREATE);
            mIsBound = true;
            mCallbackText.setText("Binding...");
        }

        void doUnbindService(){
            if(mIsBound){
                if(mService != null){
                    try{
                        Message msg = Message.obtain(null,MessengerService.MSG_UNREGISTER_CLIENT);
                        msg.replyTo = mMessenger;
                        mService.send(msg);
                    }catch (RemoteException e){
                    }
                }
                unbindService(mConnection);
                mIsBound = false;
                mCallbackText.setText("Unbinding...");
            }
        }


    }
}
