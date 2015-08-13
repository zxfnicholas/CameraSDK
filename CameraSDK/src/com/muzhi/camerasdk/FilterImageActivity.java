package com.muzhi.camerasdk;

import java.util.ArrayList;

import com.muzhi.camerasdk.adapter.Filter_Effect_Adapter;
import com.muzhi.camerasdk.adapter.Filter_Sticker_Adapter;
import com.muzhi.camerasdk.adapter.FragmentViewPagerAdapter;
import com.muzhi.camerasdk.model.*;
import com.muzhi.camerasdk.ui.fragment.EfectFragment;
import com.muzhi.camerasdk.ui.fragment.EfectFragment.OnSlidingListener;
import com.muzhi.camerasdk.utils.FilterUtils;
import com.muzhi.camerasdk.view.CustomViewPager;
import com.muzhi.mtools.camerasdk.view.HSuperImageView;
import com.muzhi.mtools.camerasdk.view.HorizontalListView;
import com.muzhi.mtools.filter.*;
import com.muzhi.mtools.filter.util.ImageFilterTools;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class FilterImageActivity extends BaseActivity implements OnSlidingListener{

	public int RequestCode = 111;

	
	private CameraSdkParameterInfo mCameraSdkParameterInfo=new CameraSdkParameterInfo();
	private ArrayList<String> imageList;
	private HorizontalListView effect_listview, sticker_listview;
	
	
	
	private TextView tab_effect, tab_sticker,txt_cropper;
	private ProgressBar progress_bar;// 等待框
	private SeekBar mSeekBar;
	
	private CustomViewPager mViewPager;
	
	private ArrayList<Fragment> fragments;
	private int position=0;
	
	private TextView tab_sticker_library;		

	public static ArrayList<HSuperImageView> sticklist; // 保存贴纸图片的集合

	private FragmentViewPagerAdapter pAdapter;
	private Filter_Effect_Adapter eAdapter;
	private Filter_Sticker_Adapter sAdapter;

	private ArrayList<Filter_Effect_Info> effect_list=new ArrayList<Filter_Effect_Info>(); //特效
	private ArrayList<Filter_Sticker_Info> stickerList = new ArrayList<Filter_Sticker_Info>();

	private int color_selected = R.color.camerasdk_filter_tab_selected; //0xffffd83a;
	private int color_unselected = R.color.camerasdk_filter_tab_unselected; //0xffededed;


	public static Filter_Sticker_Info mSticker = null; // 从贴纸库过来的贴纸

	protected final int GETSTICKER_SUCC = 0;
	protected final int DOWNLOADSTICKER_SUCC = 1;
	
	

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
	
		if(imageList.size()>1){
			setActionBarTitle("编辑图片(1/"+imageList.size()+")");
		}
		else{
			setActionBarTitle("编辑图片");
		}
		
		initEvent();
		initData();
		
	}

	private void initView() {
		
		mViewPager= (CustomViewPager)findViewById(R.id.viewpager);
		tab_effect = (TextView) findViewById(R.id.txt_effect);
		tab_sticker = (TextView) findViewById(R.id.txt_sticker);
		txt_cropper = (TextView) findViewById(R.id.txt_cropper);
		tab_sticker_library = (TextView) findViewById(R.id.camerasdk_title_txv_right_text);
		tab_sticker_library.setText("完成");
		tab_sticker_library.setVisibility(View.VISIBLE);
		mSeekBar=(SeekBar)findViewById(R.id.seekBar);
		effect_listview = (HorizontalListView) findViewById(R.id.effect_listview);
		sticker_listview = (HorizontalListView) findViewById(R.id.sticker_listview);
		progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

		
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
		txt_cropper.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 裁剪图片
				Intent intent = new Intent();
                intent.setClassName(getApplication(), "com.muzhi.camerasdk.CropperImageActivity");
                Bundle b=new Bundle();
                b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
                intent.putExtras(b);
                startActivityForResult(intent, CameraSdkParameterInfo.TAKE_PICTURE_FROM_GALLERY);
			}
		});
		tab_sticker_library.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				complate();
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
				((EfectFragment)fragments.get(position)).addEffect(filter);
			}
		});

		sticker_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String path=stickerList.get(arg2).local_path;
				int drawableId=stickerList.get(arg2).drawableId;
				((EfectFragment)fragments.get(position)).addSticker(drawableId, path);
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
		
		fragments=new ArrayList<Fragment>();
		
		boolean flag=true;
		if(mCameraSdkParameterInfo!=null){
			if(mCameraSdkParameterInfo.isSingle_mode() && mCameraSdkParameterInfo.isCroper_image()){
				flag=false;//单条带裁剪
			}
		}
		if(flag){
			for(int i=0;i<imageList.size();i++){
				EfectFragment ef1=EfectFragment.newInstance(imageList.get(i));
				fragments.add(ef1);
			}
		}
		else{
			EfectFragment ef1=EfectFragment.newInstance("");
			fragments.add(ef1);
		}
		
		pAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPager,fragments);
		pAdapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener(){
            @Override
            public void onExtraPageSelected(int i) {
            	setActionBarTitle("编辑图片("+(i+1)+"/"+imageList.size()+")");
            	position=i;
            }
        });
        mViewPager.setAdapter(pAdapter);
        mViewPager.setOffscreenPageLimit(imageList.size());
        
		effect_list=FilterUtils.getEffectList();
		stickerList=FilterUtils.getStickerList();
		
		eAdapter = new Filter_Effect_Adapter(this,effect_list);
		sAdapter = new Filter_Sticker_Adapter(this, stickerList);
		
		effect_listview.setAdapter(eAdapter);
		sticker_listview.setAdapter(sAdapter);
		
		
	}

	@Override
	public void OnSlidingChanged(Boolean sliding) {
		mViewPager.setPagingEnabled(sliding);
	}
	

	private void complate(){
		
		progress_bar.setVisibility(View.VISIBLE);
		int size=fragments.size();
		ArrayList<String> list=new ArrayList<String>();
		for(int i=0;i<size;i++){
			String path=((EfectFragment)fragments.get(i)).getFilterImage();
			list.add(path);
		}
		PhotoPickActivity.instance.getFilterComplate(list);
        finish();
	}

	

}
