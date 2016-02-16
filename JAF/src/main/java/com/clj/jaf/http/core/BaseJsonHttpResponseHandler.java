package com.clj.jaf.http.core;

import android.util.Log;

import cz.msebera.android.httpclient.Header;

public abstract class BaseJsonHttpResponseHandler<JSON_TYPE> extends TextHttpResponseHandler {
    private static final String TAG = BaseJsonHttpResponseHandler.class.getSimpleName();

    public BaseJsonHttpResponseHandler() {
        this("UTF-8");
    }

    public BaseJsonHttpResponseHandler(String encoding) {
        super(encoding);
    }

    public abstract void onSuccess(int var1, Header[] var2, String var3, JSON_TYPE var4);

    public abstract void onFailure(int var1, Header[] var2, Throwable var3, String var4, JSON_TYPE var5);

    public final void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
        if (statusCode != 204) {
            Runnable parser = new Runnable() {
                public void run() {
                    try {
                        final Object t = BaseJsonHttpResponseHandler.this.parseResponse(responseString, false);
                        BaseJsonHttpResponseHandler.this.postRunnable(new Runnable() {
                            public void run() {
                                BaseJsonHttpResponseHandler.this.onSuccess(statusCode, headers, responseString, (JSON_TYPE) t);
                            }
                        });
                    } catch (final Throwable var2) {
                        Log.d(BaseJsonHttpResponseHandler.TAG, "parseResponse thrown an problem" + var2.getMessage());
                        BaseJsonHttpResponseHandler.this.postRunnable(new Runnable() {
                            public void run() {
                                BaseJsonHttpResponseHandler.this.onFailure(statusCode, headers, var2, responseString, null);
                            }
                        });
                    }

                }
            };
            if (!this.getUseSynchronousMode()) {
                (new Thread(parser)).start();
            } else {
                parser.run();
            }
        } else {
            this.onSuccess(statusCode, headers, null, null);
        }

    }

    public final void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable) {
        if (responseString != null) {
            Runnable parser = new Runnable() {
                public void run() {
                    try {
                        final Object t = BaseJsonHttpResponseHandler.this.parseResponse(responseString, true);
                        BaseJsonHttpResponseHandler.this.postRunnable(new Runnable() {
                            public void run() {
                                BaseJsonHttpResponseHandler.this.onFailure(statusCode, headers, throwable, responseString, (JSON_TYPE) t);
                            }
                        });
                    } catch (Throwable var2) {
                        Log.d(BaseJsonHttpResponseHandler.TAG, "parseResponse thrown an problem" + var2.getMessage());
                        BaseJsonHttpResponseHandler.this.postRunnable(new Runnable() {
                            public void run() {
                                BaseJsonHttpResponseHandler.this.onFailure(statusCode, headers, throwable, responseString, null);
                            }
                        });
                    }

                }
            };
            if (!this.getUseSynchronousMode()) {
                (new Thread(parser)).start();
            } else {
                parser.run();
            }
        } else {
            this.onFailure(statusCode, headers, throwable, null, null);
        }

    }

    protected abstract JSON_TYPE parseResponse(String var1, boolean var2) throws Throwable;
}
