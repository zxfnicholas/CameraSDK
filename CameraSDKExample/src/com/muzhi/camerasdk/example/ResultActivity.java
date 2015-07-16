package com.muzhi.camerasdk.example;


import java.util.ArrayList;

import com.muzhi.camerasdk.example.adapter.ImageGridAdapter;
import com.muzhi.camerasdk.example.model.ImageInfo;
import com.muzhi.camerasdk.utils.CommonDefine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author zengxiaofeng
 *	
 */
public class ResultActivity extends Activity {

	private EditText content;
	private GridView noScrollgridview;
	private ArrayList<ImageInfo> pic_list;
	private ImageGridAdapter mImageGridAdapter;
	private int maxImageSize=9;
	private boolean showCamera=true;
	private boolean selectedMode=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		((TextView)findViewById(R.id.camerasdk_actionbar_title)).setText(getString(R.string.app_name));
		
		
		content = (EditText) findViewById(R.id.content);
		noScrollgridview= (GridView) findViewById(R.id.noScrollgridview);
		mImageGridAdapter = new ImageGridAdapter(this,maxImageSize);
		noScrollgridview.setAdapter(mImageGridAdapter);
		
		Bundle b=getIntent().getExtras();
		maxImageSize=b.getInt("maxImageSize", 9);
		selectedMode = b.getBoolean(CommonDefine.EXTRA_SELECT_MODE_SINGLE,false);
		showCamera = b.getBoolean(CommonDefine.EXTRA_SHOW_CAMERA, true);
		 
		//getBundle(b);
		initEvent();
		
		openCameraSDKPhotoPick(this,null);
	}
	
	
	private void getBundle(Bundle bundle){
		if(bundle!=null){
			pic_list=new ArrayList<ImageInfo>();
			ArrayList<String> list=(ArrayList<String>) bundle.getSerializable(CommonDefine.EXTRA_IMAGES_LIST);
			if(list!=null){
				for(int i=0;i<list.size();i++){
					ImageInfo img=new ImageInfo();
					img.setSource_image(list.get(i));
					pic_list.add(img);
				}
				
			}
			if(pic_list.size()<maxImageSize){
				ImageInfo item=new ImageInfo();
				item.setAddButton(true);
				pic_list.add(item);
			}
			mImageGridAdapter.setList(pic_list);
		}
	}
	
	private void initEvent(){
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				//ImageInfo info=(ImageInfo)ImageGridAdapter.getItem(position);
				ImageInfo info=(ImageInfo)arg0.getAdapter().getItem(position);
				if(info.isAddButton()){
					
					ArrayList<String> list=new ArrayList<String>();
					for(ImageInfo pic:pic_list){
						if(!pic.isAddButton()){
							list.add(pic.getSource_image());
						}
					}
					openCameraSDKPhotoPick(ResultActivity.this, list);
				}
				else{					
		            openCameraSDKImagePreview(ResultActivity.this,pic_list.get(position).getSource_image(),position);
				}
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {		
		case CommonDefine.TAKE_PICTURE_FROM_GALLERY:
			if(data!=null){
				getBundle(data.getExtras());
			}
			break;		
		case CommonDefine.TAKE_PICTURE_PREVIEW:
			if(data!=null){
				int position = data.getIntExtra("position", -1);
				if(position>=0){
					mImageGridAdapter.deleteItem(position);
				}
			}
			break;
		}
		
	}
	
	
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

	
}
