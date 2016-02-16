package com.clj.jaf.http.core;

import android.util.Log;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpResponseException;

public abstract class BinaryHttpResponseHandler extends AsyncHttpResponseHandler {
    private static final String TAG = BinaryHttpResponseHandler.class.getSimpleName();
    private String[] mAllowedContentTypes = new String[]{"image/jpeg", "image/png"};

    public String[] getAllowedContentTypes() {
        return this.mAllowedContentTypes;
    }

    public BinaryHttpResponseHandler() {
    }

    public BinaryHttpResponseHandler(String[] allowedContentTypes) {
        if (allowedContentTypes != null) {
            this.mAllowedContentTypes = allowedContentTypes;
        } else {
            Log.e(TAG, "Constructor passed allowedContentTypes was null !");
        }

    }

    public abstract void onSuccess(int var1, Header[] var2, byte[] var3);

    public abstract void onFailure(int var1, Header[] var2, byte[] var3, Throwable var4);

    public final void sendResponseMessage(HttpResponse response) throws IOException {
        StatusLine status = response.getStatusLine();
        Header[] contentTypeHeaders = response.getHeaders("Content-Type");
        if (contentTypeHeaders.length != 1) {
            this.sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), (byte[]) null, new HttpResponseException(status.getStatusCode(), "None, or more than one, Content-Type Header found!"));
        } else {
            Header contentTypeHeader = contentTypeHeaders[0];
            boolean foundAllowedContentType = false;
            String[] var9;
            int var8 = (var9 = this.getAllowedContentTypes()).length;

            for (int var7 = 0; var7 < var8; ++var7) {
                String anAllowedContentType = var9[var7];

                try {
                    if (Pattern.matches(anAllowedContentType, contentTypeHeader.getValue())) {
                        foundAllowedContentType = true;
                    }
                } catch (PatternSyntaxException var11) {
                    Log.e("BinaryHttpRH", "Given pattern is not valid: " + anAllowedContentType, var11);
                }
            }

            if (!foundAllowedContentType) {
                this.sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), (byte[]) null, new HttpResponseException(status.getStatusCode(), "Content-Type not allowed!"));
            } else {
                super.sendResponseMessage(response);
            }
        }
    }
}
