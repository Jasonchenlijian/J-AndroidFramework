package com.clj.jaf.storage;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class InternalStorage extends AbstractDiskStorage {
    private Context mContext;

    InternalStorage() {
    }

    void initActivity(Context context) {
        this.mContext = context;
    }

    public JStorage.StorageType getStorageType() {
        return JStorage.StorageType.INTERNAL;
    }

    public boolean createDirectory(String name) {
        File dir = this.mContext.getDir(name, 0);
        return dir.exists();
    }

    public boolean createFile(String name, String content) {
        try {
            byte[] e = content.getBytes();
            if (this.getConfiguration().isEncrypted()) {
                e = this.encrypt(e, 1);
            }

            FileOutputStream fos = this.mContext.openFileOutput(name, 0);
            fos.write(e);
            fos.close();
            return true;
        } catch (IOException var5) {
            throw new RuntimeException("Failed to create private file on internal storage", var5);
        }
    }

    public byte[] readFile(String name) {
        try {
            FileInputStream e = this.mContext.openFileInput(name);
            byte[] out = this.readFile(e);
            return out;
        } catch (IOException var4) {
            throw new RuntimeException("Failed to create private file on internal storage", var4);
        }
    }

    protected String buildAbsolutePath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    protected String buildPath(String directoryName) {
        String path = this.mContext.getDir(directoryName, 0).getAbsolutePath();
        return path;
    }

    protected String buildPath(String directoryName, String fileName) {
        String path = this.mContext.getDir(directoryName, 0).getAbsolutePath();
        path = path + File.separator + fileName;
        return path;
    }
}
