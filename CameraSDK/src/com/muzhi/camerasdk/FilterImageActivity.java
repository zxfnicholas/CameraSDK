package com.muzhi.camerasdk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.muzhi.camerasdk.adapter.Filter_Effect_Adapter;
import com.muzhi.camerasdk.adapter.Filter_Sticker_Adapter;
import com.muzhi.camerasdk.model.CameraSdkParameterInfo;
import com.muzhi.camerasdk.model.Filter_Effect_Info;
import com.muzhi.camerasdk.model.Filter_LocalPhotoInfo;
import com.muzhi.camerasdk.model.Filter_Sticker_Info;
import com.muzhi.camerasdk.model.LocalStatic;
import com.muzhi.camerasdk.utils.ImageFilterTools;
import com.muzhi.camerasdk.utils.ImageFilterTools.OnGpuImageFilterChosenListener;
import com.muzhi.camerasdk.view.HSuperImageView;
import com.muzhi.camerasdk.view.HorizontalListView;
import com.muzhi.mtools.filter.*;
import com.muzhi.mtools.filter.GPUImage.OnPictureSavedListener;
import com.muzhi.mtools.filter.util.GPUImageFilterUtil;
import com.muzhi.mtools.utils.ImageUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FilterImageActivity extends BaseActivity {

	public int RequestCode = 111;

	private final String folderName="CameraSDK";
	private CameraSdkParameterInfo mCameraSdkParameterInfo=new CameraSdkParameterInfo();
	
	private HorizontalListView effect_listview, sticker_listview;
	private GPUImageView effect_main; // 需要修改的图像
	private String imagePath; // 要编辑的图片的URL
	
	private TextView tab_effect, tab_sticker;
	private ProgressBar edit_bar;// 等待框

	private RelativeLayout pictureUrl_layout;
	private LinearLayout content_layout;
	private ImageView pictureUrl_img;
	private TextView txt_loading;
	private Button btn_ok;
	private TextView tab_sticker_library;
	
	public static RelativeLayout edit_img; // 包含着编辑图片的ViewGroup
	public static HSuperImageView imageView; // 需要添加的贴纸

	private GPUImageFilter mFilter;

	public static ArrayList<HSuperImageView> sticklist; // 保存贴纸图片的集合
	private int sticknum = -1;// 贴纸添加的序号

	public static Bitmap editbmp; // 编辑界面的贴纸图片
	
	
	private Filter_Effect_Adapter eAdapter;
	private Filter_Sticker_Adapter sAdapter;

	private ArrayList<Filter_Sticker_Info> stickerList = new ArrayList<Filter_Sticker_Info>();
	private ArrayList<Filter_Sticker_Info> stickerTempList = new ArrayList<Filter_Sticker_Info>(); // 贴纸列表

	private int color_selected = R.color.camerasdk_filter_tab_selected; //0xffffd83a;
	private int color_unselected = R.color.camerasdk_filter_tab_unselected; //0xffededed;

	// private AnimationController mController;

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
				showImg(msg.arg1, (HSuperImageView) msg.obj);
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
		
		sticklist = new ArrayList<HSuperImageView>();

		try{
			mCameraSdkParameterInfo=(CameraSdkParameterInfo)getIntent().getSerializableExtra(CameraSdkParameterInfo.EXTRA_PARAMETER);
			imagePath=mCameraSdkParameterInfo.getImage_list().get(0);
		}
		catch(Exception e){}
		
		initView();
		initControlView();
		initPictureView();

		getStickerList();
	}

	private void initView() {
		tab_effect = (TextView) findViewById(R.id.txt_effect);
		tab_sticker = (TextView) findViewById(R.id.txt_sticker);
		tab_sticker_library = (TextView) findViewById(R.id.camerasdk_title_txv_right_text);
		tab_sticker_library.setText("完成");
		tab_sticker_library.setVisibility(View.VISIBLE);
		
		

		edit_img = (RelativeLayout) findViewById(R.id.edit_img);
		edit_bar = (ProgressBar) findViewById(R.id.edit_bar);

		// 获取屏幕宽度和高度
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.widthPixels;

		RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) edit_img.getLayoutParams();
		rlp.height = screenWidth;
		rlp.width = screenWidth;
		edit_img.setLayoutParams(rlp);

		imageView = new HSuperImageView(this);
		effect_main = (GPUImageView) findViewById(R.id.effect_main);
		
		if(mCameraSdkParameterInfo!=null && !mCameraSdkParameterInfo.isCroper_image()){
			Bitmap bitmap=ImageUtils.getBitmap(imagePath);
			effect_main.setImage(bitmap);
		}
		else{
			effect_main.setImage(LocalStatic.bitmap);
		}
		
		initEvent();
		
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
				next();
			}
		});
		edit_img.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				HSuperImageView.stickflag = false;
				//点击屏幕背景   将所有贴纸的外框全部去掉贴纸对象全部存在 没有回收 后期对这个问题再进行优化
				for (int i = 0; i < sticklist.size(); i++) {
					System.out.println(sticklist.get(i));
					sticklist.get(i).invalidate();
				}
				return false;
			}
		});
	}
	
	
	private void initControlView() {
		effect_listview = (HorizontalListView) findViewById(R.id.effect_listview);
		sticker_listview = (HorizontalListView) findViewById(R.id.sticker_listview);

		eAdapter = new Filter_Effect_Adapter(this, getEffects());
		sAdapter = new Filter_Sticker_Adapter(this, stickerList);

		effect_listview.setAdapter(eAdapter);
		sticker_listview.setAdapter(sAdapter);

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

				if (arg2 > 0) {
					// 里面与滤镜相关的图片一定要放到指定文件夹drawable-nodpi
					ImageFilterTools.showDialog(FilterImageActivity.this, new OnGpuImageFilterChosenListener() {
						@Override
						public void onGpuImageFilterChosenListener(GPUImageFilter filter) {
							switchFilterTo(filter);
							effect_main.requestRender();
						}
					}, arg2 - 1);
				} else {
				}
			}
		});

		sticker_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				showPicture(arg2);
			}
		});
	}

	private void initPictureView() {
		pictureUrl_layout = (RelativeLayout) findViewById(R.id.pictureUrl_layout);
		content_layout = (LinearLayout) findViewById(R.id.content_layout);
		pictureUrl_img = (ImageView) findViewById(R.id.pictureUrl_img);
		txt_loading = (TextView) findViewById(R.id.txt_loading);
		btn_ok = (Button) findViewById(R.id.btn_ok);

		int w = GPUImageFilterUtil.getscreenwidth(getApplicationContext()) * 3 / 4;
		RelativeLayout.LayoutParams rlp = (android.widget.RelativeLayout.LayoutParams) content_layout.getLayoutParams();
		rlp.width = w;
		content_layout.setLayoutParams(rlp);

		LayoutParams lp = pictureUrl_img.getLayoutParams();
		lp.height = w;
		lp.width = w;
		pictureUrl_img.setLayoutParams(lp);

		pictureUrl_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mController.upOut(pictureUrl_layout, 300, 0);
			}
		});
	}

	private void showPicture(final int position) {
		
		setSticker(position);
	}

	// 添加贴纸图片到编辑图片中
	private void setSticker(int postion) {
		HSuperImageView.stickflag = true;
		sticknum++;
		HSuperImageView imageView = new HSuperImageView(FilterImageActivity.this, sticknum);
		setImg(postion, imageView);
		sticklist.add(imageView);
		edit_img.addView(imageView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	// 滤镜的列表项赋值	
	private List<Filter_Effect_Info> getEffects() {
		List<Filter_Effect_Info> list = new ArrayList<Filter_Effect_Info>();

		list.add(new Filter_Effect_Info("原图", R.drawable.effect_original));
		list.add(new Filter_Effect_Info("罗马夏日", R.drawable.effect_amaro));
		list.add(new Filter_Effect_Info("富士山下", R.drawable.effect_hudson));
		list.add(new Filter_Effect_Info("塞纳河畔", R.drawable.effect_valencia));
		list.add(new Filter_Effect_Info("光辉岁月", R.drawable.effect_toncurve));
		list.add(new Filter_Effect_Info("自由", R.drawable.effect_sierra));
		list.add(new Filter_Effect_Info("回忆", R.drawable.effect_hefe));
		list.add(new Filter_Effect_Info("离人醉", R.drawable.effect_rise));
		list.add(new Filter_Effect_Info("日落大道", R.drawable.effect_earlybird));
		list.add(new Filter_Effect_Info("秋意浓", R.drawable.effect_brannan));
		list.add(new Filter_Effect_Info("黑白故事", R.drawable.effect_inkwell));
		list.add(new Filter_Effect_Info("罗曼蒂克", R.drawable.effect_walden));
		list.add(new Filter_Effect_Info("不羁", R.drawable.effect_xproll));
		list.add(new Filter_Effect_Info("暗里着迷", R.drawable.effect_lookup));

		return list;
	}

	// 贴纸的列表项赋值
	private void getStickerList() {
				
		//本地贴纸
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_1));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_2));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_3));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_4));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_5));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_6));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_7));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_8));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_9));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_10));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_11));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_12));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_13));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_14));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_15));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_16));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_17));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_18));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_19));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_20));
		
		
		//stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_13));
		/*AssetManager assetManager =getApplication().getAssets();
    	InputStream inputStream;
    	String[] files = null;

    	  try {
    		  files = assetManager.list("sticker");
    		  for(int i=0;i<=files.length;i++){
    	    		String fileName=files[i];
    	    		try{
    	    			inputStream = assetManager.open(fileName);
    	    			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
    	    			stickerList.add(new Filter_Sticker_Info(bitmap));
    	    			inputStream.close();
    	    		}
    	    		catch (IOException ignored) {
    	    			Log.v("sticker", ignored.getMessage());
    	            }
    	    	}    	
    	  } 
    	  catch (IOException e) {
    		  Log.e("tag", e.getMessage());
    	  }*/
		
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	

	@Override
	public void onResume() {
		super.onResume();
		if (mSticker != null) {
			// addSticker(mSticker);
		}
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

	// 从贴纸库界面跳转过来 增加贴纸
	/*
	 * private void addSticker(final Sticker mSticker) {
	 * Utils.downLoadFile(mSticker, mSticker.pasterUrl); File file = new
	 * File(mSticker.pasterLocalpath); Bitmap bmp = Util.decodeFile(file,
	 * Util.getscreenwidth(getApplicationContext())); if (bmp != null) {
	 * imageView.init(bmp);// 设置控件图片 sticknum++; //贴纸个数要增加
	 * sticklist.add(imageView); edit_img.addView(imageView, new LayoutParams(
	 * LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)); } else
	 * Toast.makeText(this, "加载贴纸失败", Toast.LENGTH_SHORT).show(); }
	 */

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { menu.clear();
	 * menu.add(0, MenuInfo.TOPMENU_NEXT, 0, "下一步").setShowAsAction(
	 * MenuItem.SHOW_AS_ACTION_ALWAYS); return true; }
	 */

	/*
	 * @Override public boolean onOptionsItemSelected(final MenuItem item) {
	 * switch (item.getItemId()) { case MenuInfo.TOPMENU_NEXT: next(); break;
	 * 
	 * case android.R.id.home: onBackPressed(); break; } return true; }
	 */

	//点击下一步后进入的界面
	private void next() {
		HSuperImageView.stickflag = false;
		edit_bar.setVisibility(View.VISIBLE);
		getViewBitmap();

		// 隐蔽所有贴纸
		/*for (int i = 0; i < sticklist.size(); i++) {
			sticklist.get(i).setVisibility(View.INVISIBLE);
		}*/
	}

	private void setImg(final int position, final HSuperImageView imageView) {
		File file = new File(stickerList.get(position).pasterLocalpath);
		if (file.exists()) {
			showImg(position, imageView);
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

	private void showImg(int position, HSuperImageView imageView) {
		//File file = new File(stickerList.get(position).pasterLocalpath);
		//Bitmap bmp = Util.decodeFile(file, Util.getscreenwidth(getApplicationContext()));
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), stickerList.get(position).drawableId);
		//Bitmap bmp=stickerList.get(position).bitmap;
		if (bmp != null)
			imageView.init(bmp);// 设置控件图片
		else
			Toast.makeText(this, "加载贴纸失败", Toast.LENGTH_SHORT).show();
	}

	// 设置Filter啥玩意啊 应该是给图像上滤镜吧 里面的原理也不太清楚
	private void switchFilterTo(final GPUImageFilter filter) {
		if (mFilter == null || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
			mFilter = filter;
			effect_main.setFilter(mFilter);
			// mFilterAdjuster = new FilterAdjuster(mFilter);
		}
	}

	// 由于GPUImageView是继承自GLSurfaceView 普通的截取无效 只能分别截取 再进行合成才行
	private void getViewBitmap() {

		edit_bar.setVisibility(View.INVISIBLE);
		edit_img.setDrawingCacheEnabled(true);// 得到上面的贴纸图片
		Bitmap bitmap = Bitmap.createBitmap(edit_img.getDrawingCache());
		edit_img.destroyDrawingCache();// 清缓存
		int w = edit_img.getWidth();
		editbmp = Bitmap.createBitmap(bitmap, 0, 0, w, w);

		// 保存底部效果图
		//String fileName = System.currentTimeMillis() + ".jpg";
		//effect_main.saveToPictures(folderName, fileName, new MyListeners());
		
		Bitmap fBitmap=effect_main.getBitmapWithFilterApplied();
		bitmap=Bitmap.createBitmap(fBitmap.getWidth(), fBitmap.getHeight(),Config.ARGB_8888);
		Canvas cv = new Canvas(bitmap);
		cv.drawBitmap(fBitmap, 0, 0, null);
		cv.drawBitmap(editbmp, 0, 0, null);
		
		saveImage(bitmap);
	}

	public class MyListeners implements OnPictureSavedListener {
		@Override
		public void onPictureSaved(String path, Uri uri) {
			edit_bar.setVisibility(View.INVISIBLE);
			saveBitmap(path, uri);
		}
	}

	private void saveBitmap(String path, Uri uri) {
		Bitmap bmp = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		try {
			bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// 图像的合成 将第2个Bitmap画到第一个Bitmap上面
		Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Config.ARGB_8888);
		Canvas cv = new Canvas(bitmap);
		cv.drawBitmap(bmp, 0, 0, null);
		cv.drawBitmap(editbmp, 0, 0, null);

		saveImage(bitmap);

		/*bmp.recycle();
		new File(path).delete();
		MediaScannerConnection.scanFile(Activity_Edit.this, new String[] { new File(path).toString() }, null, null);*/
	}

	private void saveImage(final Bitmap image) {
		String fileName = System.currentTimeMillis() + ".jpg";
		File parentpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File file = new File(parentpath, folderName+"/" + fileName);
		file.getParentFile().mkdirs();
		try {
			image.compress(CompressFormat.JPEG, 100, new FileOutputStream(file));
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
