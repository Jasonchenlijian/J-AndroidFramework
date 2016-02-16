package com.clj.jaf.storage;

import android.os.Environment;

import java.io.File;

public class ExternalStorage extends AbstractDiskStorage {
    ExternalStorage() {
    }

    public JStorage.StorageType getStorageType() {
        return JStorage.StorageType.EXTERNAL;
    }

    public boolean isWritable() {
        String state = Environment.getExternalStorageState();
        return "mounted".equals(state);
    }

    protected String buildAbsolutePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public String getPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    protected String buildPath(String name) {
        String path = this.buildAbsolutePath();
        path = path + File.separator + name;
        return path;
    }

    protected String buildPath(String directoryName, String fileName) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path = path + File.separator + directoryName + File.separator + fileName;
        return path;
    }
}
