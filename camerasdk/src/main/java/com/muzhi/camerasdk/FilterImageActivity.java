package com.muzhi.camerasdk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.muzhi.camerasdk.adapter.Filter_Effect_Adapter;
import com.muzhi.camerasdk.adapter.Filter_Sticker_Adapter;
import com.muzhi.camerasdk.adapter.FragmentViewPagerAdapter;
import com.muzhi.camerasdk.adapter.SmallThumbAdapter;
import com.muzhi.camerasdk.library.filter.GPUImageFilter;
import com.muzhi.camerasdk.library.filter.util.ImageFilterTools;
import com.muzhi.camerasdk.library.views.HorizontalListView;
import com.muzhi.camerasdk.model.CameraSdkParameterInfo;
import com.muzhi.camerasdk.model.Constants;
import com.muzhi.camerasdk.model.Filter_Effect_Info;
import com.muzhi.camerasdk.model.Filter_Sticker_Info;
import com.muzhi.camerasdk.ui.fragment.EfectFragment;
import com.muzhi.camerasdk.utils.FilterUtils;
import com.muzhi.camerasdk.view.CustomViewPager;

import java.util.ArrayList;

public class FilterImageActivity extends BaseActivity {
	
	private CameraSdkParameterInfo mCameraSdkParameterInfo=new CameraSdkParameterInfo();
	
	private HorizontalListView effect_listview, sticker_listview,images_listview;
	
	private TextView tab_effect, tab_sticker,txt_cropper,btn_done,txt_enhance,txt_graffiti;
	private RelativeLayout loading_layout;// 等待框
	private SeekBar mSeekBar;		
	

	private SmallThumbAdapter iAdapter;
	private Filter_Effect_Adapter eAdapter;
	private Filter_Sticker_Adapter sAdapter;

	private ArrayList<Filter_Effect_Info> effect_list=new ArrayList<Filter_Effect_Info>(); //特效
	private ArrayList<Filter_Sticker_Info> stickerList = new ArrayList<Filter_Sticker_Info>();
	private ArrayList<String> imageList;
	
	public static Filter_Sticker_Info mSticker = null; // 从贴纸库过来的贴纸

	private FragmentViewPagerAdapter fAdapter;
	private CustomViewPager mViewPager;	
	private ArrayList<Fragment> fragments;
	
