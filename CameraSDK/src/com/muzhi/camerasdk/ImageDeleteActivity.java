package com.muzhi.camerasdk;


import com.muzhi.camerasdk.utils.ImageUtils;
import com.muzhi.camerasdk.utils.MResource;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageDeleteActivity extends BaseActivity {

	private ImageView image_show;	
	private TextView actionbarTitle,button_cancel,button_delete;	
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		int resId=MResource.getIdByName(this,MResource.layout, "camerasdk_activity_image_delete");
		if (resId > 0) {
			setContentView(resId);
			image_show = (ImageView) findViewById(MResource.getIdByName(this,MResource.id, "image_show"));
			
			actionbarTitle = (TextView) findViewById(MResource.getIdByName(getApplication(),MResource.id,"camerasdk_actionbar_title"));
			button_cancel = (TextView) findViewById(MResource.getIdByName(getApplication(),MResource.id,"camerasdk_title_txv_left_text"));
			button_delete = (TextView) findViewById(MResource.getIdByName(getApplication(),MResource.id,"camerasdk_title_txv_right_text"));
			
			actionbarTitle.setText(MResource.getIdByName(getApplication(),MResource.string,"camerasdk_show_image"));
			button_cancel.setText(MResource.getIdByName(getApplication(),MResource.string,"camerasdk_back"));
			button_delete.setText(MResource.getIdByName(getApplication(),MResource.string,"camerasdk_delete"));
			button_delete.setVisibility(View.VISIBLE);
					
			
			Bundle bundle=getIntent().getExtras();
			if(bundle!=null){
				position=bundle.getInt("position", -1);
				String path=bundle.getString("path");				
				Bitmap bitmap = ImageUtils.getSmallBitmap(path);
				image_show.setImageBitmap(bitmap);
			}
		}
		
		
		initEvent();
		
	}

	private void initEvent(){
		button_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		button_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				delete();
			}
		});
	}
	public void delete() {
		Intent intent = new Intent();
		intent.putExtra("position", position);
		setResult(RESULT_OK, intent);
		finish();
	}

}
