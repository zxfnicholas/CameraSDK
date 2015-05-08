package com.muzhi.camerasdk.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import com.muzhi.camerasdk.R;
import com.muzhi.camerasdk.model.ImageBucket;
import com.muzhi.camerasdk.model.MediaStoreBucket;
import com.muzhi.camerasdk.model.MediaStoreCursorHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public class AlbumAdapter extends CameraCommonAdapter<MediaStoreBucket>{

	
	private ImageLoader loader;
	private DisplayImageOptions options;
	
	public AlbumAdapter(Context context) {
		super(context);
		this.mContext = context;
	}
	
	public AlbumAdapter(Context context, ArrayList<MediaStoreBucket> mBuckets, ImageLoader loader, DisplayImageOptions options) {
		super(context);
		this.mContext = context;
		this.mList = mBuckets;
		this.loader = loader;
		this.options = options;
	}

	
	private class ViewHolder {
		ImageView itemIVAlbum;
		TextView itemTVAlbum;
		TextView itemTVAlbumCount;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.camerasdk_item_album, null);
			viewHolder.itemIVAlbum = (ImageView) convertView.findViewById(R.id.item_album_iv);
			viewHolder.itemTVAlbum = (TextView) convertView.findViewById(R.id.item_album_tv);
			viewHolder.itemTVAlbumCount = (TextView) convertView.findViewById(R.id.item_album_count_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		MediaStoreBucket mediaStoreBucket = mList.get(position);
		String id = mediaStoreBucket.getId();
		if( id != null){
			ArrayList<ImageBucket> listPath = MediaStoreCursorHelper.queryPhotoByBucketID(mContext, id);
			String firstImgPath = listPath.get(0).bucketThumb;	
			viewHolder.itemTVAlbumCount.setText("("+listPath.get(0).count+")");
			loader.displayImage("file://" + firstImgPath, viewHolder.itemIVAlbum, options);
		} else {
			ArrayList<ImageBucket> list = MediaStoreCursorHelper.queryAllPhoto((Activity)mContext);
			if(list!=null && list.size()>0){
				String string = list.get(0).bucketThumb;
				viewHolder.itemTVAlbumCount.setText("("+list.get(0).count+")");
				loader.displayImage("file://" + string, viewHolder.itemIVAlbum, options);
			}
		}
		String name = mediaStoreBucket.getName();
		
		
		String ablum_name=mContext.getResources().getString(R.string.camerasdk_album_all);
		if (name.equals(ablum_name)) {
			viewHolder.itemTVAlbum.setText(ablum_name);
		} else {
			viewHolder.itemTVAlbum.setText(name);
		}
		return convertView;
	}

}
