package com.lishensong.apidemo.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lishensong.apidemo.R;

/**
 * Created by li.shensong on 2016/10/9.
 */
public class LocalServiceActivities {

    /** 开始、结束Service */
    public static class Controller extends Activity{
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.local_service_controller);
            Button button = (Button) findViewById(R.id.start);
            button.setOnClickListener(mStartListener);
            button = (Button) findViewById(R.id.stop);
            button.setOnClickListener(mStopListener);
        }

        private View.OnClickListener mStartListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(Controller.this,LocalService.class));
            }
        };

        private View.OnClickListener mStopListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                stopService(new Intent(Controller.this,LocalService.class));
            }
        };
    }

    // ---------------------------------------------------------------------------------------------
    public static class Binding extends Activity{
        private boolean mIsBound;

        private LocalService mBoundService;

        private ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBoundService = ((LocalService.LocalBinder)service).getService();
                Toast.makeText(Binding.this,R.string.local_service_connected,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBoundService = null;
                Toast.makeText(Binding.this,R.string.local_service_disconnected,Toast.LENGTH_SHORT).show();
            }
        };


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.local_service_binding);
            Button button = (Button) findViewById(R.id.bind);
            button.setOnClickListener(mBindListener);
            button = (Button)findViewById(R.id.unbind);
            button.setOnClickListener(mUnbindListener);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            doUnbindService();
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

        private void doBindService(){
            bindService(new Intent(Binding.this,LocalService.class),mConnection, Context.BIND_AUTO_CREATE);
            mIsBound = true;
        }

        private void doUnbindService(){
            if(mIsBound){
                unbindService(mConnection);
                mIsBound = false;
            }
        }
    }
}
