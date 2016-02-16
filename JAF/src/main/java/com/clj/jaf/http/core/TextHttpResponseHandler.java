package com.clj.jaf.http.core;

import android.util.Log;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public abstract class TextHttpResponseHandler extends AsyncHttpResponseHandler {
    private static final String TAG = TextHttpResponseHandler.class.getSimpleName();

    public TextHttpResponseHandler() {
        this("UTF-8");
    }

    public TextHttpResponseHandler(String encoding) {
        this.setCharset(encoding);
    }

    public abstract void onFailure(int var1, Header[] var2, String var3, Throwable var4);

    public abstract void onSuccess(int var1, Header[] var2, String var3);

    public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
        this.onSuccess(statusCode, headers, getResponseString(responseBytes, this.getCharset()));
    }

    public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
        this.onFailure(statusCode, headers, getResponseString(responseBytes, this.getCharset()), throwable);
    }

    public static String getResponseString(byte[] stringBytes, String charset) {
        try {
            return stringBytes == null ? null : new String(stringBytes, charset);
        } catch (UnsupportedEncodingException var3) {
            Log.e(TAG, "Encoding response into string failed" + var3.getMessage());
            return null;
        }
    }
}
