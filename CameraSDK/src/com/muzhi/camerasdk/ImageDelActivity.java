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

public class ImageDelActivity extends BaseActivity {

	private ImageView image_show;
	private TextView group_photo_cancel,group_photo_del;
	
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		int resId=MResource.getIdByName(this,MResource.layout, "camerasdk_activity_img_del");
		if (resId > 0) {
			setContentView(resId);
			Bundle bundle=getIntent().getExtras();
			if(bundle!=null){
				position=bundle.getInt("position", -1);
				String path=bundle.getString("path");
				image_show = (ImageView) findViewById(MResource.getIdByName(this,MResource.id, "image_show"));
				Bitmap bitmap = ImageUtils.getSmallBitmap(path);
				image_show.setImageBitmap(bitmap);
			}
		}
		
		int group_photo_cancel_id=MResource.getIdByName(this,MResource.id, "group_photo_cancel");
		group_photo_cancel=(TextView)findViewById(group_photo_cancel_id);
		int group_photo_del_id=MResource.getIdByName(this,MResource.id, "group_photo_del");
		group_photo_del=(TextView)findViewById(group_photo_del_id);
		
		initEvent();
		
	}

	private void initEvent(){
		group_photo_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		group_photo_del.setOnClickListener(new OnClickListener() {
			
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
