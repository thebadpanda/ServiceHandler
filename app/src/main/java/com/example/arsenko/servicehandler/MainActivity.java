package com.example.arsenko.servicehandler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final Messenger messenger = new Messenger(new MyHandler());
    Messenger toServiceMessenger;
    MyServiceConnection connection;
    boolean mBound = false;
    TextView textView;
    int count = 0;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

//        bindService(new Intent(this, HandlerService.class), connection, Context.BIND_AUTO_CREATE);
    }

    public void onStart() {
        super.onStart();
        bindService(new Intent(this, HandlerService.class), (connection = new MyServiceConnection()), Context.BIND_AUTO_CREATE);

    }

    public void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(connection);
            mBound = false;
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerService.GET_COUNT:
                    textView.setText(" " + msg.arg1);
                    break;
            }
        }
    }

    public void countInc(View button) {
        Message msg = Message.obtain(null, HandlerService.INCR);
        msg.replyTo = messenger;
        try {
            toServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void countDec(View button) {
        Message msg = Message.obtain(null, HandlerService.DECR);
        msg.replyTo = messenger;
        try {
            toServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            toServiceMessenger = new Messenger(service);
            // відправляємо початкове значення лічильника
            Message msg = Message.obtain(null, HandlerService.SET_COUNT);
            msg.replyTo = messenger;
            msg.arg1 = count;
            try {
                toServiceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }


            Toast.makeText(MainActivity.this, "onServiceConn", Toast.LENGTH_SHORT).show();
            mBound = true;


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(MainActivity.this, "onServDisconn", Toast.LENGTH_SHORT).show();
            mBound = false;

        }
    }
}
