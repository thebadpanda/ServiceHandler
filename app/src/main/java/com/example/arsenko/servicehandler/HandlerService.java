package com.example.arsenko.servicehandler;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

public class HandlerService extends Service {

    public static  final int  SET_COUNT = 0;
    public static final int INCR = 1;
    public static final int DECR = 2;
    public static final int GET_COUNT = 3;

    int count = 0;

    MyHandler mHandler;
    Messenger messanger;
    Messenger toActivityMessenger;


    @Override
    public void onCreate(){
        super.onCreate();

        HandlerThread mHandlerThread = new HandlerThread("incremDecrem");
        mHandlerThread.start();
        mHandler = new MyHandler(mHandlerThread.getLooper());
        messanger = new Messenger(mHandler);


    }


    private class MyHandler extends Handler{
        public MyHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message msg){

            toActivityMessenger = msg.replyTo;

            switch (msg.what){
                case SET_COUNT:
                    count = msg.arg1;
                    break;
                case INCR:
                    Toast.makeText(getApplicationContext(), "incrementation", Toast.LENGTH_SHORT).show();
                    count ++;
                    break;
                case DECR:
                    Toast.makeText(HandlerService.this, "decrementation", Toast.LENGTH_SHORT).show();
                    count --;
                    break;

                default:
                    super.handleMessage(msg);
            }

            //відправляємо значення лічильника в актівіті

            Message actMsg = Message.obtain(mHandler, GET_COUNT);
            actMsg.arg1 = count;
            actMsg.replyTo = messanger;

            try {
                if(toActivityMessenger != null)
                    toActivityMessenger.send(actMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return messanger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
//        if(intent.getAction() != null){
//            switch(intent.getAction()){
//               case HandlerService.INCREMENT:
//                    myHa
//
//
//            }
//        }
        return START_STICKY;
    }
}
