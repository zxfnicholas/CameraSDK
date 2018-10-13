package com.muzhi.camerasdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.muzhi.camerasdk.model.CameraSdkParameterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @Description: 图片操作
 * @author: Zengxiaofeng
 * @date: 2016-3-28 下午3:04:02
 */
public class PickUtil {

	
	//图片预览
	public static void openCameraSDKImagePreview(Context mContext, List<String> list, int position) {

		CameraSdkParameterInfo mCameraSdkParameterInfo=new CameraSdkParameterInfo();

		mCameraSdkParameterInfo.setImage_list((ArrayList<String>)list);
		mCameraSdkParameterInfo.setPosition(position);
		mCameraSdkParameterInfo.showDeleteButton=false;

		Bundle b=new Bundle();
		b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);

		Intent intent = new Intent();
		intent.setClassName(mContext, "com.muzhi.camerasdk.PreviewActivity");
		intent.putExtras(b);

		mContext.startActivity(intent);
	}
		
	/**
	 * 多图选择
	 * @param aty
	 * @param maxNum	最多选择多少张
	 * @param imgList :已选择的图片
	 */
	public static void pick(Activity aty,int maxNum,ArrayList<String> imgList) {

		/*CameraSdkParameterInfo mCameraSdkParameterInfo = new CameraSdkParameterInfo();
		mCameraSdkParameterInfo.setCroper_image(false);
		mCameraSdkParameterInfo.setFilter_image(false);
		mCameraSdkParameterInfo.setShow_camera(true);
		mCameraSdkParameterInfo.setMax_image(maxNum);
		mCameraSdkParameterInfo.setImage_list(imgList);
		mCameraSdkParameterInfo.setSingle_mode(maxNum==0);
		Intent intent = new Intent();
		
		//intent.setClassName(aty,"com.muzhi.mdroid.widget.photopick.PhotoPickActivity");
		intent.setClassName(aty,PhotoPickActivity.class.getName());
		Bundle b = new Bundle();
		b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER,mCameraSdkParameterInfo);
		intent.putExtras(b);
		aty.startActivityForResult(intent,CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY);*/
	}
	
    /**
     * 选择图片
     * @param aty
     */
	public static void pick(Activity aty) {

		/*CameraSdkParameterInfo mCameraSdkParameterInfo = new CameraSdkParameterInfo();
		mCameraSdkParameterInfo.setCroper_image(false);
		mCameraSdkParameterInfo.setFilter_image(false);
		mCameraSdkParameterInfo.setShow_camera(true);
		mCameraSdkParameterInfo.setSingle_mode(true);
		Intent intent = new Intent();
		intent.setClassName(aty,"com.muzhi.mdroid.widget.photopick.PhotoPickActivity");
		Bundle b = new Bundle();
		b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER,mCameraSdkParameterInfo);
		intent.putExtras(b);
		aty.startActivityForResult(intent,CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY);*/
	}
	
	/**
     * 选择图片
     * @param fra
     */
	public static void pick(Fragment fra) {

		/*CameraSdkParameterInfo mCameraSdkParameterInfo = new CameraSdkParameterInfo();
		mCameraSdkParameterInfo.setCroper_image(false);
		mCameraSdkParameterInfo.setFilter_image(false);
		mCameraSdkParameterInfo.setShow_camera(true);
		mCameraSdkParameterInfo.setSingle_mode(true);

		Intent intent = new Intent();
		intent.setClassName(fra.getContext(),"com.muzhi.mdroid.widget.photopick.PhotoPickActivity");
		Bundle b = new Bundle();
		b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER,mCameraSdkParameterInfo);
		intent.putExtras(b);
		fra.startActivityForResult(intent,CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY);*/
	}


	/**
     * 跳转到系统裁剪图片方法实现
     */
	
	public static void openSysCropCut(Activity aty,String filePath) {
		openSysCropCut(aty,100,filePath);
	}
	public static void openSysCropCut(Activity aty,int widthHeight, String filePath) {
		
		/*if(widthHeight==0){
			widthHeight=100;
		}
		
		File file = new File(filePath);
		Intent intent = new Intent("com.android.camera.action.CROP");

		Uri imageUri;
		if (Build.VERSION.SDK_INT >= 24) {
			String providerPkg= AppUtils.getAppPackageName(aty)+".fileprovider";
			imageUri = FileProvider.getUriForFile(aty,providerPkg , file);//通过FileProvider创建一个content类型的Uri
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
		else {
			imageUri=Uri.fromFile(file);
		}
		intent.setDataAndType(imageUri,"image*//*");

        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", widthHeight);
        intent.putExtra("outputY", widthHeight);
        intent.putExtra("scale", true);
        intent.putExtra("circleCrop", true);
        //设置输出的格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("return-data", true);

        aty.startActivityForResult(intent, CameraSdkParameterInfo.TAKE_PICTURE_CUT);*/
	}
	
	
	
}
