package com.clj.jaf.storage;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.clj.jaf.utils.JAndroidUtil;

import java.io.File;

public class JStorageUtils {
    public static String TAG = JStorageUtils.class.getSimpleName();
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    public JStorageUtils() {
    }

    public static boolean isExternalStorageWrittenable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static boolean checkAvailableStorage() {
        Log.d(TAG, "checkAvailableStorage E");
        return getAvailableStorage() >= 10485760L;
    }

    public static boolean isExternalStoragePresent() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static boolean isExternalStorageRemovable() {
        return !JAndroidUtil.isGingerbreadOrHigher() || Environment.isExternalStorageRemovable();
    }

    public static long getAvailableStorage() {
        String storageDirectory = null;
        storageDirectory = Environment.getExternalStorageDirectory().toString();
        Log.v(TAG, "getAvailableStorage. storageDirectory : " + storageDirectory);

        try {
            StatFs ex = new StatFs(storageDirectory);
            long avaliableSize = (long) ex.getAvailableBlocks() * (long) ex.getBlockSize();
            Log.v(TAG, "getAvailableStorage. avaliableSize : " + avaliableSize);
            return avaliableSize;
        } catch (RuntimeException var4) {
            Log.e(TAG, "getAvailableStorage - exception. return 0");
            return 0L;
        }
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = (long) stat.getBlockSize();
        long availableBlocks = (long) stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = (long) stat.getBlockSize();
        long totalBlocks = (long) stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static String getDeviceId(Context context) {
        try {
            TelephonyManager e = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return e.getDeviceId();
        } catch (RuntimeException var2) {
            Log.w(TAG, "Couldn\'t retrieve DeviceId for : " + context.getPackageName() + var2.getMessage());
            return null;
        }
    }

    public static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return perm == 0;
    }
}
