package com.clj.jaf.http.core;

import android.os.Message;
import android.util.Log;

import com.clj.jaf.http.JAsyncHttpClient;

import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.util.ByteArrayBuffer;

public abstract class DataAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
    private static final String TAG = DataAsyncHttpResponseHandler.class.getSimpleName();
    protected static final int PROGRESS_DATA_MESSAGE = 6;

    public DataAsyncHttpResponseHandler() {
    }

    public void onProgressData(byte[] responseBody) {
    }

    public final void sendProgressDataMessage(byte[] responseBytes) {
        this.sendMessage(this.obtainMessage(6, new Object[]{responseBytes}));
    }

    protected void handleMessage(Message message) {
        super.handleMessage(message);
        switch (message.what) {
            case 6:
                Object[] response = (Object[]) message.obj;
                if (response != null && response.length >= 1) {
                    try {
                        this.onProgressData((byte[]) response[0]);
                    } catch (Throwable var4) {
                        Log.e(TAG, "custom onProgressData contains an error" + var4.getMessage());
                    }
                } else {
                    Log.e(TAG, "PROGRESS_DATA_MESSAGE didn\'t got enough params");
                }
            default:
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

                if (contentLength < 0L) {
                    contentLength = 4096L;
                }

                try {
                    ByteArrayBuffer e = new ByteArrayBuffer((int) contentLength);

                    try {
                        byte[] tmp = new byte[4096];

                        int l;
                        while ((l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                            e.append(tmp, 0, l);
                            this.sendProgressDataMessage(copyOfRange(tmp, 0, l));
                        }
                    } finally {
                        JAsyncHttpClient.silentCloseInputStream(instream);
                    }

                    responseBody = e.toByteArray();
                } catch (OutOfMemoryError var13) {
                    System.gc();
                    throw new IOException("File too large to fit into available memory");
                }
            }
        }

        return responseBody;
    }

    public static byte[] copyOfRange(byte[] original, int start, int end) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, NullPointerException {
        if (start > end) {
            throw new IllegalArgumentException();
        } else {
            int originalLength = original.length;
            if (start >= 0 && start <= originalLength) {
                int resultLength = end - start;
                int copyLength = Math.min(resultLength, originalLength - start);
                byte[] result = new byte[resultLength];
                System.arraycopy(original, start, result, 0, copyLength);
                return result;
            } else {
                throw new ArrayIndexOutOfBoundsException();
            }
        }
    }
}
