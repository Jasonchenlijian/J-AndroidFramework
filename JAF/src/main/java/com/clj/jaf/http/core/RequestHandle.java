package com.clj.jaf.http.core;

import java.lang.ref.WeakReference;

public class RequestHandle {
    private final WeakReference<AsyncHttpRequest> mRequest;

    public RequestHandle(AsyncHttpRequest request) {
        this.mRequest = new WeakReference(request);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        AsyncHttpRequest _request = (AsyncHttpRequest) this.mRequest.get();
        return _request == null || _request.cancel(mayInterruptIfRunning);
    }

    public boolean isFinished() {
        AsyncHttpRequest _request = (AsyncHttpRequest) this.mRequest.get();
        return _request == null || _request.isDone();
    }

    public boolean isCancelled() {
        AsyncHttpRequest _request = (AsyncHttpRequest) this.mRequest.get();
        return _request == null || _request.isCancelled();
    }

    public boolean shouldBeGarbageCollected() {
        boolean should = this.isCancelled() || this.isFinished();
        if (should) {
            this.mRequest.clear();
        }

        return should;
    }
}
