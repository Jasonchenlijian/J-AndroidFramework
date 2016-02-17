package com.clj.jaf.networdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.clj.jaf.app.JIGlobalInterface;

import java.util.ArrayList;

public class JNetworkStateReceiver extends BroadcastReceiver implements JIGlobalInterface {
    private static Boolean mNetworkAvailable = false;
    private static JNetWorkUtil.netType mNetType;
    private static ArrayList<JINetChangeListener> mNetChangeObserverArrayList = new ArrayList<>();
    private static final String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String TA_ANDROID_NET_CHANGE_ACTION = "think.android.net.conn.CONNECTIVITY_CHANGE";
    private static JNetworkStateReceiver mThis;
    private Context mContext;

    public JNetworkStateReceiver() {
    }

    public static JNetworkStateReceiver getInstance() {
        if (mThis == null) {
            mThis = new JNetworkStateReceiver();
        }
        return mThis;
    }

    public void initConfig(Context context) {
        this.mContext = context;
        this.registerNetworkStateReceiver();
    }

    public void initConfig() {
    }

    public void release() {
        this.unRegisterNetworkStateReceiver();
    }

    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION)
                || intent.getAction().equalsIgnoreCase(TA_ANDROID_NET_CHANGE_ACTION)) {
            Log.i("TANetworkStateReceiver", "网络状态变化");

            if (!JNetWorkUtil.isNetworkAvailable()) {
                Log.i("TANetworkStateReceiver", "网络不可用");
                mNetworkAvailable = false;
            } else {
                Log.i("TANetworkStateReceiver", "网络可用");
                mNetType = JNetWorkUtil.getAPNType();
                mNetworkAvailable = true;
            }

            this.notifyObserver();
        }
    }

    private void registerNetworkStateReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TA_ANDROID_NET_CHANGE_ACTION);
        filter.addAction(ANDROID_NET_CHANGE_ACTION);
        this.mContext.getApplicationContext().registerReceiver(getInstance(), filter);
    }

    public void checkNetworkState() {
        Intent intent = new Intent();
        intent.setAction(TA_ANDROID_NET_CHANGE_ACTION);
        this.mContext.sendBroadcast(intent);
    }

    private void unRegisterNetworkStateReceiver() {
        try {
            this.mContext.getApplicationContext().unregisterReceiver(this);
        } catch (Exception var2) {
            Log.d("TANetworkStateReceiver", var2.getMessage());
        }

    }

    public Boolean isNetworkAvailable() {
        return mNetworkAvailable;
    }

    public JNetWorkUtil.netType getAPNType() {
        return mNetType;
    }

    private void notifyObserver() {
        for (int i = 0; i < mNetChangeObserverArrayList.size(); ++i) {
            JINetChangeListener observer = mNetChangeObserverArrayList.get(i);
            if (observer != null) {
                if (this.isNetworkAvailable()) {
                    observer.onConnect(mNetType);
                } else {
                    observer.onDisConnect();
                }
            }
        }
    }

    public static void registerObserver(JINetChangeListener observer) {
        if (mNetChangeObserverArrayList == null) {
            mNetChangeObserverArrayList = new ArrayList<>();
        }

        mNetChangeObserverArrayList.add(observer);
    }

    public void removeRegisterObserver(JINetChangeListener observer) {
        if (mNetChangeObserverArrayList != null) {
            mNetChangeObserverArrayList.remove(observer);
        }

    }
}
