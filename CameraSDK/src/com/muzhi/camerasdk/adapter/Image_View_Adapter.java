package com.muzhi.camerasdk.adapter;

import java.io.File;
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
 * 特效(暂时无用)
 * @author jazzy
 *
 */
public class Image_View_Adapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<String> mData;
	private Context mContext;

	public Image_View_Adapter(Context context, List<String> mData) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mData = mData;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
			ViewHolder holder = new ViewHolder();
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.camerasdk_list_item_image_view2, null);
				holder.img = (ImageView) convertView.findViewById(R.id.iv_image);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder) convertView.getTag();
			}

			String path= mData.get(position);
			
			File imageFile = new File(path);
			Picasso.with(mContext).load(imageFile).error(R.drawable.camerasdk_pic_loading).into(holder.img);
			
			return convertView;
	}

	public final class ViewHolder {
		public ImageView img; // 图像
	}
}



