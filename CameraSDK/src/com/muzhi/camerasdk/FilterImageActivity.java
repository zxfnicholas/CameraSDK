package com.muzhi.camerasdk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.muzhi.camerasdk.adapter.Filter_Effect_Adapter;
import com.muzhi.camerasdk.adapter.Filter_Sticker_Adapter;
import com.muzhi.camerasdk.model.CameraSdkParameterInfo;
import com.muzhi.camerasdk.model.*;
import com.muzhi.camerasdk.model.LocalStatic;
import com.muzhi.camerasdk.utils.FilterUtils;
import com.muzhi.camerasdk.utils.GPUImageFilterTools;
import com.muzhi.camerasdk.view.HSuperImageView;
import com.muzhi.camerasdk.view.HorizontalListView;
import com.muzhi.mtools.filter.*;
import com.muzhi.mtools.utils.ImageUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FilterImageActivity extends BaseActivity {

	public int RequestCode = 111;

	private final String folderName="CameraSDK";
	private CameraSdkParameterInfo mCameraSdkParameterInfo=new CameraSdkParameterInfo();
	
	private HorizontalListView effect_listview, sticker_listview;
	private GPUImageView effect_main; // 需要修改的图像
	private String sourceImagePath; // 要编辑的图片的URL
	private Bitmap sourceBitmap;
	
	private TextView tab_effect, tab_sticker;
	private ProgressBar progress_bar;// 等待框

	/*private RelativeLayout pictureUrl_layout;
	private LinearLayout content_layout;
	private ImageView pictureUrl_img;
	private TextView txt_loading;
	private Button btn_ok;*/
	private TextView tab_sticker_library;
	
	public static HSuperImageView imageView; // 需要添加的贴纸


	public static ArrayList<HSuperImageView> sticklist; // 保存贴纸图片的集合
	private int sticknum = -1;// 贴纸添加的序号

	public static Bitmap editbmp; // 编辑界面的贴纸图片
	
	
	private Filter_Effect_Adapter eAdapter;
	private Filter_Sticker_Adapter sAdapter;

	private ArrayList<Filter_Effect_Info> effect_list=new ArrayList<Filter_Effect_Info>(); //特效
	private ArrayList<Filter_Sticker_Info> stickerList = new ArrayList<Filter_Sticker_Info>();
	private ArrayList<Filter_Sticker_Info> stickerTempList = new ArrayList<Filter_Sticker_Info>(); // 使用到的贴纸列表

	private int color_selected = R.color.camerasdk_filter_tab_selected; //0xffffd83a;
	private int color_unselected = R.color.camerasdk_filter_tab_unselected; //0xffededed;


	public static Filter_Sticker_Info mSticker = null; // 从贴纸库过来的贴纸

	protected final int GETSTICKER_SUCC = 0;
	protected final int DOWNLOADSTICKER_SUCC = 1;
	
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GETSTICKER_SUCC:
				stickerList.clear();
				stickerList.addAll(stickerTempList);
				sAdapter.notifyDataSetChanged();
				break;
			case DOWNLOADSTICKER_SUCC:
				showStickerImg(msg.arg1, (HSuperImageView) msg.obj);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camerasdk_filter_image);
		showLeftIcon();
		setActionBarTitle("编辑图片");

		try{
			mCameraSdkParameterInfo=(CameraSdkParameterInfo)getIntent().getSerializableExtra(CameraSdkParameterInfo.EXTRA_PARAMETER);
			sourceImagePath=mCameraSdkParameterInfo.getImage_list().get(0);
		}
		catch(Exception e){}
		
		initView();
		initEvent();
		initData();
		
	}

	private void initView() {
		
		tab_effect = (TextView) findViewById(R.id.txt_effect);
		tab_sticker = (TextView) findViewById(R.id.txt_sticker);
		tab_sticker_library = (TextView) findViewById(R.id.camerasdk_title_txv_right_text);
		tab_sticker_library.setText("完成");
		tab_sticker_library.setVisibility(View.VISIBLE);
		
		effect_listview = (HorizontalListView) findViewById(R.id.effect_listview);
		sticker_listview = (HorizontalListView) findViewById(R.id.sticker_listview);

		progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

		imageView = new HSuperImageView(this);
		effect_main = (GPUImageView) findViewById(R.id.effect_main);
		
	}

	
	
	
	private void initEvent(){
		tab_effect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				effect_listview.setVisibility(View.VISIBLE);
				sticker_listview.setVisibility(View.INVISIBLE);
				tab_effect.setBackgroundResource(color_selected);
				tab_sticker.setBackgroundResource(color_unselected);
			}
		});
		tab_sticker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				effect_listview.setVisibility(View.INVISIBLE);
				sticker_listview.setVisibility(View.VISIBLE);
				tab_effect.setBackgroundResource(color_unselected);
				tab_sticker.setBackgroundResource(color_selected);
			}
		});
		tab_sticker_library.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				complate();
			}
		});
		effect_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//点击屏幕背景   将所有贴纸的外框全部去掉贴纸对象全部存在 没有回收 后期对这个问题再进行优化
				HSuperImageView.stickflag = false;
				for (int i = 0; i < sticklist.size(); i++) {
					System.out.println(sticklist.get(i));
					sticklist.get(i).invalidate();
				}
			}
		});
		
		effect_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				eAdapter.setSelectItem(arg2);

				final int tmpint = arg2;
				final int tmpitem = arg1.getWidth();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						effect_listview.scrollTo(tmpitem * (tmpint - 1) - tmpitem / 4);
					}
				}, 200);

				Filter_Effect_Info info= effect_list.get(arg2);
				GPUImageFilter filter = GPUImageFilterTools.createFilterForType(mContext,info.getFilterType());
				effect_main.setFilter(filter);
				effect_main.requestRender();
			}
		});

		sticker_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				setSticker(arg2);
			}
		});
	}
	

	private void initData(){
		
		if(mCameraSdkParameterInfo!=null && !mCameraSdkParameterInfo.isCroper_image()){
			sourceBitmap=ImageUtils.getBitmap(sourceImagePath);
			effect_main.setImage(sourceBitmap);
		}
		else{
			sourceBitmap=LocalStatic.bitmap;
		}
		
		float width = sourceBitmap.getWidth();
		float height = sourceBitmap.getHeight();
		float ratio = width / height;
		effect_main.setRatio(ratio);
		effect_main.setImage(sourceBitmap);
		
		
		
		sticklist = new ArrayList<HSuperImageView>();
		effect_list=FilterUtils.getEffectList();
		stickerList=FilterUtils.getStickerList();
		
		eAdapter = new Filter_Effect_Adapter(this,effect_list);
		sAdapter = new Filter_Sticker_Adapter(this, stickerList);
		
		effect_listview.setAdapter(eAdapter);
		sticker_listview.setAdapter(sAdapter);
		
		
	}
	
	

	// 添加贴纸图片到编辑图片中
	private void setSticker(int postion) {
		HSuperImageView.stickflag = true;
		sticknum++;
		HSuperImageView imageView = new HSuperImageView(FilterImageActivity.this, sticknum);
		setStickerImg(postion, imageView);
		sticklist.add(imageView);
		//effect_main.addView(imageView,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		effect_main.addView(imageView,new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}


	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == RequestCode) {
				// String url = data.getStringExtra("STICKER");
				// addSticker(url);
			}
		}
	}


	//点击下一步后进入的界面
	private void complate() {
		
		HSuperImageView.stickflag = false;
		progress_bar.setVisibility(View.VISIBLE);
		effect_main.setDrawingCacheEnabled(true);
		Bitmap editbmp = Bitmap.createBitmap(effect_main.getDrawingCache());
		
		try{
			Bitmap fBitmap=effect_main.capture();
			Bitmap bitmap=Bitmap.createBitmap(fBitmap.getWidth(), fBitmap.getHeight(),Config.ARGB_8888);
			Canvas cv = new Canvas(bitmap);
			cv.drawBitmap(fBitmap, 0, 0, null);
			cv.drawBitmap(editbmp, 0, 0, null);
			saveImage(bitmap);
		}
		catch(Exception e){}
	}

	private void setStickerImg(final int position, final HSuperImageView imageView) {
		File file = new File(stickerList.get(position).local_path);
		if (file.exists()) {
			showStickerImg(position, imageView);
		} else {
			new Thread() {
				public void run() {
					//Utils.downLoadFile(stickerList.get(position), stickerList.get(position).pasterUrl);
					Message msg = new Message();
					msg.what = DOWNLOADSTICKER_SUCC;
					msg.arg1 = position;
					msg.obj = imageView;
					handler.sendMessage(msg);
				}
			}.start();
		}
	}

	private void showStickerImg(int position, HSuperImageView imageView) {
		//File file = new File(stickerList.get(position).pasterLocalpath);
		//Bitmap bmp = Util.decodeFile(file, Util.getscreenwidth(getApplicationContext()));
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), stickerList.get(position).drawableId);
		if (bmp != null)
			imageView.init(bmp);// 设置控件图片
		else
			Toast.makeText(this, "加载贴纸失败", Toast.LENGTH_SHORT).show();
	}
	

	//最终合并生成图片
	private void saveImage(final Bitmap image) {
		String fileName = System.currentTimeMillis() + ".jpg";
		File parentpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File file = new File(parentpath, folderName+"/" + fileName);
		file.getParentFile().mkdirs();
		try {
			image.compress(CompressFormat.JPEG, 80, new FileOutputStream(file));
			MediaScannerConnection.scanFile(FilterImageActivity.this, new String[] { file.toString() }, null, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (file.exists()) {
			Filter_LocalPhotoInfo mLocalPhotoInfo = new Filter_LocalPhotoInfo(file.getAbsolutePath(), "file://"+ file.getAbsolutePath());
			//ResourceManager.bitmapsList.add(mLocalPhotoInfo);
		} else {
			Toast.makeText(FilterImageActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
		}
		image.recycle();
		
		PhotoPickActivity.instance.getForResultComplate(file.getAbsolutePath());
        finish();
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (LocalStatic.bitmap != null){
			LocalStatic.bitmap.recycle();
		}
	}

}
