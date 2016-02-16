package com.clj.jaf.http.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.clj.jaf.http.JAsyncHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpResponseException;
import cz.msebera.android.httpclient.util.ByteArrayBuffer;

public abstract class AsyncHttpResponseHandler implements ResponseHandlerInterface {
    private static final String LOG_TAG = "AsyncHttpResponseHandler";
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;
    protected static final int PROGRESS_MESSAGE = 4;
    protected static final int RETRY_MESSAGE = 5;
    protected static final int CANCEL_MESSAGE = 6;
    protected static final int BUFFER_SIZE = 4096;
    public static final String DEFAULT_CHARSET = "UTF-8";
    private String mResponseCharset = "UTF-8";
    private Handler mHandler;
    private boolean mUseSynchronousMode;
    private URI mRequestURI = null;
    private Header[] mRequestHeaders = null;

    public URI getRequestURI() {
        return this.mRequestURI;
    }

    public Header[] getRequestHeaders() {
        return this.mRequestHeaders;
    }

    public void setRequestURI(URI requestURI) {
        this.mRequestURI = requestURI;
    }

    public void setRequestHeaders(Header[] requestHeaders) {
        this.mRequestHeaders = requestHeaders;
    }

    public boolean getUseSynchronousMode() {
        return this.mUseSynchronousMode;
    }

    public void setUseSynchronousMode(boolean value) {
        if (!value && Looper.myLooper() == null) {
            value = true;
            Log.w("AsyncHttpRH", "Current thread has not called Looper.prepare(). Forcing synchronous mode.");
        }

        if (!value && this.mHandler == null) {
            this.mHandler = new AsyncHttpResponseHandler.ResponderHandler(this);
        } else if (value && this.mHandler != null) {
            this.mHandler = null;
        }

        this.mUseSynchronousMode = value;
    }

    public void setCharset(String charset) {
        this.mResponseCharset = charset;
    }

    public String getCharset() {
        return this.mResponseCharset == null ? "UTF-8" : this.mResponseCharset;
    }

    public AsyncHttpResponseHandler() {
        this.setUseSynchronousMode(false);
    }

    public void onProgress(int bytesWritten, int totalSize) {
        Log.v("AsyncHttpRH", String.format("Progress %d from %d (%2.0f%%)",
                bytesWritten, totalSize, totalSize > 0 ?
                        (double) bytesWritten * 1.0D / (double) totalSize * 100.0D : -1.0D));
    }

    public void onStart() {
    }

    public void onFinish() {
    }

    public abstract void onSuccess(int var1, Header[] var2, byte[] var3);

    public abstract void onFailure(int var1, Header[] var2, byte[] var3, Throwable var4);

    public void onRetry(int retryNo) {
        Log.d("AsyncHttpRH", String.format("Request retry no. %d", new Object[]{Integer.valueOf(retryNo)}));
    }

    public void onCancel() {
        Log.d("AsyncHttpRH", "Request got cancelled");
    }

    public final void sendProgressMessage(int bytesWritten, int bytesTotal) {
        this.sendMessage(this.obtainMessage(4, new Object[]{Integer.valueOf(bytesWritten), Integer.valueOf(bytesTotal)}));
    }

