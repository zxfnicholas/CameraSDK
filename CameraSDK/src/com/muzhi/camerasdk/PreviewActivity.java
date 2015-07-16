package com.muzhi.camerasdk;


import java.io.File;
import java.util.ArrayList;

import com.muzhi.camerasdk.utils.CommonDefine;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 图片预览
 * @author zengxiaofeng
 *
 */
public class PreviewActivity extends BaseActivity {

	private ImageView image_show;	
	private TextView button_delete;	
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camerasdk_activity_preview);
		showLeftIcon();
		setActionBarTitle(getString(R.string.camerasdk_preview_image));
		
		button_delete = (TextView) findViewById(R.id.camerasdk_title_txv_right_text);
		button_delete.setVisibility(View.VISIBLE);
		button_delete.setText(getString(R.string.camerasdk_delete));
		
		image_show = (ImageView)findViewById(R.id.iv_camerasdk_preview_image);

		Intent intent = getIntent();
		ArrayList<String>  resultList = intent.getStringArrayListExtra(CommonDefine.EXTRA_IMAGES_LIST);
		position=intent.getIntExtra(CommonDefine.EXTRA_POSITION, -1);
		
		File imageFile = new File(resultList.get(0));
		image_show.setImageURI(Uri.fromFile(imageFile));
		
		initEvent();
		
	}

	private void initEvent(){
		
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
