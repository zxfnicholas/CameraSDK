package com.muzhi.camerasdk.example.adapter;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/*********************
 * 抽象适配器，作为抽象基类使用，封装BaseAdapter基本操作
 * 
 * @author 
 * @param <T>
 */
public abstract class CommonListAdapter<T> extends BaseAdapter {

	protected List<T> mList;
	protected Context mContext;
	protected OnItemCallBackClickListener onItemClick = null;
	
	public CommonListAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView,
			ViewGroup parent);

	

	public List<T> getList() {
		return mList;
	}
	

	public void setList(List<T> list) {
		this.mList = list;
		notifyDataSetChanged();
	}
	public void setList(T[] list) {
		ArrayList<T> arrayList = new ArrayList<T>(list.length);
		for (T t : list) {
			arrayList.add(t);
		}
		setList(arrayList);
	}
	
	public void addToFirst(List<T> list) {
		
		ArrayList<T> arrayList = new ArrayList<T>(list.size());
		for (T t : list) {
			arrayList.add(t);
		}
		setList(arrayList);
	}

	public void addToLast(List<T> list) {		
		 if (list != null && list.size() > 0) {	            
			 this.mList.addAll(list);
	     }
		 this.notifyDataSetChanged();
	}
	
	
	
	/**
	 * 回调
	 */
	public void OnItemClickListener(OnItemCallBackClickListener mClickListener) {
		this.onItemClick = mClickListener;
	}
	
	/**
	 * 按钮事件接口
	 */
	public interface OnItemCallBackClickListener {
		public void onItemClick(int position);
	}
	
	
}
