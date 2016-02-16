package com.clj.jaf.storage;

import android.graphics.Bitmap;

import com.clj.jaf.storage.helpers.OrderType;
import com.clj.jaf.storage.helpers.SizeUnit;

import java.io.File;
import java.util.List;

public interface Storage {
    JStorage.StorageType getStorageType();

    boolean createDirectory(String var1);

    boolean createDirectory(String var1, boolean var2);

    boolean deleteDirectory(String var1);

    boolean isDirectoryExists(String var1);

    boolean createFile(String var1, String var2, String var3);

    boolean createFile(String var1, String var2, Storable var3);

    boolean createFile(String var1, String var2, Bitmap var3);

    boolean createFile(String var1, String var2, byte[] var3);

    boolean deleteFile(String var1, String var2);

    boolean isFileExist(String var1, String var2);

    byte[] readFile(String var1, String var2);

    String readTextFile(String var1, String var2);

    void appendFile(String var1, String var2, String var3);

    void appendFile(String var1, String var2, byte[] var3);

    List<File> getNestedFiles(String var1);

    List<File> getFiles(String var1, String var2);

    List<File> getFiles(String var1, OrderType var2);

    File getFile(String var1);

    File getFile(String var1, String var2);

    void rename(File var1, String var2);

    double getSize(File var1, SizeUnit var2);

    long getFreeSpace(SizeUnit var1);

    long getUsedSpace(SizeUnit var1);

    void copy(File var1, String var2, String var3);

    void move(File var1, String var2, String var3);
}
