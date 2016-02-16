package com.clj.jaf.storage;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.clj.jaf.app.JApplication;

import java.io.File;

public class JFilePath {
    public static final String PATH_IMAGE = "image";
    public static final String PATH_AUDIO = "audio";
    public static final String PATH_VIDEO = "video";
    public static final String PATH_DOWNLOAD = "download";
    public static final String PATH_CACHE = "cache";
    public static final String PATH_SPLIT;
    protected String mAppName = "";

    static {
        PATH_SPLIT = File.separator;
    }

    public JFilePath() {
        this.mAppName = JApplication.getInstance().getApplicationContext().getPackageName();
        boolean result = false;
        if (!this.getExternalStorage().isDirectoryExists(this.getExternalAppDir())) {
            result = this.getExternalStorage().createDirectory(this.mAppName);
        }

        if (!this.getExternalStorage().isDirectoryExists(this.getExternalImageDir())) {
            result = this.getExternalStorage().createDirectory(this.mAppName + PATH_SPLIT + "image");
        }

        if (!this.getExternalStorage().isDirectoryExists(this.getExternalAudioDir())) {
            result = this.getExternalStorage().createDirectory(this.mAppName + PATH_SPLIT + "audio");
        }

        if (!this.getExternalStorage().isDirectoryExists(this.getExternalVideoDir())) {
            result = this.getExternalStorage().createDirectory(this.mAppName + PATH_SPLIT + "video");
        }

        if (!this.getExternalStorage().isDirectoryExists(this.getExternalDownloadDir())) {
            result = this.getExternalStorage().createDirectory(this.mAppName + PATH_SPLIT + "download");
        }

        if (!this.getExternalStorage().isDirectoryExists(this.getExternalCacheDir())) {
            result = this.getExternalStorage().createDirectory(this.mAppName + PATH_SPLIT + "cache");
        }

        result = this.getInternalStorage().createDirectory("image");
        result = this.getInternalStorage().createDirectory("audio");
        result = this.getInternalStorage().createDirectory("video");
        result = this.getInternalStorage().createDirectory("download");
        result = this.getInternalStorage().createDirectory("cache");
    }

    public InternalStorage getInternalStorage() {
        return JStorage.getInstance().getInternalStorage(JApplication.getInstance());
    }

    public ExternalStorage getExternalStorage() {
        return JStorage.getInstance().getExternalStorage();
    }

    public String getImageDir() {
        return this.mAppName + PATH_SPLIT + "image";
    }

    public String getExternalImageDir() {
        return this.getExternalAppDir() + PATH_SPLIT + "image";
    }

    public String getAudioDir() {
        return this.mAppName + PATH_SPLIT + "audio";
    }

    public String getExternalAudioDir() {
        return this.getExternalAppDir() + PATH_SPLIT + "audio";
    }

    public String getVideoDir() {
        return this.mAppName + PATH_SPLIT + "video";
    }

    public String getExternalVideoDir() {
        return this.getExternalAppDir() + PATH_SPLIT + "video";
    }

    public String getDownloadDir() {
        return this.mAppName + PATH_SPLIT + "download";
    }

    public String getExternalDownloadDir() {
        return this.getExternalAppDir() + PATH_SPLIT + "download";
    }

    public String getCacheDir() {
        return this.mAppName + PATH_SPLIT + "cache";
    }

    public String getExternalCacheDir() {
        return this.getExternalAppDir() + PATH_SPLIT + "cache";
    }

    public String getAppDir() {
        return this.mAppName;
    }

    public String getExternalAppDir() {
        return JStorage.getInstance().getExternalStorage().getPath() + PATH_SPLIT + this.mAppName;
    }

    public static File getCacheDirectory(Context context) {
        return getDirByName(context, "cache", true);
    }

    public static File getImageDirectory(Context context) {
        return getDirByName(context, "image", true);
    }

    public static File getVideoDirectory(Context context) {
        return getDirByName(context, "video", true);
    }

    public static File getDownloadDirectory(Context context) {
        new JFilePath();
        return getExternalDirByName(context, "download");
    }

    public static File getDirByName(Context context, String dirName, boolean preferExternal) {
        File appCacheDir = null;
        JFilePath filePath = new JFilePath();

        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException | IncompatibleClassChangeError var7) {
            externalStorageState = "";
        }

        if (preferExternal && "mounted".equals(externalStorageState)
                && JStorageUtils.hasExternalStoragePermission(context)) {
            appCacheDir = getExternalDirByName(context, dirName);
        }

        if (appCacheDir == null) {
            appCacheDir = filePath.getInternalStorage().getFile(dirName);
        }

        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/" + dirName + "/";
            Log.w("JFilePath", "Can\'t define system cache directory! \'%s\' will be used." + cacheDirPath);
            appCacheDir = new File(cacheDirPath);
        }

        return appCacheDir;
    }

    private static File getExternalDirByName(Context context, String dirName) {
        JFilePath filePath = new JFilePath();
        File appDir = new File(filePath.getExternalAppDir(), dirName);
        if (!appDir.exists() && !appDir.mkdirs()) {
            Log.w("JFilePath", "Unable to create external cache directory");
            return null;
        } else {
            return appDir;
        }
    }

    @SuppressLint({"NewApi"})
    public static String get4_4Path(Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= 19;
        if (isKitKat && DocumentsContract.isDocumentUri(JApplication.getInstance(), uri)) {
            String docId;
            String[] split;
            String type;
            if (isExternalStorageDocument(uri)) {
                docId = DocumentsContract.getDocumentId(uri);
                split = docId.split(":");
                type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else {
                if (isDownloadsDocument(uri)) {
                    docId = DocumentsContract.getDocumentId(uri);
                    Uri split1 = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    return getDataColumn(JApplication.getInstance(), split1, null, null);
                }

                if (isMediaDocument(uri)) {
                    docId = DocumentsContract.getDocumentId(uri);
                    split = docId.split(":");
                    type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(JApplication.getInstance(), contentUri, "_id=?", selectionArgs);
                }
            }
        } else {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }

                return getDataColumn(JApplication.getInstance(), uri, null, null);
            }

            if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = new String[]{"_data"};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, (String) null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow("_data");
                String var9 = cursor.getString(index);
                return var9;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
