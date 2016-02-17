package com.clj.jaf.app;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;

import com.clj.jaf.broadcast.JBroadcastByInner;
import com.clj.jaf.broadcast.JBroadcastByProcess;
import com.clj.jaf.networdk.JINetChangeListener;
import com.clj.jaf.networdk.JNetWorkUtil;
import com.clj.jaf.preference.JPreferenceConfig;
import com.clj.jaf.task.JITaskListener;
import com.clj.jaf.task.JTask;

import java.util.ArrayList;

/**
 * 集成Task监听、网络变化监听、广播接收监听
 */
public class JApplication extends Application implements JITaskListener,
        JINetChangeListener, JIProcessEvent {

    protected static JApplication mThis = null;
    protected static boolean mDebug = true;
    private JTask mInitTask = null;
    private boolean mInit = false;
    protected IntentFilter filter = new IntentFilter();
    protected ArrayList<String> mBroadcastParametersInner = new ArrayList<>();
    protected ArrayList<String> mBroadcastParametersProcess = new ArrayList<>();

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (JBroadcastByInner.INTENT_ACTION_EVENT.equals(action)) {
                JApplication.this.initBroadcastParameterByInner(intent);
                JApplication.this.processEventByInner(intent);
            } else if (JBroadcastByProcess.INTENT_ACTION_EVENT.equals(action)) {
                JApplication.this.initBroadcastParameterByProcess(intent);
                JApplication.this.processEventByProcess(intent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mThis = this;
        this.filter.addAction(JBroadcastByInner.INTENT_ACTION_EVENT);
        this.filter.addAction(JBroadcastByProcess.INTENT_ACTION_EVENT);
        this.filter.setPriority(1000);
        this.registerReceiver(this.mBroadcastReceiver, this.filter);
        JPreferenceConfig.getInstance().initConfig(this);
        this.mInitTask = new JTask();
        this.mInitTask.setIXTaskListener(this);
        this.mInitTask.startTask(100);
    }

    @Override
    public void onTerminate() {
        if (this.mInitTask != null) {
            this.mInitTask.stopTask();
        }

        this.mInitTask = null;
        this.onExitApplication();
        this.unregisterReceiver(this.mBroadcastReceiver);
        this.mBroadcastReceiver = null;
        this.filter = null;
        if (this.mBroadcastParametersInner != null) {
            this.mBroadcastParametersInner.clear();
        }

        this.mBroadcastParametersInner = null;
        if (this.mBroadcastParametersProcess != null) {
            this.mBroadcastParametersProcess.clear();
        }

        this.mBroadcastParametersProcess = null;
        super.onTerminate();
    }

    @Override
    public void onConnect(JNetWorkUtil.netType var1) {
    }

    @Override
    public void onDisConnect() {
    }

    @Override
    public void processEventByInner(Intent intent) {
    }

    @Override
    public void processEventByProcess(Intent intent) {
    }

    public static JApplication getInstance() {
        return mThis;
    }

    protected void onExitApplication() {
        JPreferenceConfig.getInstance().release();
    }

    protected void onInitConfigByThread() {
    }

    public void onInitComplete() {
    }

    public boolean isInitComplete() {
        return this.mInit;
    }

    public static boolean isRelease() {
        return !mDebug;
    }

    @Override
    public void onTask(JTask.Task task, JTask.TaskEvent event, Object... params) {
        if (this.mInitTask != null && this.mInitTask.equalTask(task)) {
            try {
                if (event == JTask.TaskEvent.Work) {
                    this.onInitConfigByThread();
                } else if (event == JTask.TaskEvent.Cancel) {
                    this.mInit = true;
                    this.onInitComplete();
                }
            } catch (Exception var5) {
                //ignore
            }
        }
    }

    private void initBroadcastParameterByInner(Intent intent) {
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

    private void initBroadcastParameterByProcess(Intent intent) {
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

    public PackageInfo getPackageInfo(int flags) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), flags);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return packageInfo;
    }

    public ArrayList<String> getBroadcastParameterByInner() {
        return this.mBroadcastParametersInner;
    }

    public ArrayList<String> getBroadcastParameterByProcess() {
        return this.mBroadcastParametersProcess;
    }

    public static String getResString(int id) {
        return getInstance().getString(id);
    }

    public int getStatusByComponent(String packageName, String receiverName) {
        ComponentName mComponentName = new ComponentName(packageName, receiverName);
        return this.getPackageManager().getComponentEnabledSetting(mComponentName);
    }
}
