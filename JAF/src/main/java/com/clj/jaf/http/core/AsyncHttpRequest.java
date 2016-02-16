package com.clj.jaf.http.core;

import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpRequestRetryHandler;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.impl.client.AbstractHttpClient;
import cz.msebera.android.httpclient.protocol.HttpContext;

public class AsyncHttpRequest implements Runnable {
    private static String TAG = AsyncHttpRequest.class.getSimpleName();
    private final AbstractHttpClient mClient;
    private final HttpContext mContext;
    private final HttpUriRequest mRequest;
    private final ResponseHandlerInterface mResponseHandler;
    private int mExecutionCount;
    private boolean mIsCancelled = false;
    private boolean mCancelIsNotified = false;
    private boolean mIsFinished = false;

    public AsyncHttpRequest(AbstractHttpClient client, HttpContext context, HttpUriRequest request, ResponseHandlerInterface responseHandler) {
        this.mClient = client;
        this.mContext = context;
        this.mRequest = request;
        this.mResponseHandler = responseHandler;
    }

    public void run() {
        if (!this.isCancelled()) {
            if (this.mResponseHandler != null) {
                this.mResponseHandler.sendStartMessage();
            }

            if (!this.isCancelled()) {
                try {
                    this.makeRequestWithRetries();
                } catch (IOException var2) {
                    if (!this.isCancelled() && this.mResponseHandler != null) {
                        this.mResponseHandler.sendFailureMessage(0, null, null, var2);
                    } else {
                        Log.e(TAG, "makeRequestWithRetries returned error, but handler is null" + var2.getMessage());
                    }
                }

                if (!this.isCancelled()) {
                    if (this.mResponseHandler != null) {
                        this.mResponseHandler.sendFinishMessage();
                    }

                    this.mIsFinished = true;
                }
            }
        }
    }

    private void makeRequest() throws IOException {
        if (!this.isCancelled()) {
            if (this.mRequest.getURI().getScheme() == null) {
                throw new MalformedURLException("No valid URI scheme was provided");
            } else {
                HttpResponse response = this.mClient.execute(this.mRequest, this.mContext);
                if (!this.isCancelled() && this.mResponseHandler != null) {
                    this.mResponseHandler.sendResponseMessage(response);
                }

            }
        }
    }

    private void makeRequestWithRetries() throws IOException {
        boolean retry = true;
        IOException cause = null;
        HttpRequestRetryHandler retryHandler = this.mClient.getHttpRequestRetryHandler();

        while (true) {
            try {
                if (retry) {
                    try {
                        this.makeRequest();
                        return;
                    } catch (UnknownHostException var5) {
                        cause = new IOException("UnknownHostException exception: " + var5.getMessage());
                        retry = this.mExecutionCount > 0 && retryHandler.retryRequest(cause, ++this.mExecutionCount, this.mContext);
                    } catch (NullPointerException var6) {
                        cause = new IOException("NPE in HttpClient: " + var6.getMessage());
                        retry = retryHandler.retryRequest(cause, ++this.mExecutionCount, this.mContext);
                    } catch (IOException var7) {
                        if (this.isCancelled()) {
                            return;
                        }

                        cause = var7;
                        retry = retryHandler.retryRequest(var7, ++this.mExecutionCount, this.mContext);
                    }

                    if (retry && this.mResponseHandler != null) {
                        this.mResponseHandler.sendRetryMessage(this.mExecutionCount);
                    }
                    continue;
                }
            } catch (Exception var8) {
                Log.e(TAG, "Unhandled exception origin cause" + var8.getMessage());
                cause = new IOException("Unhandled exception: " + var8.getMessage());
            }

            throw cause;
        }
    }

    public boolean isCancelled() {
        if (this.mIsCancelled) {
            this.sendCancelNotification();
        }

        return this.mIsCancelled;
    }

    private synchronized void sendCancelNotification() {
        if (!this.mIsFinished && this.mIsCancelled && !this.mCancelIsNotified) {
            this.mCancelIsNotified = true;
            if (this.mResponseHandler != null) {
                this.mResponseHandler.sendCancelMessage();
            }
        }

    }

    public boolean isDone() {
        return this.isCancelled() || this.mIsFinished;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        this.mIsCancelled = true;
        this.mRequest.abort();
        return this.isCancelled();
    }
}