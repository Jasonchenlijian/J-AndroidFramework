package com.clj.jaf.storage;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StatFs;

import com.clj.jaf.storage.helpers.OrderType;
import com.clj.jaf.storage.helpers.SizeUnit;
import com.clj.jaf.storage.security.SecurityUtil;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

abstract class AbstractDiskStorage implements Storage {
    protected static final String UTF_8 = "UTF-8";

    AbstractDiskStorage() {
    }

    protected StorageConfiguration getConfiguration() {
        return JStorage.getInstance().getConfiguration();
    }

    public boolean createDirectory(String name) {
        String path = this.buildPath(name);
        if (this.isDirectoryExists(path)) {
            throw new RuntimeException("The direcory already exist. You can\'t override the existing one. Use createDirectory(String path, boolean override)");
        } else {
            File directory = new File(path);
            boolean wasCreated = directory.mkdirs();
            return wasCreated;
        }
    }

    public boolean createDirectory(String name, boolean override) {
        if (!override) {
            return this.isDirectoryExists(name) || this.createDirectory(name);
        } else {
            if (this.isDirectoryExists(name)) {
                this.deleteDirectory(name);
            }

            boolean wasCreated = this.createDirectory(name);
            if (!wasCreated) {
                throw new RuntimeException("Couldn\'t create new direcory");
            } else {
                return true;
            }
        }
    }

    public boolean deleteDirectory(String name) {
        String path = this.buildPath(name);
        return this.deleteDirectoryImpl(path);
    }

    public boolean isDirectoryExists(String name) {
        String path = this.buildPath(name);
        return (new File(path)).exists();
    }

    public boolean createFile(String directoryName, String fileName, String content) {
        return this.createFile(directoryName, fileName, content.getBytes());
    }

    public boolean createFile(String directoryName, String fileName, Storable storable) {
        return this.createFile(directoryName, fileName, storable.getBytes());
    }

    public boolean createFile(String directoryName, String fileName, byte[] content) {
        String path = this.buildPath(directoryName, fileName);

        try {
            FileOutputStream e = new FileOutputStream(new File(path));
            if (this.getConfiguration().isEncrypted()) {
                content = this.encrypt(content, 1);
            }

            e.write(content);
            e.flush();
            e.close();
            return true;
        } catch (IOException var6) {
            throw new RuntimeException("Failed to create", var6);
        }
    }

    public boolean createFile(String directoryName, String fileName, Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return this.createFile(directoryName, fileName, byteArray);
    }

    public boolean deleteFile(String directoryName, String fileName) {
        String path = this.buildPath(directoryName, fileName);
        File file = new File(path);
        return file.delete();
    }

    public boolean isFileExist(String directoryName, String fileName) {
        String path = this.buildPath(directoryName, fileName);
        return (new File(path)).exists();
    }

    public byte[] readFile(String directoryName, String fileName) {
        String path = this.buildPath(directoryName, fileName);

        try {
            FileInputStream stream = new FileInputStream(new File(path));
            return this.readFile(stream);
        } catch (FileNotFoundException var6) {
            throw new RuntimeException("Failed to read file to input stream", var6);
        }
    }

    public String readTextFile(String directoryName, String fileName) {
        byte[] bytes = this.readFile(directoryName, fileName);
        String content = new String(bytes);
        return content;
    }

    public void appendFile(String directoryName, String fileName, String content) {
        this.appendFile(directoryName, fileName, content.getBytes());
    }

    public void appendFile(String directoryName, String fileName, byte[] bytes) {
        if (!this.isFileExist(directoryName, fileName)) {
            throw new RuntimeException("Impossible to append content, because such file doesn\'t exist");
        } else {
            try {
                String e = this.buildPath(directoryName, fileName);
                FileOutputStream stream = new FileOutputStream(new File(e), true);
                stream.write(bytes);
                stream.write(System.getProperty("line.separator").getBytes());
                stream.flush();
                stream.close();
            } catch (IOException var6) {
                throw new RuntimeException("Failed to append content to file", var6);
            }
        }
    }

    public List<File> getNestedFiles(String directoryName) {
        String buildPath = this.buildPath(directoryName);
        File file = new File(buildPath);
        ArrayList out = new ArrayList();
        this.getDirectoryFilesImpl(file, out);
        return out;
    }

