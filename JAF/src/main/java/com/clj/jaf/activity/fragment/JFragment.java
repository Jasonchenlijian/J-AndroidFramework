package com.clj.jaf.activity.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clj.jaf.app.JApplication;
import com.clj.jaf.utils.JHandler;

public class JFragment extends Fragment {

    protected View mThis;
    private JHandler<JFragment> mJHandler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mJHandler = new JHandler(this) {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (this.getOwner() != null && ((JFragment) this.getOwner()).getActivity() != null) {
                    Bundle data = msg.getData();
                    JFragment.this.handleEvent(msg.what, new String[]{data.getString("core0"), data.getString("core1"), data.getString("core2"), data.getString("core3"), data.getString("core4")});
                }
            }
        };
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onDestroy() {
        super.onDestroy();
        this.mJHandler = null;
    }

    public void onDetach() {
        super.onDetach();
    }

    public static String getResString(int id) {
        return JApplication.getInstance().getString(id);
    }

    protected void handleEvent(int id, String... params) {
    }

    public void sentPostEvent(final int second, final int id, final String... params) {
        (new AsyncTask<String, Integer, Boolean>() {
            protected Boolean doInBackground(String... paramsx) {
                try {
                    Thread.sleep((long) (second * 1000));
                } catch (Exception var3) {

                }

                return null;
            }

            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                JFragment.this.sentEvent(id, params);
            }
        }).execute(new String[]{""});
    }

    public void sentEvent(int id, String... params) {
        if (this.mJHandler != null) {
            Message message = new Message();
            if (params != null && params.length > 0) {
                Bundle bundle = new Bundle();

                for (int i = 0; i < params.length; ++i) {
                    bundle.putString("core" + i, params[i]);
                }

                message.setData(bundle);
            }

            message.what = id;
            this.mJHandler.sendMessage(message);
        }
    }
}