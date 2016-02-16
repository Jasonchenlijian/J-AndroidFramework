package com.clj.jaf.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Process;

import com.clj.jaf.app.JApplication;

/**
 * 同一个进程内发送的广播
 */
public class JBroadcastByInner {

    public static String TAG = JBroadcastByInner.class.getCanonicalName();
    public static final String INTENT_ACTION_EVENT;
    public static final String MAINEVENT = "mainevent";
    public static final String EVENT = "event";
    public static final String MESSAGE = "message";
    public static final String MESSAGE0 = "message0";
    public static final String MESSAGE1 = "message1";
    public static final String MESSAGE2 = "message2";
    public static final String MESSAGE3 = "message3";
    public static final String MESSAGE4 = "message4";
    public static final String MESSAGE5 = "message5";

    static {
        INTENT_ACTION_EVENT = TAG + ".INTENT_ACTION_EVENT" + "." + Process.myPid();
    }

    public JBroadcastByInner() {
    }

    public static void sentEvent(int mainEvent, String... message) {
        sentEvent(mainEvent, 0, message);
    }

    public static void sentEvent(int mainEvent, int event, String... message) {
        Intent intent = new Intent(INTENT_ACTION_EVENT);
        intent.putExtra("mainevent", mainEvent);
        intent.putExtra("event", event);
        if(message != null) {
            for(int i = 0; i < message.length; ++i) {
                intent.putExtra(String.format("message%d", new Object[]{Integer.valueOf(i)}), message[i]);
            }
        }

        JApplication.getInstance().sendBroadcast(intent);
    }

    public static void sentPostEvent(final int mainEvent, final int event, final int second, final String... message) {
        (new AsyncTask<String, Integer, Boolean>() {
            protected Boolean doInBackground(String... params) {
                try {
                    Thread.sleep((long)(second * 1000));
                } catch (Exception var3) {

                }
                return true;
            }

            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                JBroadcastByInner.sentEvent(mainEvent, event, message);
            }
        }).execute(new String[]{""});
    }

    public static void sentPostEvent(int mainEvent, int second, String... message) {
        sentPostEvent(mainEvent, 0, second, message);
    }

    public static void registerBroadcast(Context context, BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_ACTION_EVENT);
        filter.setPriority(1000);
        context.registerReceiver(receiver, filter);
    }

    public static void removeBroadcast(Context context, BroadcastReceiver receiver) {
        context.unregisterReceiver(receiver);
    }

}