    public final void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBytes) {
        this.sendMessage(this.obtainMessage(0, new Object[]{Integer.valueOf(statusCode), headers, responseBytes}));
    }

    public final void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable throwable) {
        this.sendMessage(this.obtainMessage(1, new Object[]{Integer.valueOf(statusCode), headers, responseBody, throwable}));
    }

    public final void sendStartMessage() {
        this.sendMessage(this.obtainMessage(2, (Object) null));
    }

    public final void sendFinishMessage() {
        this.sendMessage(this.obtainMessage(3, (Object) null));
    }

    public final void sendRetryMessage(int retryNo) {
        this.sendMessage(this.obtainMessage(5, new Object[]{Integer.valueOf(retryNo)}));
    }

    public final void sendCancelMessage() {
        this.sendMessage(this.obtainMessage(6, (Object) null));
    }

    protected void handleMessage(Message message) {
        Object[] response;
        switch (message.what) {
            case 0:
                response = (Object[]) message.obj;
                if (response != null && response.length >= 3) {
                    this.onSuccess(((Integer) response[0]).intValue(), (Header[]) response[1], (byte[]) response[2]);
                } else {
                    Log.e("AsyncHttpRH", "SUCCESS_MESSAGE didn\'t got enough params");
                }
                break;
            case 1:
                response = (Object[]) message.obj;
                if (response != null && response.length >= 4) {
                    this.onFailure(((Integer) response[0]).intValue(), (Header[]) response[1], (byte[]) response[2], (Throwable) response[3]);
                } else {
                    Log.e("AsyncHttpRH", "FAILURE_MESSAGE didn\'t got enough params");
                }
                break;
            case 2:
                this.onStart();
                break;
            case 3:
                this.onFinish();
                break;
            case 4:
                response = (Object[]) message.obj;
                if (response != null && response.length >= 2) {
                    try {
                        this.onProgress((Integer) response[0], (Integer) response[1]);
                    } catch (Throwable var4) {
                        Log.e("AsyncHttpRH", "custom onProgress contains an error" + var4.getMessage());
                    }
                } else {
                    Log.e("AsyncHttpRH", "PROGRESS_MESSAGE didn\'t got enough params");
                }
                break;
            case 5:
                response = (Object[]) message.obj;
                if (response != null && response.length == 1) {
                    this.onRetry((Integer) response[0]);
                } else {
                    Log.e("AsyncHttpRH", "RETRY_MESSAGE didn\'t get enough params");
                }
                break;
            case 6:
                this.onCancel();
        }

    }

    protected void sendMessage(Message msg) {
        if (!this.getUseSynchronousMode() && this.mHandler != null) {
            if (!Thread.currentThread().isInterrupted()) {
                this.mHandler.sendMessage(msg);
            }
        } else {
            this.handleMessage(msg);
        }

    }

    protected void postRunnable(Runnable runnable) {
        if (runnable != null) {
            if (!this.getUseSynchronousMode() && this.mHandler != null) {
                this.mHandler.post(runnable);
            } else {
                runnable.run();
            }
        }

    }

    protected Message obtainMessage(int responseMessageId, Object responseMessageData) {
        Message msg;
        if (this.mHandler == null) {
            msg = Message.obtain();
            if (msg != null) {
                msg.what = responseMessageId;
                msg.obj = responseMessageData;
            }
        } else {
            msg = Message.obtain(this.mHandler, responseMessageId, responseMessageData);
        }

        return msg;
    }

    public void sendResponseMessage(HttpResponse response) throws IOException {
        if (!Thread.currentThread().isInterrupted()) {
            StatusLine status = response.getStatusLine();
            byte[] responseBody = this.getResponseData(response.getEntity());
            if (!Thread.currentThread().isInterrupted()) {
                if (status.getStatusCode() >= 300) {
                    this.sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), responseBody, new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
                } else {
                    this.sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), responseBody);
                }
            }
        }

    }

    byte[] getResponseData(HttpEntity entity) throws IOException {
        byte[] responseBody = null;
        if (entity != null) {
            InputStream instream = entity.getContent();
            if (instream != null) {
                long contentLength = entity.getContentLength();
                if (contentLength > 2147483647L) {
                    throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                }

                int buffersize = contentLength <= 0L ? 4096 : (int) contentLength;

                try {
                    ByteArrayBuffer e = new ByteArrayBuffer(buffersize);

                    try {
                        byte[] tmp = new byte[4096];
                        int count = 0;

                        int l;
                        while ((l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                            count += l;
                            e.append(tmp, 0, l);
                            this.sendProgressMessage(count, (int) (contentLength <= 0L ? 1L : contentLength));
                        }
                    } finally {
                        JAsyncHttpClient.silentCloseInputStream(instream);
                    }

                    responseBody = e.toByteArray();
                } catch (OutOfMemoryError var15) {
                    System.gc();
                    throw new IOException("File too large to fit into available memory");
                }
            }
        }

        return responseBody;
    }

    private static class ResponderHandler extends Handler {
        private final AsyncHttpResponseHandler mResponder;

        ResponderHandler(AsyncHttpResponseHandler mResponder) {
            this.mResponder = mResponder;
        }

        public void handleMessage(Message msg) {
            this.mResponder.handleMessage(msg);
        }
    }
}
