非常实用的类似新浪微博的多图片选择工具
===================

一.软件功能：
-------------

>  - 1.可以加载手机里面所有的图片
>  - 2.也可以根据相册文件夹选择图片
>  - 3.裁剪正方形图片
>  - 4.给图片加滤镜特效
>  - 5.给图片加贴纸功能
>  - 6.可以删除已选择的图片。

二.使用方法：
-------------
> 1.下载camerasdk并添加到主项目当中.
> 
> 

>2.在主项目的AndroidManifest.xml文件中添加如下代码
```  
<!-- CameraSDK相册选取相关权限 -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<!-- CameraSDK相册选取相关activity -->
<activity android:name="com.muzhi.camerasdk.PhotoPickActivity" />
<activity android:name="com.muzhi.camerasdk.PreviewActivity" />
<activity android:name="com.muzhi.camerasdk.CropperImageActivity" />
<activity android:name="com.muzhi.camerasdk.FilterImageActivity" />
```

> 3.在主程序中利用如下两个方法调用
```   
//图片预览
public void openCameraSDKImagePreview(Activity activity,String path,int position) {
	Intent intent = new Intent(); 
	intent.setClassName(activity.getApplication(), "com.muzhi.camerasdk.PreviewActivity");  
	ArrayList<String> list=new ArrayList<String>();
	list.add(path);
	mCameraSdkParameterInfo.setImage_list(list);
	mCameraSdkParameterInfo.setPosition(position);
	Bundle b=new Bundle();
	b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
	intent.putExtras(b);
	startActivityForResult(intent, CameraSdkParameterInfo.TAKE_PICTURE_PREVIEW);
}
//本地相册选择
public void openCameraSDKPhotoPick(Activity activity,ArrayList<String> list) {
	Intent intent = new Intent(); 
	intent.setClassName(activity.getApplication(), "com.muzhi.camerasdk.PhotoPickActivity"); 
	Bundle b=new Bundle();
	b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
	intent.putExtras(b);
	startActivityForResult(intent, CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY);
}
```

三.软件截图：
-------------
![image](https://github.com/zxfnicholas/CameraSDK/blob/master/screenshots/1.png)
![image](https://github.com/zxfnicholas/CameraSDK/blob/master/screenshots/2.png)
![image](https://github.com/zxfnicholas/CameraSDK/blob/master/screenshots/3.png)
![image](https://github.com/zxfnicholas/CameraSDK/blob/master/screenshots/4.png)

四.意见反馈：
-------------
> 微博：[http://www.weibo.com/zengxiaofeng](http://www.weibo.com/zengxiaofeng)  
> QQ群：241374213

