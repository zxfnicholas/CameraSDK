package com.muzhi.camerasdk.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 跟App相关的辅助类
 * 
 * 
 * 
 */
public class AppUtils {

	private AppUtils(){
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");

	}

	/**
	 * 获取应用程序名称
	 */
	public static String getAppName(Context context){
		
		try{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} 
		catch (NameNotFoundException e){
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 根据Pkg获取应用程序名称
	 */
	public static String getAppName(Context context,String pkg){
		PackageManager packMan = context.getPackageManager();
		ApplicationInfo appInfo;
		try {
			appInfo = packMan.getApplicationInfo(pkg,0);
			return packMan.getApplicationLabel(appInfo).toString();
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	
	/**
	 * 获取应用程序包名
	 */
	public static String getAppPackageName(Context context){
		
		try	{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.packageName;

		} 
		catch (NameNotFoundException e){
			e.printStackTrace();
			return "";
		}
	}
	
	
	
	/**
	 * [获取应用程序版本名称信息]
	 * 
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context){
		try	{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;

		} 
		catch (NameNotFoundException e){
			e.printStackTrace();
		}
		return null;
	}

	
	/**
     * 获取软件版本号
     * 
     * @param context
     * @return
     */
    public static int getVersionCode(Context context){  
        int versionCode = 0;
        try{
            // 获取软件版本号           
        	PackageManager packageManager = context.getPackageManager();       
	        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),0); 
	        versionCode = packInfo.versionCode;
        } 
        catch (NameNotFoundException e){
            e.printStackTrace();
        }
        return versionCode;
    }
    
    /**
     *  获取本地号码
     */
	@SuppressLint("MissingPermission")
	public static String getLocationPhone(Context context){
  	  	TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId(); //设备号
        String tel = tm.getLine1Number();	//电话号码
        String imei = tm.getSimSerialNumber();
        String imsi = tm.getSubscriberId();
        return tel;
    }
    
    /**
     * 获取手机信息包括(手机制作商名称，手机型号)
     */
    /*public static MobileManufacturInfo getMobileManufacturerInfo(){
    	
    	String MANUFACTURER= Build.MANUFACTURER;
		String MODEL= Build.MODEL;
		String PRODUCT= Build.PRODUCT;
		String version= Build.VERSION.RELEASE;
		//String version_codes=android.os.Build.VERSION_CODES.;

		MobileManufacturInfo info=new MobileManufacturInfo();
		info.setManufacurerInfo(MANUFACTURER.toLowerCase());
		info.setModelInfo(MODEL.toLowerCase());
		info.setProductInfo(PRODUCT.toLowerCase());

		return info;

		//MANUFACTURER如下：
		//三星:	samsung
		//魅族:	meizu
		//VIVO: vivo
		//小米:xiaomi


		//samsung
		*//*if(MANUFACTURER.toLowerCase().equals("meizu")){
			MToast.showShort(mContext, manufacurer+"-"+MODEL+"-"+PRODUCT+"-品牌手机");
		}*//*
    }*/




    /**
     * 判断程序是否在 主进程
     * 需要添加android.permission.GET_TASKS权限
     */
    public static boolean isMainProcess(Context context) {
		ActivityManager am = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE));
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		String mainProcessName = context.getPackageName();
		int myPid = android.os.Process.myPid();
		for (RunningAppProcessInfo info : processInfos) {
			if (info.pid == myPid && mainProcessName.equals(info.processName)) {
				return true;
			}
		}
		return false;
	}

    /**
     * 获取当前运行的应用程包名
     * @return
     */
    public static String getCurrentPackage(Context context) {

    	String packageName="";
    	ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
    	List<RunningTaskInfo>  tasksInfo = activityManager.getRunningTasks(1);
    	if(tasksInfo.size() > 0){
    		//应用程序位于堆栈的顶层
    		packageName= tasksInfo.get(0).topActivity.getPackageName();
    	}
    	return packageName;
    }


    /**
     * 判断 Service是否启用状态
     *
     * @return
     */
    public static boolean isServiceEnabled(Context context,String serviceName) {

    	ActivityManager  activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
  	    List<ActivityManager.RunningServiceInfo>serviceList= activityManager.getRunningServices(300);
  	    boolean flag=false;
  	    for(int i = 0; i < serviceList.size(); i ++){
  	    	String cn=serviceList.get(i).service.getClassName();
  	    	if(serviceName.equals(cn)){
  	    		flag= true;
  	    		break;
  	    	}
  	    }
  	    return flag;
    }

    /**
     * 判断 ACCESSIBILITY_SERVICE 是否启用状态
     *
     * @return
     */
    public static boolean isAccessibilityServiceEnabled(Context context,String serviceName) {

    	AccessibilityManager accessibilityManager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices =accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {

            if (info.getId().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }





    /**
     * 判断应用是否具有某个权限
     *
     * @return
     */
    public static boolean checkPermission(Context context,String permissionName) {

    	PackageManager pm = context.getPackageManager();
		boolean flag = (PackageManager.PERMISSION_GRANTED ==pm.checkPermission(permissionName, context.getPackageName()));
		return flag;
    }






    /**
     * 清除本应用所有的数据
     */
	public static void cleanApplicationData(Context context, String filePath) {

		File directory=new File(filePath);

		if (directory != null && directory.exists() && directory.isDirectory()) {
			/*for (File item : directory.listFiles()) {
				item.delete();
			}*/
			directory.delete();
		}
	}


	 /**
     * 显示或隐藏软键盘
     */
    public static void showKeyboard(Context context,boolean show) {
        Activity activity = (Activity) context;
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(show){
            	imm.showSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
            	imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
            else{
            	if (imm.isActive() && activity.getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                }
            }
        }
    }
    /**
     * 显示App应用市场去评论对话框
     */
    public static void showAppScoreDialog(Context mContext) {
		try{
			Uri uri = Uri.parse("market://details?id="+mContext.getPackageName());
			Intent intent = new Intent(Intent.ACTION_VIEW,uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		}
		catch (Exception e){
			e.printStackTrace();
		}

    }
    /**
     * 系统级的分享代码
     */
    public static void showShareDialog(Context mContext,String msg) {
    	/*Intent sendIntent = new Intent();
    	sendIntent.setAction(Intent.ACTION_SEND);
    	sendIntent.setType("text/*");
    	sendIntent.putExtra(Intent.EXTRA_TEXT,msg);
    	mContext.startActivity(sendIntent);*/

		Intent intent=new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,msg);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(Intent.createChooser(intent, "分享到"));

    }




    //跳转到拨号界面
    public static void openIntentPhone(Context context,String phoneNumber){
    	Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + phoneNumber));
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(intent);
    }
    //跳转到发短信
    public static void openIntentSms(Context context,String phoneNumber){
    	Intent intent = new Intent( Intent.ACTION_SENDTO, Uri.parse("smsto://"+phoneNumber));
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity( intent );
    }




}

