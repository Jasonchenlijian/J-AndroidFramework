package com.clj.jaf.activity.layoutloader;

import android.content.pm.PackageManager;

import com.clj.jaf.exception.JNoSuchNameLayoutException;

public interface JILayoutLoader {
    int getLayoutID(String var1) throws ClassNotFoundException,
            IllegalArgumentException,
            IllegalAccessException,
            PackageManager.NameNotFoundException,
            JNoSuchNameLayoutException;
}
