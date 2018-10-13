package com.muzhi.camerasdk.library.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;


/**
 * 文件操作类
 */
public class CommonUtils {

	/**
	 * 获取应用程序名称
	 * @param mContext
	 * @return
	 */
	public static String getApplicationName(Context mContext) { 
		PackageManager packageManager = null; 
		ApplicationInfo applicationInfo = null; 
		try { 
			packageManager = mContext.getApplicationContext().getPackageManager(); 
			applicationInfo = packageManager.getApplicationInfo(mContext.getPackageName(), 0); 
		} 
		catch (PackageManager.NameNotFoundException e) { 
			applicationInfo = null; 
		} 
		String applicationName = (String) packageManager.getApplicationLabel(applicationInfo); 
		return applicationName; 
	}

}
