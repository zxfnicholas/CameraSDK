package com.muzhi.camerasdk.example;


import com.muzhi.camerasdk.model.CameraSdkParameterInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private RadioGroup mChoiceMode, mShowCamera,mCrop,mFilter;
    private EditText mRequestNum;
    private CameraSdkParameterInfo mCameraSdkParameterInfo=new CameraSdkParameterInfo();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		((TextView)findViewById(R.id.camerasdk_actionbar_title)).setText(getString(R.string.app_name));
		
        mChoiceMode = (RadioGroup) findViewById(R.id.choice_mode);
        mShowCamera = (RadioGroup) findViewById(R.id.show_camera);
        mRequestNum = (EditText) findViewById(R.id.request_num);
        mCrop = (RadioGroup) findViewById(R.id.rg_crop);
        mFilter = (RadioGroup) findViewById(R.id.rg_filter);
        
        mChoiceMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.multi){
                    mRequestNum.setEnabled(true);
                }else{
                    mRequestNum.setEnabled(false);
                    mRequestNum.setText("");
                }
            }
        });
        
        findViewById(R.id.button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				boolean mode_flag=mChoiceMode.getCheckedRadioButtonId() == R.id.single;
				mCameraSdkParameterInfo.setSingle_mode(mode_flag);
				
				boolean camera_flag=mShowCamera.getCheckedRadioButtonId() == R.id.show;
				mCameraSdkParameterInfo.setShow_camera(camera_flag);
               
                
                if(!TextUtils.isEmpty(mRequestNum.getText())){
                   int maxNum = Integer.valueOf(mRequestNum.getText().toString());
                    mCameraSdkParameterInfo.setMax_image(maxNum);
                }
                
                boolean crop_flag=mCrop.getCheckedRadioButtonId()==R.id.crop_yes;
                mCameraSdkParameterInfo.setCroper_image(crop_flag);
                
                if(crop_flag){
                	//暂时只支持单张即Single_mode模式必须为true
                	if(!mCameraSdkParameterInfo.isSingle_mode()){
                		Toast.makeText(MainActivity.this, "选择模式必须为单选", Toast.LENGTH_LONG).show();;
                		return;
                	}
                }
                
                boolean filter_flag=mFilter.getCheckedRadioButtonId()==R.id.filter_yes;
                mCameraSdkParameterInfo.setFilter_image(filter_flag);
                
                if(filter_flag){
                	//暂时只支持单张即Single_mode模式必须为true
                	if(!mCameraSdkParameterInfo.isSingle_mode()){
                		Toast.makeText(MainActivity.this, "选择模式必须为单选", Toast.LENGTH_LONG).show();;
                		return;
                	}
                }
                
                Intent intent = new Intent();
                intent.setClassName(getApplication(), "com.muzhi.camerasdk.PhotoPickActivity");
                Bundle b=new Bundle();
                b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
                intent.putExtras(b);
                startActivityForResult(intent, CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY);
                
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY) {
			if(data!=null){
				Intent intent = new Intent(MainActivity.this, ResultActivity.class);
				intent.putExtras(data.getExtras());
				startActivity(intent);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
