非常实用的类似新浪微博的多图片选择工具
===================

一.软件功能：
-------------

>  - 1.可以加载手机里面所有的图片
>  - 2.也可以根据相册文件夹选择图片
>  - 3.可以实时预览已选中的图片
>  - 4.可以删除已选择的图片。

二.使用方法：
-------------
> 1.下载camrasdk并添加到主项目当中.
> 
> 

>2.在主项目的AndroidManifest.xml文件中添加如下代码
```   
<activity android:name="com.muzhi.camerasdk.PhotoPickActivity" />
<activity android:name="com.muzhi.camerasdk.PreviewActivity" />
```

> 3.在主程序中利用如下两个方法调用
```   
//图片预览
public void openCameraSDKImagePreview(Activity activity,String path,int position) {
	Intent intent = new Intent(); 
	intent.setClassName(activity.getApplication(), "com.muzhi.camerasdk.PreviewActivity");  
	ArrayList<String> list=new ArrayList<String>();
	list.add(path);
	intent.putExtra(CommonDefine.EXTRA_IMAGES_LIST, list);// 默认选择的图片列表
	intent.putExtra(CommonDefine.EXTRA_POSITION, position);
	activity.startActivityForResult(intent, CommonDefine.TAKE_PICTURE_PREVIEW);
}
//本地相册选择
public void openCameraSDKPhotoPick(Activity activity,ArrayList<String> list) {
	Intent intent = new Intent(); 
	intent.setClassName(activity.getApplication(), "com.muzhi.camerasdk.PhotoPickActivity"); 
	intent.putExtra(CommonDefine.EXTRA_SHOW_CAMERA, showCamera);// 是否显示拍照按钮
       intent.putExtra(CommonDefine.EXTRA_SELECT_COUNT_MAX, maxImageSize); // 最大可选择图片数量
       intent.putExtra(CommonDefine.EXTRA_SELECT_MODE_SINGLE, selectedMode);// 选择模式(多选)
       if(list==null){
       	list=new ArrayList<String>();
       }
       intent.putExtra(CommonDefine.EXTRA_IMAGES_LIST, list);// 默认选择的图片列表
	startActivityForResult(intent, CommonDefine.TAKE_PICTURE_FROM_GALLERY);
}
```

三.软件截图：
-------------
![image](https://github.com/zxfnicholas/CameraSDK/blob/master/screenshots/1.png)
![image](https://github.com/zxfnicholas/CameraSDK/blob/master/screenshots/2.png)
![image](https://github.com/zxfnicholas/CameraSDK/blob/master/screenshots/3.png)
![image](https://github.com/zxfnicholas/CameraSDK/blob/master/screenshots/4.png)
![image](https://github.com/zxfnicholas/CameraSDK/blob/master/screenshots/5.png)

四.意见反馈：
-------------
> 微博：[http://www.weibo.com/zengxiaofeng](http://www.weibo.com/zengxiaofeng)  
> QQ群：241374213

