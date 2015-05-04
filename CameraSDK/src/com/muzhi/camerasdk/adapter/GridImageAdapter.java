package com.muzhi.camerasdk.adapter;

import java.util.ArrayList;

import com.muzhi.camerasdk.R;
import com.muzhi.camerasdk.model.CameraImageInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class GridImageAdapter extends CameraCommonAdapter<CameraImageInfo> {

	private ImageLoader loader;
	private DisplayImageOptions options;

	public GridImageAdapter(Context context) {
		super(context);
		this.mContext = context;
	}

	public GridImageAdapter(Context context, ArrayList<CameraImageInfo> list,ImageLoader loader, DisplayImageOptions options) {
		super(context);
		this.mContext = context;
		this.mList = list;
		this.loader = loader;
		this.options = options;
	}

	public ArrayList<String> getSourceList(){
		ArrayList<String> ret=new ArrayList<String>();
		for(CameraImageInfo info : mList){
			if(!info.isAddButton){
				ret.add(info.getSource_image());
			}
		}
		return ret;
	}
	public ArrayList<String> getUploadList(){
		ArrayList<String> ret=new ArrayList<String>();
		for(CameraImageInfo info : mList){
			if(!info.isAddButton){
				ret.add(info.getUpload_image());
			}
		}
		return ret;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		/*if (convertView == null) {
			convertView = View.inflate(mContext,R.layout.camerasdk_item_grid_img, null);
			holder = new ViewHolder();
			holder.imageview = (ImageView) convertView.findViewById(R.id.row_gridview_imageview);
			convertView.setTag(holder);
		} 
		else {
			holder = (ViewHolder) convertView.getTag();
		}*/
		
		convertView = View.inflate(mContext,R.layout.camerasdk_item_grid_img, null);
		holder = new ViewHolder();
		holder.imageview = (ImageView) convertView.findViewById(R.id.row_gridview_imageview);
		convertView.setTag(holder);
		
		CameraImageInfo info = mList.get(position);
		if(!info.isAddButton()){
			loader.displayImage("file://" + info.getUpload_image(), holder.imageview, options);
		}
		else{
			holder.imageview.setImageResource(R.drawable.addphoto_button_pressed);
		}

		return convertView;

	}

	public static class ViewHolder {
		public ImageView imageview;
	}
}
