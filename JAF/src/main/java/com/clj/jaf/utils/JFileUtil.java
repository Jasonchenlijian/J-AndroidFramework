package com.clj.jaf.utils;

import android.content.Context;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;


public class JFileUtil {

    /**
     * 判断是否文件
     */
    public static boolean isFile(File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * 判断是否目录
     */
    public static boolean isDirectory(File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    /**
     * 路径是否存在
     */
    public static boolean isFileExit(String path) {
        if(path == null) {
            return false;
        } else {
            try {
                File f = new File(path);
                if(f.exists()) {
                    return true;
                }
            } catch (Exception var2) {
                ;
            }

            return false;
        }
    }

    /**
     * 写入文件(覆盖模式)
     *
     * @param context
     * @param content
     * @param path
     */
    public static void writeString(Context context, String content, String path) {
        try {
            FileOutputStream fos = context.openFileOutput(path, Context.MODE_PRIVATE);
            PrintStream ps = new PrintStream(fos);
            ps.print(content);
            ps.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读文件
     *
     * @param context
     * @param path
     * @return
     */
    public static String readString(Context context, String path) {
        try {
            FileInputStream fis = context.openFileInput(path);
            byte[] buff = new byte[1024];
            int hasRead = 0;
            StringBuilder sb = new StringBuilder("");
            while ((hasRead = fis.read(buff)) > 0) {
                sb.append(new String(buff, 0, hasRead));
            }
            fis.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 写入文件（对象）
     *
     * @param context
     * @param content
     * @param path
     */
    public static void writeSObject(Context context, Object content, String path) {

        try {
            ByteArrayOutputStream e = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(e);
            oos.writeObject(content);
            String stringBase64 = new String(Base64.encode(e.toByteArray(), 0));
            writeString(context, stringBase64, path);
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    /**
     * 读文件（对象）
     *
     * @param context
     * @param path
     * @return
     */
    public static Object readObject(Context context, String path) {

        String str = readString(context, path);
        if (JStringUtil.isEmpty(str)) {
            return null;
        } else {
            byte[] base64Bytes = Base64.decode(str.getBytes(), 0);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            try {
                ObjectInputStream ois = new ObjectInputStream(bais);
                return ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
