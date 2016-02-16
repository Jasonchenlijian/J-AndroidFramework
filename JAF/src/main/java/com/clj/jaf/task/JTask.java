package com.clj.jaf.task;

import android.os.AsyncTask;
import android.util.Log;

import com.clj.jaf.utils.JAndroidUtil;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class JTask {
    private static final String TAG = JTask.class.getCanonicalName();
    protected JTask.Task mTask;
    protected JITaskListener mIListener;
    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(30);
    private static int Update = 1;

    public JTask() {
    }

    public boolean equalTask(JTask.Task task) {
        return this.mTask == task;
    }

    public JTask.Task getTask() {
        return this.mTask;
    }

    public void newTask1(int TaskId, String... params) {
        this.stopTask();
        this.mTask = new JTask.Task(TaskId, params);
    }

    public void executeTask1(String... params) {
        if (JAndroidUtil.isHoneycombOrHigher()) {
            this.mTask.executeOnExecutor(mExecutorService, new String[]{""});
        } else {
            this.mTask.execute(new String[]{""});
        }

    }

    public void startTask(int TaskId) {
        this.stopTask();
        this.mTask = null;
        this.mTask = new JTask.Task(TaskId, new String[0]);
        if (JAndroidUtil.isHoneycombOrHigher()) {
            this.mTask.executeOnExecutor(mExecutorService, new String[]{""});
        } else {
            this.mTask.execute(new String[]{""});
        }

    }

    public void startTask(int TaskId, String... params) {
        this.stopTask();
        this.mTask = new JTask.Task(TaskId, params);
        if (JAndroidUtil.isHoneycombOrHigher()) {
            this.mTask.executeOnExecutor(mExecutorService, params);
        } else {
            this.mTask.execute(params);
        }

    }

    public void startTask(String... params) {
        this.stopTask();
        this.mTask = new JTask.Task(0, params);
        if (JAndroidUtil.isHoneycombOrHigher()) {
            this.mTask.executeOnExecutor(mExecutorService, params);
        } else {
            this.mTask.execute(params);
        }

    }

    public void stopTask() {
        if (this.mTask != null) {
            this.mTask.stopTask();
        }

    }

    public boolean isTasking() {
        return this.mTask != null && this.mTask.getStatus() == AsyncTask.Status.RUNNING;
    }

    public void setIXTaskListener(JITaskListener listener) {
        this.mIListener = listener;
    }

    public class Task extends AsyncTask<String, Integer, Boolean> {
        protected String mErrorString = "";
        protected JTask.Task mThis;
        private int mTaskId = 0;
        private boolean mBCancel = false;
        private ArrayList<String> mParameters = new ArrayList<>();
        private Object mResultObject;

        public Task(int taskId, String... params) {
            this.mTaskId = taskId;
            this.mThis = this;
            if (params != null) {
                for (int i = 0; i < params.length; ++i) {
                    this.mParameters.add(params[i]);
                }
            }
        }

        protected Boolean doInBackground(String... params) {
            boolean result = false;
            if (this.mBCancel) {
                return false;
            } else {
                if (JTask.this.mIListener != null) {
                    JTask.this.mIListener.onTask(this, JTask.TaskEvent.Work, new Object[]{params});
                }
                return true;
            }
        }

        protected void onPreExecute() {
            if (!this.mBCancel) {
                super.onPreExecute();
                this.mResultObject = null;
                if (JTask.this.mIListener != null) {
                    JTask.this.mIListener.onTask(this, JTask.TaskEvent.Before, new Object[0]);
                }
            }
        }

        protected void onPostExecute(Boolean result) {
            if (!this.mBCancel) {
                super.onPostExecute(result);
                if (JTask.this.mIListener != null) {
                    JTask.this.mIListener.onTask(this, JTask.TaskEvent.Cancel, new Object[]{result});
                }

                if (this.mErrorString != null && !this.mErrorString.equals("")) {
                    Log.i(JTask.TAG, this.getTaskId() + this.mErrorString);
                }
            }
        }

        protected void onCancelled() {
            super.onCancelled();
            this.mBCancel = true;
            if (JTask.this.mIListener != null) {
                JTask.this.mIListener.onTask(this, JTask.TaskEvent.Cancel, new Object[]{Boolean.valueOf(false)});
            }

        }

        protected void onProgressUpdate(Integer... values) {
            if (!this.mBCancel) {
                super.onProgressUpdate(values);
                if (JTask.this.mIListener != null) {
                    JTask.this.mIListener.onTask(this, JTask.TaskEvent.Update, new Object[]{values});
                }
            }
        }

        public void publishTProgress(int values) {
            JTask.this.mTask.publishProgress(new Integer[]{values});
        }

        public ArrayList<String> getParameter() {
            return this.mParameters;
        }

        public void stopTask() {
            this.mParameters.clear();
            this.mBCancel = true;
            this.cancel(true);
        }

        public void setResultObject(Object object) {
            this.mResultObject = object;
        }

        public Object getResultObject() {
            return this.mResultObject;
        }

        public void setError(String error) {
            this.mErrorString = error;
        }

        public String getError() {
            return this.mErrorString;
        }

        public void setTaskId(int taskId) {
            this.mTaskId = taskId;
        }

        public int getTaskId() {
            return this.mTaskId;
        }

        public boolean isCancel() {
            return this.mBCancel;
        }
    }

    public static enum TaskEvent {
        Before,
        Update,
        Cancel,
        Work, TaskEvent;
    }
}
