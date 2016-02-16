package com.clj.jaf.activity.layoutloader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.clj.jaf.app.JApplication;
import com.clj.jaf.exception.JNoSuchNameLayoutException;

import java.lang.reflect.Field;

public class JLayoutLoader implements JILayoutLoader {
    private static String TAG = JLayoutLoader.class.getSimpleName();
    private static JLayoutLoader instance;
    private Context mContext;

    private JLayoutLoader(Context context) {
        this.mContext = context;
    }

    public static JLayoutLoader getInstance() {
        if (instance == null) {
            instance = new JLayoutLoader(JApplication.getInstance().getApplicationContext());
        }

        return instance;
    }

    public int getLayoutID(String resIDName) throws PackageManager.NameNotFoundException,
            ClassNotFoundException,
            IllegalArgumentException,
            IllegalAccessException,
            JNoSuchNameLayoutException {
        int resID = this.readResID("layout", resIDName);
        if (resID == 0) {
            throw new JNoSuchNameLayoutException();
        } else {
            return resID;
        }
    }

    public int readResID(String type, String resIDName) throws PackageManager.NameNotFoundException,
            ClassNotFoundException,
            IllegalArgumentException,
            IllegalAccessException {
        PackageManager pm = this.mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(this.mContext.getPackageName(), 0);
        String packageName = pi.packageName;
        if (packageName != null && !packageName.equalsIgnoreCase("")) {
            packageName = packageName + ".R";
            Class clazz = Class.forName(packageName);
            Class cls = this.readResClass(clazz, packageName + "$" + type);
            if (cls == null) {
                throw new PackageManager.NameNotFoundException("类名为空");
            } else {
                return this.readResID(cls, resIDName);
            }
        } else {
            throw new PackageManager.NameNotFoundException("包名为空");
        }
    }

    public Class<?> readResClass(Class<?> cls, String respackageName) {
        Class[] classes = cls.getDeclaredClasses();

        for (int i = 0; i < classes.length; ++i) {
            Class tempClass = classes[i];
            Log.v(TAG, tempClass.getName());
            if (tempClass.getName().equalsIgnoreCase(respackageName)) {
                return tempClass;
            }
        }

        return null;
    }

    public int readResID(Class<?> cls, String resIDName) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = cls.getDeclaredFields();

        for (int j = 0; j < fields.length; ++j) {
            if (fields[j].getName().equalsIgnoreCase(resIDName)) {
                return fields[j].getInt(cls);
            }
        }

        return 0;
    }
}
