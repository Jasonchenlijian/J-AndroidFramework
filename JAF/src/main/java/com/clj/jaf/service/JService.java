package com.clj.jaf.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.clj.jaf.app.JIProcessEvent;
import com.clj.jaf.broadcast.JBroadcastByInner;
import com.clj.jaf.broadcast.JBroadcastByProcess;

import java.util.ArrayList;

public class JService extends Service implements JIProcessEvent {
    protected Context mContext;
    protected IntentFilter filter = new IntentFilter();
    protected ArrayList<String> mBroadcastParametersInner = new ArrayList<>();
    protected ArrayList<String> mBroadcastParametersProcess = new ArrayList<>();
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (JBroadcastByInner.INTENT_ACTION_EVENT.equals(action)) {
                JService.this.initBroadcastParameterByInner(intent);
                JService.this.processEventByInner(intent);
            } else if (JBroadcastByProcess.INTENT_ACTION_EVENT.equals(action)) {
                JService.this.initBroadcastParameterByProcess(intent);
                JService.this.processEventByProcess(intent);
            }

        }
    };

    public JService() {
    }

    public void processEventByInner(Intent intent) {
    }

    public void processEventByProcess(Intent intent) {
    }

    public void onCreate() {
        super.onCreate();
        this.startForeground(0, null);
        this.filter.addAction(JBroadcastByInner.INTENT_ACTION_EVENT);
        this.filter.addAction(JBroadcastByProcess.INTENT_ACTION_EVENT);
        this.filter.setPriority(1000);
        this.registerReceiver(this.mBroadcastReceiver, this.filter);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        this.stopForeground(true);
        this.unregisterReceiver(this.mBroadcastReceiver);
        this.mBroadcastReceiver = null;
        if (this.mBroadcastParametersInner != null) {
            this.mBroadcastParametersInner.clear();
        }

        this.mBroadcastParametersInner = null;
        if (this.mBroadcastParametersProcess != null) {
            this.mBroadcastParametersProcess.clear();
        }

        this.mBroadcastParametersProcess = null;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    protected void initBroadcastParameterByInner(Intent intent) {
        if (this.mBroadcastParametersInner != null) {
            this.mBroadcastParametersInner.clear();
            this.mBroadcastParametersInner.add(intent.getStringExtra("message0"));
            this.mBroadcastParametersInner.add(intent.getStringExtra("message1"));
            this.mBroadcastParametersInner.add(intent.getStringExtra("message2"));
            this.mBroadcastParametersInner.add(intent.getStringExtra("message3"));
            this.mBroadcastParametersInner.add(intent.getStringExtra("message4"));
            this.mBroadcastParametersInner.add(intent.getStringExtra("message5"));
        }
    }

    protected void initBroadcastParameterByProcess(Intent intent) {
        if (this.mBroadcastParametersProcess != null) {
            this.mBroadcastParametersProcess.clear();
            this.mBroadcastParametersProcess.add(intent.getStringExtra("message0"));
            this.mBroadcastParametersProcess.add(intent.getStringExtra("message1"));
            this.mBroadcastParametersProcess.add(intent.getStringExtra("message2"));
            this.mBroadcastParametersProcess.add(intent.getStringExtra("message3"));
            this.mBroadcastParametersProcess.add(intent.getStringExtra("message4"));
            this.mBroadcastParametersProcess.add(intent.getStringExtra("message5"));
        }
    }

    protected ArrayList<String> getBroadcastParameterByInner() {
        return this.mBroadcastParametersInner;
    }

    protected ArrayList<String> getBroadcastParameterByProcess() {
        return this.mBroadcastParametersProcess;
    }
}
