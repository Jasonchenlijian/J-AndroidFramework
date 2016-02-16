package com.clj.jaf.utils;

import android.os.Handler;

import java.lang.ref.WeakReference;

public abstract class JHandler<T> extends Handler {
    private WeakReference<T> mOwner;

    public JHandler(T owner) {
        this.mOwner = new WeakReference(owner);
    }

    public T getOwner() {
        return this.mOwner.get();
    }
}
