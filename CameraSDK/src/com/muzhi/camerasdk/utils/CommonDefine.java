package com.muzhi.camerasdk.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class CommonDefine {

	public static final String FILE_PATH = "/sdcard/syscamera.jpg";
	// 相机，图库的选择
	public static final int TAKE_PICTURE_FROM_CAMERA = 100;
	public static final int TAKE_PICTURE_FROM_GALLERY = 200;
	public static final int DELETE_IMAGE = 300;
	
	//相片数据参数
	public static final String IMAGES_LIST = "imageslist";
	public static final String IMAGES_ADD = "imagesadd";
	
	

	/*public static void openPhotoPick(Activity activity, Class<?> activityclass, Bundle bundle, int requestCode) {
		Intent intent = new Intent(activity, activityclass);
		if (null != bundle && !"".equals(bundle)) {
			intent.putExtras(bundle);
		}
		activity.startActivityForResult(intent, requestCode);

	}*/
	
}
