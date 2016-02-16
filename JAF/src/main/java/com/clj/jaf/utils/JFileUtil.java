package com.clj.jaf.utils;

import java.io.File;


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
     * @param path：路径
     * @return： 是否
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
}
