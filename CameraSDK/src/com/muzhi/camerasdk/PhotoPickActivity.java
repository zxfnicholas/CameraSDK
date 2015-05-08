package com.muzhi.camerasdk;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.muzhi.camerasdk.adapter.PhotoAdapter;
import com.muzhi.camerasdk.model.ImageBucket;
import com.muzhi.camerasdk.model.MediaStoreCursorHelper;
import com.muzhi.camerasdk.utils.AlbumHelper;
import com.muzhi.camerasdk.utils.CommonDefine;
import com.muzhi.camerasdk.utils.MResource;

public class PhotoPickActivity extends BaseActivity {

	
	private ArrayList<String> dataList;
	private HashMap<String, ImageView> hashMap = new HashMap<String, ImageView>();
	private ArrayList<String> selectedDataList = new ArrayList<String>();
	private PhotoAdapter gridImageAdapter;
	
	private String defaultTitle;
	private boolean booleanExtra;
	
	private GridView gridView;
	
	private ProgressBar progressBar;	
	private LinearLayout selectedImageLayout;
	private TextView actionbarTitle,button_complate,button_ablum,button_cancel;
	private HorizontalScrollView scrollview;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	
		int resId=MResource.getIdByName(this,MResource.layout, "camerasdk_activity_photopick");
		
		if (resId > 0) {
			setContentView(resId);
			initView();
			/*AlbumHelper helper = AlbumHelper.getHelper();
			helper.init(getApplicationContext());
			this.dataList=helper.getAllPhoto();*/
			
			dataList=new ArrayList<String>();
			ArrayList<ImageBucket> list=MediaStoreCursorHelper.queryAllPhoto(this);
			for(ImageBucket ib : list){
				dataList.add(ib.bucketThumb);
			}
			
			updateList(getIntent());
		}
		
	}

	
	private void initView(){
			
		gridView = (GridView) findViewById(MResource.getIdByName(getApplication(),MResource.id,"myGrid"));
		actionbarTitle = (TextView) findViewById(MResource.getIdByName(getApplication(),MResource.id,"camerasdk_actionbar_title"));
		selectedImageLayout = (LinearLayout) findViewById(MResource.getIdByName(getApplication(),MResource.id,"selected_image_layout"));
		scrollview = (HorizontalScrollView) findViewById(MResource.getIdByName(getApplication(),MResource.id,"scrollview"));
		button_complate = (TextView) findViewById(MResource.getIdByName(getApplication(),MResource.id,"button_complate"));
		button_ablum = (TextView) findViewById(MResource.getIdByName(getApplication(),MResource.id,"camerasdk_title_txv_left_text"));
		button_cancel = (TextView) findViewById(MResource.getIdByName(getApplication(),MResource.id,"camerasdk_title_txv_right_text"));
		
		button_ablum.setText(MResource.getIdByName(getApplication(),MResource.string,"camerasdk_album_button"));
		button_cancel.setText(MResource.getIdByName(getApplication(),MResource.string,"camerasdk_cancel"));
		button_cancel.setVisibility(View.VISIBLE);
	
	}
	
	
	private void updateList(Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			ArrayList<String> selList1 = (ArrayList<String>) bundle.getSerializable(CommonDefine.IMAGES_LIST);
			ArrayList<String> pathList = bundle.getStringArrayList("listPath");
			ArrayList<String> selList2 = bundle.getStringArrayList("selectedDataList");
			defaultTitle = bundle.getString("name");
			
			if (pathList != null) {
				dataList = pathList;
			}
			
			if (selList2 != null) {
				selectedDataList = selList2;
			} else if (selList1 != null) {
				selectedDataList = selList1;
			}
			booleanExtra = bundle.getBoolean("album");
		}
		init();
		initListener();
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode == -1 && data != null) {
			updateList(data);
		}
	}

	private void init() {
		
		if(!TextUtils.isEmpty(defaultTitle)){
			actionbarTitle.setText(defaultTitle);
		} else {
			int resid=MResource.getIdByName(this, MResource.string, "camerasdk_album_all");		
			actionbarTitle.setText(resid);
		}
		gridImageAdapter = new PhotoAdapter(this, dataList, selectedDataList, loader, options);
		gridView.setAdapter(gridImageAdapter);
		initSelectImage();
		
		button_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mActThis.finish();
			}
		});
		button_ablum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActThis, AlbumActivity.class);
				intent.putExtra("selectedDataList", selectedDataList);
				intent.putExtra("album", booleanExtra);
				startActivityForResult(intent, 0);
				overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
			}
		});
		button_complate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent;
				intent = new Intent();
				intent.putExtra(CommonDefine.IMAGES_LIST, selectedDataList);
				setResult(1, intent);
				mActThis.finish();
			}
		});
	}

	private void initSelectImage() {
		if (selectedDataList == null)
			return;
		selectedImageLayout.removeAllViews();
		for (final String path : selectedDataList) {
			ImageView imageView = (ImageView) LayoutInflater.from(PhotoPickActivity.this).inflate(R.layout.camerasdk_item_choose_imageview, selectedImageLayout, false);
			selectedImageLayout.addView(imageView);
			hashMap.put(path, imageView);
			loader.displayImage("file://" + path, imageView, options);
			imageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					removePath(path);
					gridImageAdapter.notifyDataSetChanged();
				}
			});
		}
		button_complate.setText("完成(" + selectedDataList.size() + ")");
	}

	private void initListener() {

		gridImageAdapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(final CheckBox toggleButton, int position, final String path, boolean isChecked) {
					if (selectedDataList.size() >= 9) {
						toggleButton.setChecked(false);
						if (!removePath(path)) {
							Toast.makeText(PhotoPickActivity.this, "只能选择9张图片", Toast.LENGTH_SHORT).show();
						}
						return;
					}
					if (isChecked) {
						if (!hashMap.containsKey(path)) {
							ImageView imageView = (ImageView) LayoutInflater.from(PhotoPickActivity.this).inflate(R.layout.camerasdk_item_choose_imageview, selectedImageLayout, false);
							selectedImageLayout.addView(imageView);
							imageView.postDelayed(new Runnable() {

								@Override
								public void run() {

									int off = selectedImageLayout.getMeasuredWidth() - scrollview.getWidth();
									if (off > 0) {
										scrollview.smoothScrollTo(off, 0);
									}
								}
							}, 100);

							hashMap.put(path, imageView);
							selectedDataList.add(path);
//							DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.group_item_pic_bg).cacheInMemory(true).cacheOnDisc(true).build();
							loader.displayImage("file://" + path, imageView, options);
							imageView.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									toggleButton.setChecked(false);
									removePath(path);
								}
							});
							button_complate.setText("完成(" + selectedDataList.size() + ")");
						}
					} else {
						removePath(path);
					}
				
			}
		});

		
	}

	private boolean removePath(String path) {
		if (hashMap.containsKey(path)) {
			selectedImageLayout.removeView(hashMap.get(path));
			hashMap.remove(path);
			removeOneData(selectedDataList, path);
			button_complate.setText("完成(" + selectedDataList.size() + ")");
			return true;
		} else {
			return false;
		}
	}

	private void removeOneData(ArrayList<String> arrayList, String s) {
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).equals(s)) {
				arrayList.remove(i);
				return;
			}
		}
	}

}
