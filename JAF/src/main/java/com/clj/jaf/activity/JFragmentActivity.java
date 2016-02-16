package com.clj.jaf.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.clj.jaf.app.JApplication;
import com.clj.jaf.app.JIProcessEvent;
import com.clj.jaf.broadcast.JBroadcastByInner;
import com.clj.jaf.broadcast.JBroadcastByProcess;
import com.clj.jaf.task.JITaskListener;
import com.clj.jaf.task.JTask;
import com.clj.jaf.utils.JActivityUtil;
import com.clj.jaf.utils.JStringUtil;
import com.clj.jaf.utils.JToastUtil;

import java.util.ArrayList;

public class JFragmentActivity extends FragmentActivity implements JIProcessEvent, JITaskListener {

    private String mModuleName = "";
    private String mLayoutName = "";
    protected Context mContext;
    protected JTask mActivityTask;
    protected IntentFilter filter = new IntentFilter();
    private JFragmentActivity.Status mStatus;
    protected ArrayList<String> mActivityParameters = new ArrayList<>();
    protected ArrayList<String> mBroadcastParametersInner = new ArrayList<>();
    protected ArrayList<String> mBroadcastParametersProcess = new ArrayList<>();
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (JBroadcastByInner.INTENT_ACTION_EVENT.equals(action)) {
                JFragmentActivity.this.initBroadcastParameterByInner(intent);
                JFragmentActivity.this.processEventByInner(intent);
            } else if (JBroadcastByProcess.INTENT_ACTION_EVENT.equals(action)) {
                JFragmentActivity.this.initBroadcastParameterByProcess(intent);
                JFragmentActivity.this.processEventByProcess(intent);
            }
        }
    };

    public void processEventByInner(Intent intent) {
    }

    public void processEventByProcess(Intent intent) {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        this.mStatus = JFragmentActivity.Status.CREATED;
        this.filter.addAction(JBroadcastByInner.INTENT_ACTION_EVENT);
        this.filter.addAction(JBroadcastByProcess.INTENT_ACTION_EVENT);
        this.filter.setPriority(1000);
        this.registerReceiver(this.mBroadcastReceiver, this.filter);
        this.initActivityParameter(this.getIntent());
        JActivityManager.getInstance().addActivity(this);
        this.getModuleName();
        if (JStringUtil.isEmpty(this.mLayoutName)) {
            this.mLayoutName = this.mContext.getPackageName();
        }

        this.initInjector();
        this.loadDefautLayout();
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        JActivityManager.getInstance().getInjector().injectView(this);
    }

    public void finish() {
        JActivityManager.getInstance().removeActivity(this);
        super.finish();
    }

    protected void onStart() {
        this.mStatus = JFragmentActivity.Status.STARTED;
        super.onStart();
    }

    protected void onResume() {
        this.mStatus = JFragmentActivity.Status.RESUMED;
        super.onResume();
    }

    protected void onPause() {
        this.mStatus = JFragmentActivity.Status.PAUSED;
        super.onPause();
    }

    protected void onStop() {
        this.mStatus = JFragmentActivity.Status.STOPPED;
        super.onStop();
    }

    protected void onDestroy() {
        this.unregisterReceiver(this.mBroadcastReceiver);
        this.mBroadcastReceiver = null;
        if (this.mActivityParameters != null) {
            this.mActivityParameters.clear();
        }

        this.mActivityParameters = null;
        this.stopTask();
        this.mActivityTask = null;
        if (this.mBroadcastParametersInner != null) {
            this.mBroadcastParametersInner.clear();
        }

        this.mBroadcastParametersInner = null;
        if (this.mBroadcastParametersProcess != null) {
            this.mBroadcastParametersProcess.clear();
        }

        this.mBroadcastParametersProcess = null;
        this.mStatus = JFragmentActivity.Status.DESTROYED;
        this.mContext = null;
        super.onDestroy();
    }

    public void onTask(JTask.Task task, JTask.TaskEvent event, Object... params) {
    }

    protected void startTask(int taskID, String... params) {
        if (this.mActivityTask == null) {
            this.mActivityTask = new JTask();
            this.mActivityTask.setIXTaskListener(this);
        }

        this.mActivityTask.startTask(taskID, params);
    }

    protected void stopTask() {
        if (this.mActivityTask != null) {
            this.mActivityTask.stopTask();
        }

    }

    protected void handleMessage(Message msg) {
    }

    private void initInjector() {
        JActivityManager.getInstance().getInjector().injectResource(this);
        JActivityManager.getInstance().getInjector().inject(this);
    }

    private void loadDefautLayout() {
        try {
            int layoutResID = JActivityManager.getInstance().getLayoutLoader().getLayoutID(this.mLayoutName);
            this.setContentView(layoutResID);
        } catch (Exception var2) {
        }

    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        JActivityManager.getInstance().getInjector().injectView(this);
    }

    public void setContentView(View view) {
        super.setContentView(view);
        JActivityManager.getInstance().getInjector().injectView(this);
    }

    public String getModuleName() {
        if (this.mModuleName == null || this.mModuleName.equalsIgnoreCase("")) {
            this.mModuleName = this.getClass().getName().substring(0, this.getClass().getName().length() - 8);
            String[] arrays = this.mModuleName.split("\\.");
            this.mModuleName = this.mModuleName = arrays[arrays.length - 1].toLowerCase();
        }

        return this.mModuleName;
    }

    public void setModuleName(String moduleName) {
        this.mModuleName = moduleName;
    }

    protected void initActivityParameter(Intent intent) {
        if (this.mActivityParameters != null) {
            this.mActivityParameters.clear();
            this.mActivityParameters.add(intent.getStringExtra(JActivityUtil.FIELD_DATA0));
            this.mActivityParameters.add(intent.getStringExtra(JActivityUtil.FIELD_DATA1));
            this.mActivityParameters.add(intent.getStringExtra(JActivityUtil.FIELD_DATA2));
            this.mActivityParameters.add(intent.getStringExtra(JActivityUtil.FIELD_DATA3));
            this.mActivityParameters.add(intent.getStringExtra(JActivityUtil.FIELD_DATA4));
            this.mActivityParameters.add(intent.getStringExtra(JActivityUtil.FIELD_DATA5));
        }
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

    protected ArrayList<String> getActivityParameter() {
        return this.mActivityParameters;
    }

    protected ArrayList<String> getBroadcastParameterByInner() {
        return this.mBroadcastParametersInner;
    }

    protected ArrayList<String> getBroadcastParameterByProcess() {
        return this.mBroadcastParametersProcess;
    }

    public JFragmentActivity.Status get_status() {
        return this.mStatus;
    }

    public boolean isActivity() {
        return this.mStatus != JFragmentActivity.Status.DESTROYED
                && this.mStatus != JFragmentActivity.Status.PAUSED
                && this.mStatus != JFragmentActivity.Status.STOPPED;
    }

    protected void showToast(String content) {
        JToastUtil.show(this.mContext, content, 1000);
    }

    public static String getResString(int id) {
        return JApplication.getInstance().getString(id);
    }

    public static enum Status {
        NONE,
        CREATED,
        STARTED,
        RESUMED,
        PAUSED,
        STOPPED,
        DESTROYED

    }
}
