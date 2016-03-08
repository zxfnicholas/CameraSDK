package com.muzhi.camerasdk.utils;

import java.util.ArrayList;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.PagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	
	String[] titles;// = new String[]{"hot", "trending" ,"fresh"};
    private ArrayList<Fragment> fragmentsList;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentsList=new ArrayList<Fragment>();
    }

    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, String[] titles) {
        super(fm);
        this.fragmentsList = fragments;
        this.titles=titles;
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragmentsList.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    	//return PagerAdapter.POSITION_NONE;
    }

    @Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return titles[position];
	}
    
    public void add(Fragment fragment) {
    	fragmentsList.add(fragment);
	}

	public void addAll(ArrayList<Fragment> fragments) {
		this.fragmentsList.addAll(getCount(), fragments);
		
	}
}