    public List<File> getFiles(String directoryName, final String matchRegex) {
        String buildPath = this.buildPath(directoryName);
        File file = new File(buildPath);
        List out = null;
        if (matchRegex != null) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String fileName) {
                    return fileName.matches(matchRegex);
                }
            };
            out = Arrays.asList(file.listFiles(filter));
        } else {
            out = Arrays.asList(file.listFiles());
        }

        return out;
    }

    public List<File> getFiles(String directoryName, OrderType orderType) {
        List files = this.getFiles(directoryName, (String) null);
        Collections.sort(files, orderType.getComparator());
        return files;
    }

    public File getFile(String name) {
        String path = this.buildPath(name);
        File file = new File(path);
        return file;
    }

    public File getFile(String directoryName, String fileName) {
        String path = this.buildPath(directoryName, fileName);
        return new File(path);
    }

    public void rename(File file, String newName) {
        String name = file.getName();
        String newFullName = file.getAbsolutePath().replaceAll(name, newName);
        File newFile = new File(newFullName);
        file.renameTo(newFile);
    }

    public double getSize(File file, SizeUnit unit) {
        long length = file.length();
        return (double) length / (double) unit.inBytes();
    }

    @SuppressLint({"NewApi"})
    public long getFreeSpace(SizeUnit sizeUnit) {
        String path = this.buildAbsolutePath();
        StatFs statFs = new StatFs(path);
        long availableBlocks;
        long blockSize;
        if (Build.VERSION.SDK_INT < 18) {
            availableBlocks = (long) statFs.getAvailableBlocks();
            blockSize = (long) statFs.getBlockSize();
        } else {
            availableBlocks = statFs.getAvailableBlocksLong();
            blockSize = statFs.getBlockSizeLong();
        }

        long freeBytes = availableBlocks * blockSize;
        return freeBytes / sizeUnit.inBytes();
    }

    @SuppressLint({"NewApi"})
    public long getUsedSpace(SizeUnit sizeUnit) {
        String path = this.buildAbsolutePath();
        StatFs statFs = new StatFs(path);
        long availableBlocks;
        long blockSize;
        long totalBlocks;
        if (Build.VERSION.SDK_INT < 18) {
            availableBlocks = (long) statFs.getAvailableBlocks();
            blockSize = (long) statFs.getBlockSize();
            totalBlocks = (long) statFs.getBlockCount();
        } else {
            availableBlocks = statFs.getAvailableBlocksLong();
            blockSize = statFs.getBlockSizeLong();
            totalBlocks = statFs.getBlockCountLong();
        }

        long usedBytes = totalBlocks * blockSize - availableBlocks * blockSize;
        return usedBytes / sizeUnit.inBytes();
    }

    public void copy(File file, String directoryName, String fileName) {
        if (file.isFile()) {
            FileInputStream inStream = null;
            FileOutputStream outStream = null;

            try {
                inStream = new FileInputStream(file);
                outStream = new FileOutputStream(new File(this.buildPath(directoryName, fileName)));
                FileChannel e = inStream.getChannel();
                FileChannel outChannel = outStream.getChannel();
                e.transferTo(0L, e.size(), outChannel);
            } catch (Exception var11) {
                throw new StorageException(var11);
            } finally {
                this.closeQuietly(inStream);
                this.closeQuietly(outStream);
            }

        }
    }

    public void move(File file, String directoryName, String fileName) {
        this.copy(file, directoryName, fileName);
        file.delete();
    }

    protected byte[] readFile(final FileInputStream stream) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n;
        try {
            while ((n = stream.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    protected abstract String buildAbsolutePath();

    protected abstract String buildPath(String var1);

    protected abstract String buildPath(String var1, String var2);

    protected synchronized byte[] encrypt(byte[] content, int encryptionMode) {
        byte[] secretKey = this.getConfiguration().getSecretKey();
        byte[] ivx = this.getConfiguration().getIvParameter();
        return SecurityUtil.encrypt(content, encryptionMode, secretKey, ivx);
    }

    private boolean deleteDirectoryImpl(String path) {
        File directory = new File(path);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files == null) {
                return true;
            }

            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    this.deleteDirectoryImpl(files[i].getAbsolutePath());
                } else {
                    files[i].delete();
                }
            }
        }

        return directory.delete();
    }

    private void getDirectoryFilesImpl(File directory, List<File> out) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files == null) {
                return;
            }

            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    this.getDirectoryFilesImpl(files[i], out);
                } else {
                    out.add(files[i]);
                }
            }
        }

    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException var3) {
                ;
            }
        }

    }
}
