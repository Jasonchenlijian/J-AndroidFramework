package com.clj.jaf.http.core;

import android.os.SystemClock;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;

import javax.net.ssl.SSLException;

import cz.msebera.android.httpclient.NoHttpResponseException;
import cz.msebera.android.httpclient.client.HttpRequestRetryHandler;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.protocol.HttpContext;

public class RetryHandler implements HttpRequestRetryHandler {
    private static final HashSet<Class<?>> mExceptionWhitelist = new HashSet();
    private static final HashSet<Class<?>> mExceptionBlacklist = new HashSet();
    private final int maxRetries;
    private final int retrySleepTimeMS;

    static {
        mExceptionWhitelist.add(NoHttpResponseException.class);
        mExceptionWhitelist.add(UnknownHostException.class);
        mExceptionWhitelist.add(SocketException.class);
        mExceptionBlacklist.add(InterruptedIOException.class);
        mExceptionBlacklist.add(SSLException.class);
    }

    public RetryHandler(int maxRetries, int retrySleepTimeMS) {
        this.maxRetries = maxRetries;
        this.retrySleepTimeMS = retrySleepTimeMS;
    }

    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        boolean retry = true;
        Boolean b = (Boolean) context.getAttribute("http.request_sent");
        boolean sent = b != null && b.booleanValue();
        if (executionCount > this.maxRetries) {
            retry = false;
        } else if (this.isInList(mExceptionWhitelist, exception)) {
            retry = true;
        } else if (this.isInList(mExceptionBlacklist, exception)) {
            retry = false;
        } else if (!sent) {
            retry = true;
        }

        if (retry) {
            HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute("http.request");
            if (currentReq == null) {
                return false;
            }
        }

        if (retry) {
            SystemClock.sleep((long) this.retrySleepTimeMS);
        } else {
            exception.printStackTrace();
        }

        return retry;
    }

    public static void addClassToWhitelist(Class<?> cls) {
        mExceptionWhitelist.add(cls);
    }

    public static void addClassToBlacklist(Class<?> cls) {
        mExceptionBlacklist.add(cls);
    }

    protected boolean isInList(HashSet<Class<?>> list, Throwable error) {
        Iterator var4 = list.iterator();

        while (var4.hasNext()) {
            Class aList = (Class) var4.next();
            if (aList.isInstance(error)) {
                return true;
            }
        }

        return false;
    }
}
