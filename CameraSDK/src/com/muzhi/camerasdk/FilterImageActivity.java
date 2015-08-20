package com.muzhi.camerasdk;

import java.util.ArrayList;

import com.muzhi.camerasdk.adapter.Filter_Effect_Adapter;
import com.muzhi.camerasdk.adapter.Filter_Sticker_Adapter;
import com.muzhi.camerasdk.adapter.SmallThumbAdapter;
import com.muzhi.camerasdk.model.*;
import com.muzhi.camerasdk.ui.fragment.EfectFragment;
import com.muzhi.camerasdk.utils.FilterUtils;
import com.muzhi.mtools.camerasdk.view.HorizontalListView;
import com.muzhi.mtools.filter.*;
import com.muzhi.mtools.filter.util.ImageFilterTools;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class FilterImageActivity extends BaseActivity {

	public int RequestCode = 111;

	
	private CameraSdkParameterInfo mCameraSdkParameterInfo=new CameraSdkParameterInfo();
	
	private HorizontalListView effect_listview, sticker_listview,images_listview;
	
	private TextView tab_effect, tab_sticker,txt_cropper,tab_sticker_library;
	private RelativeLayout loading_layout;// 等待框
	private SeekBar mSeekBar;		
	

	private SmallThumbAdapter iAdapter;
	private Filter_Effect_Adapter eAdapter;
	private Filter_Sticker_Adapter sAdapter;

	private ArrayList<Filter_Effect_Info> effect_list=new ArrayList<Filter_Effect_Info>(); //特效
	private ArrayList<Filter_Sticker_Info> stickerList = new ArrayList<Filter_Sticker_Info>();
	private ArrayList<String> imageList;
	
	private int color_selected = R.color.camerasdk_filter_tab_selected; //0xffffd83a;
	private int color_unselected = R.color.camerasdk_filter_tab_unselected; //0xffededed;


	public static Filter_Sticker_Info mSticker = null; // 从贴纸库过来的贴纸

	protected final int GETSTICKER_SUCC = 0;
	protected final int DOWNLOADSTICKER_SUCC = 1;
	
	private FragmentManager fragmentManager;
	private EfectFragment mEfectFragment;

	
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
		
		tab_effect = (TextView) findViewById(R.id.txt_effect);
		tab_sticker = (TextView) findViewById(R.id.txt_sticker);
		txt_cropper = (TextView) findViewById(R.id.txt_cropper);
		tab_sticker_library = (TextView) findViewById(R.id.camerasdk_title_txv_right_text);
		tab_sticker_library.setText("完成");
		tab_sticker_library.setVisibility(View.VISIBLE);
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
				mEfectFragment.addEffect(filter);
			}
		});

		sticker_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String path=stickerList.get(arg2).local_path;
				int drawableId=stickerList.get(arg2).drawableId;
				mEfectFragment.addSticker(drawableId, path);
			}
		});
		images_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				mEfectFragment.changeImage(imageList.get(position));
				iAdapter.setSelected(position);
				
				final int tmpint = position;
				final int tmpitem = view.getWidth();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						images_listview.scrollTo(tmpitem * (tmpint - 1) - tmpitem / 4);
						//images_listview.scrollTo(tmpitem * tmpint);
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
		
		mEfectFragment=EfectFragment.newInstance(imageList,flag);
		fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.fragment_container, mEfectFragment).commit();
		
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
				ArrayList<String> list=mEfectFragment.getAllPhoto();
				PhotoPickActivity.instance.getFilterComplate(list);
				finish();
			}
		};
		Handler handler = new Handler();
		handler.postDelayed(runnable, delayMillis);		
	}
	

}
