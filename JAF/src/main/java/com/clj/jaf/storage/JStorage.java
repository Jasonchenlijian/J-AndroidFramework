package com.clj.jaf.storage;

import android.content.Context;

public class JStorage {
    private static InternalStorage mInternalStorage = null;
    private static ExternalStorage mExternalStorage = null;
    private static JStorage mInstance = null;
    private static StorageConfiguration mStorageConfiguration;

    private JStorage() {
        mStorageConfiguration = (new StorageConfiguration.Builder()).build();
        mInternalStorage = new InternalStorage();
        mExternalStorage = new ExternalStorage();
    }

    public static JStorage getInstance() {
        if (mInstance == null) {
            mInstance = new JStorage();
        }

        return mInstance;
    }

    public InternalStorage getInternalStorage(Context context) {
        mInternalStorage.initActivity(context);
        return mInternalStorage;
    }

    public ExternalStorage getExternalStorage() {
        return mExternalStorage;
    }

    public boolean isExternalStorageWritable() {
        return mExternalStorage.isWritable();
    }

    public StorageConfiguration getConfiguration() {
        return mStorageConfiguration;
    }

    public static void updateConfiguration(StorageConfiguration configuration) {
        if (mInstance == null) {
            throw new RuntimeException("First instantiate the Storage and then you can update the configuration");
        } else {
            mStorageConfiguration = configuration;
        }
    }

    public static void resetConfiguration() {
        StorageConfiguration configuration = (new StorageConfiguration.Builder()).build();
        mStorageConfiguration = configuration;
    }

    public static enum StorageType {
        INTERNAL,
        EXTERNAL;
    }
}
