package com.muzhi.camerasdk.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.muzhi.camerasdk.R;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 特效页顶部小图片
 * @author jazzy
 *
 */
public class SmallThumbAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<viewinfo> mData;
	private Context mContext;
	private int cur_position=0;
	
	public SmallThumbAdapter(Context context, List<String> sData) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mData=new ArrayList<viewinfo>();
		for(String path:sData){
			viewinfo info=new viewinfo();
			info.path=path;
			this.mData.add(info);
		}
		
		//this.mData = mData;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	public void setSelected(int position){
		viewinfo item=mData.get(position);
		boolean flag=false;
		for(viewinfo info: mData){
			flag=info.path.equals(item.path);
			info.selected=flag;
		}
		notifyDataSetChanged();
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
			ViewHolder holder = new ViewHolder();
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.camerasdk_list_item_image_view2, null);
				holder.img = (ImageView) convertView.findViewById(R.id.iv_image);
				holder.img_mask = (ImageView) convertView.findViewById(R.id.iv_mask);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder) convertView.getTag();
			}

			viewinfo info=mData.get(position);
			
			String path= info.path;
			int mItemSize=90;
			File imageFile = new File(path);
			Picasso.with(mContext)
			.load(imageFile)
			.error(R.drawable.camerasdk_pic_loading)
			.resize(mItemSize, mItemSize)
            .centerCrop()
			.into(holder.img);
			
			if(info.selected){
				holder.img_mask.setVisibility(View.VISIBLE);
			}
			else{
				holder.img_mask.setVisibility(View.GONE);
			}
			
			return convertView;
	}

	public final class ViewHolder {
		public ImageView img; // 图像
		public ImageView img_mask; // 图像
	}
	
	
	public class viewinfo{
		public String path;
		public boolean selected=false;
	}
	
	
}



