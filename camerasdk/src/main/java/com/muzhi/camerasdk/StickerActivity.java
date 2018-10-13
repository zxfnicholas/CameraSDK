package com.muzhi.camerasdk;

import java.util.ArrayList;

import com.muzhi.camerasdk.library.utils.MResource;
import com.muzhi.camerasdk.ui.fragment.StickerHotFragment;
import com.muzhi.camerasdk.utils.MyFragmentPagerAdapter;
import com.muzhi.camerasdk.view.PagerSlidingTabStrip;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.WindowManager;


public class StickerActivity extends BaseActivity {
	
	
	private MyFragmentPagerAdapter fragmentAdapter;	
	private ArrayList<Fragment> fragments;
	
	private PagerSlidingTabStrip mTabStrip;
	private ViewPager mViewPager;
	
	private int current_fragment_index=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		int resId = MResource.getIdByName(this,MResource.layout, "camerasdk_activity_sticker"); 
        if(resId>0){
        	setContentView(resId);
        	showLeftIcon();
            setActionBarTitle("贴纸库");
            
            mTabStrip=(PagerSlidingTabStrip)findViewById(MResource.getIdRes(this, "pager_tab_strip"));
            mViewPager=(ViewPager)findViewById(MResource.getIdRes(this, "main_viewpager"));
            
        }
        initTabStrip();
        
		initData();
		
	}
	
	private void initTabStrip(){
		mTabStrip.setSelectedTextColor(Color.parseColor("#287fd0"));	//选中文字
		mTabStrip.setBackgroundColor(Color.parseColor("#0e1c26"));
		mTabStrip.setIndicatorColor(Color.parseColor("#287fd0"));  //底部选中条颜色
		mTabStrip.setTextColor(Color.parseColor("#ffffff"));//默认颜色
		mTabStrip.setDividerColor(Color.TRANSPARENT);
		//mTabStrip.setUnderlineColorResource(R.color.divider_color);
		//mTabStrip.setBackgroundResource(R.color.action_bar);
		//mTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1, getResources().getDisplayMetrics()));
		//mTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,2, getResources().getDisplayMetrics())); //底部选中条高度
		
		
	}

	
	private void initData(){		
		mViewPager.setOffscreenPageLimit(1);		
		fragments = new ArrayList<Fragment>();	
		fragments.add(new StickerHotFragment());
		fragments.add(new StickerHotFragment());
	
		fragmentAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments,new String[]{"热门", "全部"});
		
		mViewPager.setAdapter(fragmentAdapter);		
		mTabStrip.setViewPager(mViewPager);
		mViewPager.setCurrentItem(0);
		
		
		mTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				current_fragment_index=arg0;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
