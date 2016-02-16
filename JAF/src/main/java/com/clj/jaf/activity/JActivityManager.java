package com.clj.jaf.activity;

import android.app.Activity;

import com.clj.jaf.activity.layoutloader.JILayoutLoader;
import com.clj.jaf.activity.layoutloader.JLayoutLoader;

import java.util.Iterator;
import java.util.Stack;

public class JActivityManager {
    private static Stack<Activity> mActivityStack = new Stack<>();
    private static JActivityManager mThis;
    protected JActivity mCurrentActivity;
    protected JILayoutLoader mLayoutLoader;
    protected JInjector mInjector;

    private JActivityManager() {
    }

    public static JActivityManager getInstance() {
        if (mThis == null) {
            mThis = new JActivityManager();
        }

        return mThis;
    }

    public int getSizeOfActivityStack() {
        return mActivityStack == null ? 0 : mActivityStack.size();
    }

    public void addActivity(Activity activity) {
        mActivityStack.add(activity);
    }

    public Activity currentActivity() {
        Activity activity = mActivityStack.lastElement();
        return activity;
    }

    public void finishActivity() {
        Activity activity = mActivityStack.lastElement();
        this.finishActivity(activity);
    }

    public void finishActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
            activity.finish();
            activity = null;
        }

    }

    public void removeActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
            activity = null;
        }

    }

    public void finishActivity(Class<?> cls) {
        Iterator var3 = mActivityStack.iterator();

        while (var3.hasNext()) {
            Activity activity = (Activity) var3.next();
            if (activity.getClass().equals(cls)) {
                this.finishActivity(activity);
            }
        }

    }

    public void finishAllActivity() {
        int i = 0;

        for (int size = mActivityStack.size(); i < size; ++i) {
            if (mActivityStack.get(i) != null) {
                (mActivityStack.get(i)).finish();
            }
        }

        mActivityStack.clear();
    }

    public void back() {
    }

    public boolean hasActivity(Class<?> cls) {
        Iterator var3 = mActivityStack.iterator();

        while (var3.hasNext()) {
            Activity activity = (Activity) var3.next();
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }

        return false;
    }

    public JILayoutLoader getLayoutLoader() {
        if (this.mLayoutLoader == null) {
            this.mLayoutLoader = JLayoutLoader.getInstance();
        }

        return this.mLayoutLoader;
    }

    public void setLayoutLoader(JILayoutLoader layoutLoader) {
        this.mLayoutLoader = layoutLoader;
    }

    public JInjector getInjector() {
        if (this.mInjector == null) {
            this.mInjector = JInjector.getInstance();
        }

        return this.mInjector;
    }

    public void setInjector(JInjector injector) {
        this.mInjector = injector;
    }
}
