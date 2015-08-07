package com.muzhi.camerasdk.utils;

import android.content.Context;


public class DistanceUtil {

   /* public static int getCameraAlbumWidth() {
        return (App.getApp().getScreenWidth() - App.getApp().dp2px(10)) / 4 - App.getApp().dp2px(4);
    }
    
    // 相机照片列表高度计算 
    public static int getCameraPhotoAreaHeight(Context context) {
        return getCameraPhotoWidth() + dip2px(context,4);
    }
    
    public static int getCameraPhotoWidth(Context context) {
        return App.getApp().getScreenWidth() / 4 - dip2px(context,2);
    }

    //活动标签页grid图片高度
    public static int getActivityHeight(Context context) {
        return (App.getApp().getScreenWidth() - dip2px(context,24)) / 3;
    }*/
    
    
    
    public static int dip2px(Context context,int dipValue) {
    	float reSize=context.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * reSize) + 0.5);
    }

    public static int px2dip(Context context,int pxValue) {
        float reSize = context.getResources().getDisplayMetrics().density;
        return (int) ((pxValue / reSize) + 0.5);
    }
    
    
}