	private int current=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camerasdk_filter_image);
		showLeftIcon();

		try{
			mCameraSdkParameterInfo=(CameraSdkParameterInfo)getIntent().getSerializableExtra(CameraSdkParameterInfo.EXTRA_PARAMETER);
			imageList=mCameraSdkParameterInfo.getImage_list();
		}
		catch(Exception e){}
		
		initView();	
		
		TextView tv_title=(TextView)findViewById(R.id.camerasdk_actionbar_title);
		if(mCameraSdkParameterInfo.isSingle_mode()){
			setActionBarTitle("编辑图片");
		}
		else{
			tv_title.setVisibility(View.GONE);
			findViewById(R.id.images_layout).setVisibility(View.VISIBLE);
		}
		
		initEvent();
		initData();
		
	}

	private void initView() {
		
		mViewPager= (CustomViewPager)findViewById(R.id.viewpager);
		
		tab_effect = (TextView) findViewById(R.id.txt_effect);
		tab_sticker = (TextView) findViewById(R.id.txt_sticker);
		txt_cropper = (TextView) findViewById(R.id.txt_cropper);
		txt_enhance = (TextView) findViewById(R.id.txt_enhance);
		txt_graffiti = (TextView) findViewById(R.id.txt_graffiti);
		
		btn_done = (TextView) findViewById(R.id.camerasdk_title_txv_right_text);
		btn_done.setText("完成");
		btn_done.setVisibility(View.VISIBLE);
		mSeekBar=(SeekBar)findViewById(R.id.seekBar);
		effect_listview = (HorizontalListView) findViewById(R.id.effect_listview);
		sticker_listview = (HorizontalListView) findViewById(R.id.sticker_listview);
		images_listview = (HorizontalListView) findViewById(R.id.images_listview);
		loading_layout = (RelativeLayout) findViewById(R.id.loading);
		
	}

	private void initEvent(){
		tab_effect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				effect_listview.setVisibility(View.VISIBLE);
				sticker_listview.setVisibility(View.INVISIBLE);
				tab_effect.setTextColor(getResources().getColor(R.color.camerasdk_txt_selected));
				tab_sticker.setTextColor(getResources().getColor(R.color.camerasdk_txt_normal));
			}
		});
		tab_sticker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				effect_listview.setVisibility(View.INVISIBLE);
				sticker_listview.setVisibility(View.VISIBLE);
				tab_effect.setTextColor(getResources().getColor(R.color.camerasdk_txt_normal));
				tab_sticker.setTextColor(getResources().getColor(R.color.camerasdk_txt_selected));
			}
		});
		txt_cropper.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 裁剪图片
				Constants.bitmap=((EfectFragment)fragments.get(current)).getCurrentBitMap();
				Intent intent = new Intent();
				intent.setClassName(getApplication(), "com.muzhi.camerasdk.CutActivity");				
				startActivityForResult(intent,Constants.RequestCode_Croper);
			}
		});
		btn_done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 贴纸Tab
				complate();
			}
		});
		txt_enhance.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 图片增强
				Constants.bitmap=((EfectFragment)fragments.get(current)).getCurrentBitMap();
				Intent intent = new Intent();
				intent.setClassName(getApplication(), "com.muzhi.camerasdk.PhotoEnhanceActivity");				
				startActivityForResult(intent,Constants.RequestCode_Croper);
			}
		});
		txt_graffiti.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 涂鸦
				Constants.bitmap=((EfectFragment)fragments.get(current)).getCurrentBitMap();
				Intent intent = new Intent();
				intent.putExtra("path", imageList.get(0));
				intent.setClassName(getApplication(), "com.muzhi.camerasdk.GraffitiActivity");				
				startActivityForResult(intent,Constants.RequestCode_Croper);
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
				GPUImageFilter filter = ImageFilterTools.createFilterForType(mContext,info.getFilterType());
				((EfectFragment)fragments.get(current)).addEffect(filter);
			}
		});

		sticker_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				Filter_Sticker_Info info = stickerList.get(arg2);
				if(info.isLib()){
					Intent intent=new Intent(mContext,StickerActivity.class);
					startActivityForResult(intent, Constants.RequestCode_Sticker);
				}
				else{
					String path=stickerList.get(arg2).getLocal_path();
					int drawableId=stickerList.get(arg2).getDrawableId();
					((EfectFragment)fragments.get(current)).addSticker(drawableId, path);
				}
				
			}
		});
		images_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				mViewPager.setCurrentItem(position,false);
				fragments.get(position).onResume();
				fragments.get(current).onPause();
				current=position;
				
				iAdapter.setSelected(position);
				final int tmpint = position;
				final int tmpitem = view.getWidth();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						images_listview.scrollTo(tmpitem * (tmpint - 1) - tmpitem / 4);
					}
				}, 200);
				
			}
		});
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
				// TODO Auto-generated method stub
				/*if (mFilterAdjuster != null) {
					mFilterAdjuster.adjust(progress);
				}*/
				//effect_main.requestRender();
			}
		});
		
	}

	private void initData(){		
        
		boolean flag=false;
		if(mCameraSdkParameterInfo.isSingle_mode() && mCameraSdkParameterInfo.isCroper_image()){
			flag=true;
		}
		
		fragments=new ArrayList<Fragment>();
		for(int i=0;i<imageList.size();i++){
			EfectFragment ef1=EfectFragment.newInstance(imageList.get(i),flag);
			fragments.add(ef1);
		}
		
		fAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPager,fragments);
		mViewPager.setAdapter(fAdapter);
		mViewPager.setCurrentItem(0);
		//mViewPager.setOffscreenPageLimit(imageList.size());
		
		effect_list=FilterUtils.getEffectList();
		stickerList=FilterUtils.getStickerList();
		
		iAdapter = new SmallThumbAdapter(mContext, imageList);
		eAdapter = new Filter_Effect_Adapter(this,effect_list);
		sAdapter = new Filter_Sticker_Adapter(this, stickerList);
		
		images_listview.setAdapter(iAdapter);
		iAdapter.setSelected(0);
		effect_listview.setAdapter(eAdapter);
		sticker_listview.setAdapter(sAdapter);
		
	}


	private void complate(){
        
		loading_layout.setVisibility(View.VISIBLE);
		complate_runnable(3*1000);
		
	}
	
	private void complate_runnable(long delayMillis) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				
				ArrayList<String> list=new ArrayList<String>();
				if(mCameraSdkParameterInfo.getRet_type()==0){
					//返回一个路径
					for(int i=0;i<imageList.size();i++){
						Fragment mFragment=fragments.get(i);
						if(mFragment.isAdded()){
							String path=((EfectFragment)fragments.get(i)).getFilterImage();
							list.add(path);
						}
						else{
							list.add(imageList.get(i));
						}
					}
				}
				else{
					//保存bitmap
					CameraSdkParameterInfo.bitmap_list.clear();
					for(int i=0;i<imageList.size();i++){
						Fragment mFragment=fragments.get(i);
						if(mFragment.isAdded()){
							Bitmap bitmap=((EfectFragment)fragments.get(i)).getFilterBitmap();
							CameraSdkParameterInfo.bitmap_list.add(bitmap);
						}
					}
				}
				
				//如果是网络图片则直接返回
				if(mCameraSdkParameterInfo.is_net_path()){
	
					Bundle b=new Bundle();
					b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
					
					Intent intent = new Intent();
					intent.putExtras(b);
					setResult(RESULT_OK, intent);
			        finish();
				}
				else{
					PhotoPickActivity.instance.getFilterComplate(list);
				}
				
				
				
				finish();
				
			}
		};
		Handler handler = new Handler();
		handler.postDelayed(runnable, delayMillis);		
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constants.RequestCode_Croper) {
			//截图返回
			((EfectFragment)fragments.get(current)).setBitMap();
		}
		else if(resultCode == Constants.RequestCode_Sticker){
			if(data!=null){
				Filter_Sticker_Info info=(Filter_Sticker_Info)data.getSerializableExtra("info");
				((EfectFragment)fragments.get(current)).addSticker(0, info.getImage());
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
