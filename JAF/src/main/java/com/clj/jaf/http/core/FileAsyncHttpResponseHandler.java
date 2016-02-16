package com.clj.jaf.http.core;

import android.util.Log;

import com.clj.jaf.app.JApplication;
import com.clj.jaf.http.JAsyncHttpClient;
import com.clj.jaf.storage.JFilePath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

public abstract class FileAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
    protected final File mFile;
    private static final String TAG = FileAsyncHttpResponseHandler.class.getSimpleName();

    public FileAsyncHttpResponseHandler(File file) {
        assert file != null;

        this.mFile = file;
    }

    public FileAsyncHttpResponseHandler(String fileName) {
        this.mFile = this.getTemporaryFile(fileName);
    }

    public boolean deleteTargetFile() {
        return this.getTargetFile() != null && this.getTargetFile().delete();
    }

    protected File getTemporaryFile(String fileName) {
        try {
            return new File(JFilePath.getDownloadDirectory(JApplication.getInstance()), fileName);
        } catch (Throwable var3) {
            Log.e(TAG, "Cannot create temporary file" + var3.getMessage());
            return null;
        }
    }

    protected File getTargetFile() {
        assert this.mFile != null;

        return this.mFile;
    }

    public final void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
        this.onFailure(statusCode, headers, throwable, this.getTargetFile());
    }

    public abstract void onFailure(int var1, Header[] var2, Throwable var3, File var4);

    public final void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
        this.onSuccess(statusCode, headers, this.getTargetFile());
    }

    public abstract void onSuccess(int var1, Header[] var2, File var3);

    protected byte[] getResponseData(HttpEntity entity) throws IOException {
        if (entity != null) {
            InputStream inStream = entity.getContent();
            long contentLength = entity.getContentLength();
            FileOutputStream buffer = new FileOutputStream(this.getTargetFile());
            if (inStream != null) {
                try {
                    byte[] tmp = new byte[4096];
                    int count = 0;

                    int l;
                    while ((l = inStream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                        count += l;
                        buffer.write(tmp, 0, l);
                        this.sendProgressMessage(count, (int) contentLength);
                    }
                } finally {
                    JAsyncHttpClient.silentCloseInputStream(inStream);
                    buffer.flush();
                    JAsyncHttpClient.silentCloseOutputStream(buffer);
                }
            }
        }

        return null;
    }
}
