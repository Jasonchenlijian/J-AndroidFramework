package com.clj.jaf.http.core;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpResponseException;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;

public abstract class RangeFileAsyncHttpResponseHandler extends FileAsyncHttpResponseHandler {
    private static final String TAG = RangeFileAsyncHttpResponseHandler.class.getSimpleName();
    private long mCurrent = 0L;
    private boolean append = false;

    public RangeFileAsyncHttpResponseHandler(File file) {
        super(file);
    }

    public void sendResponseMessage(HttpResponse response) throws IOException {
        if (!Thread.currentThread().isInterrupted()) {
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == 416) {
                if (!Thread.currentThread().isInterrupted()) {
                    this.sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), (byte[]) null);
                }
            } else if (status.getStatusCode() >= 300) {
                if (!Thread.currentThread().isInterrupted()) {
                    this.sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), (byte[]) null, new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
                }
            } else if (!Thread.currentThread().isInterrupted()) {
                Header header = response.getFirstHeader("Content-Range");
                if (header == null) {
                    this.append = false;
                    this.mCurrent = 0L;
                } else {
                    Log.v(TAG, "Content-Rnage: " + header.getValue());
                }

                this.sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), this.getResponseData(response.getEntity()));
            }
        }

    }

    protected byte[] getResponseData(HttpEntity entity) throws IOException {
        if (entity != null) {
            InputStream instream = entity.getContent();
            long contentLength = entity.getContentLength() + this.mCurrent;
            FileOutputStream buffer = new FileOutputStream(this.getTargetFile(), this.append);
            if (instream != null) {
                try {
                    byte[] tmp = new byte[4096];

                    int l;
                    while (this.mCurrent < contentLength && (l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                        this.mCurrent += (long) l;
                        buffer.write(tmp, 0, l);
                        this.sendProgressMessage((int) this.mCurrent, (int) contentLength);
                    }
                } finally {
                    instream.close();
                    buffer.flush();
                    buffer.close();
                }
            }
        }

        return null;
    }

    public void updateRequestHeaders(HttpUriRequest uriRequest) {
        if (this.mFile.exists() && this.mFile.canWrite()) {
            this.mCurrent = this.mFile.length();
        }

        if (this.mCurrent > 0L) {
            this.append = true;
            uriRequest.setHeader("Range", "bytes=" + this.mCurrent + "-");
        }

    }
}
