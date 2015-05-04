非常实用的类似新浪微博的多图片选择工具
===========
一.实现的功能：
1.可以加载手机里面所有的图片
2.也可以根据相册文件夹选择图片
3.可以实时预览已选中的图片
4.可以删除已选择的图片。
二.如何使用
1.下载camrasdk并添加到主项目当中
2.在主项目的AndroidManifest.xml文件中添加如下代码
<!-- 相册选取 -->
        <activity android:name="com.muzhi.camerasdk.PhotoPickActivity" />
        <activity android:name="com.muzhi.camerasdk.AlbumActivity" />
        <activity android:name="com.muzhi.camerasdk.ImageDelActivity" />
3.在主程序中需要调用的的地方添加如下代码：
Intent intent = new Intent();  
		            intent.setClassName(getApplication(), "com.muzhi.camerasdk.PhotoPickActivity");  
		            Bundle bundle = new Bundle();
					bundle.putStringArrayList(CommonDefine.IMAGES_LIST,gridImageAdapter.getSourceList());
					intent.putExtras(bundle);
		            startActivityForResult(intent, CommonDefine.TAKE_PICTURE_FROM_GALLERY);
	4.回调的方法中取数据
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_CODE:
		case CommonDefine.TAKE_PICTURE_FROM_GALLERY:
			if(data!=null){
				Bundle bundle=data.getExtras();
				ArrayList<String> list=(ArrayList<String>) bundle.getSerializable(CommonDefine.IMAGES_LIST);
			}
			break;
		
		}
		
	}
	
	
	
	
	
