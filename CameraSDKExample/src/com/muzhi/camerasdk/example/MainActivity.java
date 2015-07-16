package com.muzhi.camerasdk.example;

import java.util.ArrayList;

import com.muzhi.camerasdk.utils.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final int REQUEST_IMAGE = 2;

    private TextView mResultText;
    private RadioGroup mChoiceMode, mShowCamera;
    private EditText mRequestNum;

    private ArrayList<String> mSelectPath;
    private int maxNum = 9;
	private boolean showCamera;
	private boolean selectedMode=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		((TextView)findViewById(R.id.camerasdk_actionbar_title)).setText(getString(R.string.app_name));
		
		mResultText = (TextView) findViewById(R.id.result);
        mChoiceMode = (RadioGroup) findViewById(R.id.choice_mode);
        mShowCamera = (RadioGroup) findViewById(R.id.show_camera);
        mRequestNum = (EditText) findViewById(R.id.request_num);

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
                if(mChoiceMode.getCheckedRadioButtonId() == R.id.single){
                    selectedMode = false;
                }

                showCamera = mShowCamera.getCheckedRadioButtonId() == R.id.show;

                
                if(!TextUtils.isEmpty(mRequestNum.getText())){
                    maxNum = Integer.valueOf(mRequestNum.getText().toString());
                }
                
               /* Intent intent = new Intent(); 
        		intent.setClassName(getApplication(), "com.muzhi.camerasdk.PhotoPickActivity"); */
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra(CommonDefine.EXTRA_SHOW_CAMERA, showCamera);// 是否显示拍照按钮
                intent.putExtra(CommonDefine.EXTRA_SELECT_COUNT_MAX, maxNum); // 最大可选择图片数量
                intent.putExtra(CommonDefine.EXTRA_SELECT_MODE_SINGLE, selectedMode);// 选择模式(多选)
                if(mSelectPath != null && mSelectPath.size()>0){
                    intent.putExtra(CommonDefine.EXTRA_IMAGES_LIST, mSelectPath);// 默认选择的图片列表
                }
        		startActivityForResult(intent, CommonDefine.TAKE_PICTURE_FROM_GALLERY);
                
        		
                
			}
		});

	}

	/*@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == CommonDefine.TAKE_PICTURE_FROM_GALLERY) {
			if(data!=null){
				Intent intent = new Intent(MainActivity.this, ResultActivity.class);
				intent.putExtras(data.getExtras());
				intent.putExtra(CommonDefine.EXTRA_SHOW_CAMERA, showCamera);// 是否显示拍照按钮
                intent.putExtra(CommonDefine.EXTRA_SELECT_COUNT_MAX, maxNum); // 最大可选择图片数量
                intent.putExtra(CommonDefine.EXTRA_SELECT_MODE_SINGLE, selectedMode);// 选择模式(多选)
				intent.putExtra("maxImageSize", maxNum);
				startActivity(intent);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}*/
}
