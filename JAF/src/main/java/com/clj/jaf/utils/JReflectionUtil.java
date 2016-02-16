/**
 * 
 */
package com.clj.jaf.utils;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

/**
 * 反射的帮助类
 */
public class JReflectionUtil {
	
	/**
	 * 获取某一个包名下的所有类名
	 */
    public static List<String> getPackageAllClassName(Context context,String packageName) throws IOException{ 
		String sourcePath = context.getApplicationInfo().sourceDir;
		List<String> paths = new ArrayList<>();
		
		if (sourcePath != null) {
			DexFile dexfile = new DexFile(sourcePath);
			Enumeration<String> entries = dexfile.entries();

			while (entries.hasMoreElements()) {
				String element = entries.nextElement();
				if (element.contains(packageName)) {
					paths.add(element);
				}
			}
		}
		
		return paths;
    }
}
