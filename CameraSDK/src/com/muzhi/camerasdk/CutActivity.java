package com.muzhi.camerasdk;

import com.muzhi.camerasdk.library.utils.PhotoUtils;
import com.muzhi.camerasdk.model.Constants;
import com.muzhi.camerasdk.view.CropImageView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;



public class CutActivity extends BaseActivity {
  
    private CropImageView mCropView;
    private TextView btn_done;

    private RadioGroup layout_crop,layout_tab;
    private LinearLayout layout_rotation;
    private Bitmap sourceMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camerasdk_activity_cut);
        showLeftIcon();
        setActionBarTitle("裁剪");
        findViews();
        
        sourceMap=Constants.bitmap;
        mCropView.setImageBitmap(sourceMap);		
        
    }

    private void findViews() {
        mCropView = (CropImageView) findViewById(R.id.cropImageView);
        btn_done=(TextView)findViewById(R.id.camerasdk_title_txv_right_text);
        btn_done.setVisibility(View.VISIBLE);
        btn_done.setText("确定");
        
        layout_crop=(RadioGroup)findViewById(R.id.layout_crop);
        layout_tab=(RadioGroup)findViewById(R.id.layout_tab);
        layout_rotation=(LinearLayout)findViewById(R.id.layout_rotation);
        initEvent();
    }
	
	private void initEvent() {
		findViewById(R.id.button1_1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCropView.setCropMode(CropImageView.CropMode.RATIO_1_1);
			}
		});
		findViewById(R.id.button3_4).setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
				mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
			}
		});
		findViewById(R.id.button4_3).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
			}
		});
		findViewById(R.id.button9_16).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
			}
		});
		findViewById(R.id.button16_9).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
			}
		});
		btn_done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				done();
			}
		});
		/*findViewById(R.id.buttonFree).setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				mCropView.setCropMode(CropImageView.CropMode.RATIO_FREE); //自由裁剪
			}
		});*/
		
		//*******************************************************************************
		
		layout_tab.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if(arg1==R.id.button_crop){
					layout_crop.setVisibility(View.VISIBLE);
					layout_rotation.setVisibility(View.GONE);
				}
				else{
					layout_crop.setVisibility(View.GONE);
					layout_rotation.setVisibility(View.VISIBLE);
				}
			}
		});
		
		findViewById(R.id.ratation_left).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sourceMap = PhotoUtils.rotateImage(sourceMap, -90);
				mCropView.setImageBitmap(sourceMap);
			}
		});
		findViewById(R.id.ratation_right).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sourceMap = PhotoUtils.rotateImage(sourceMap, 90);
				mCropView.setImageBitmap(sourceMap);
			}
		});
		findViewById(R.id.ratation_vertical).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sourceMap = PhotoUtils.reverseImage(sourceMap, -1, 1);
				mCropView.setImageBitmap(sourceMap);
			}
		});
		findViewById(R.id.ratation_updown).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sourceMap = PhotoUtils.reverseImage(sourceMap, 1, -1);
				mCropView.setImageBitmap(sourceMap);
			}
		});
		
		
	}
    
    private void done(){
    	Constants.bitmap=mCropView.getCroppedBitmap();
    	setResult(Constants.RequestCode_Croper);
        finish();
    }
    
    
    
    
    
    
    
    
}
