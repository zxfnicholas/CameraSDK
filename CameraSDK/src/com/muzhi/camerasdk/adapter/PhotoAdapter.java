package com.muzhi.camerasdk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.muzhi.camerasdk.R;
import com.muzhi.camerasdk.utils.CommonDefine;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public class PhotoAdapter extends CameraCommonAdapter<String>  implements OnClickListener {

	private Context mContext;
	//private ArrayList<String> dataList;
	private ArrayList<String> selectedDataList;
	private ImageLoader loader;
	private DisplayImageOptions options;
	
	public PhotoAdapter(Context context) {
		super(context);
		this.mContext = context;
	}
	
	public PhotoAdapter (Context context, ArrayList<String> dataList, ArrayList<String> selectedDataList, ImageLoader loader, DisplayImageOptions options) {
		super(context);
		this.mContext = context;
		this.mList = dataList;
		this.selectedDataList = selectedDataList;
		this.loader = loader;
		this.options = options;
	}

	
	private class ViewHolder {
		public ImageView imageView;
		public CheckBox checkBox;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.camerasdk_select_imageview, parent, false);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
			viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.select_pic_check);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		String path=mList.get(position);
		if (path.equals(CommonDefine.IMAGES_ADD)) {
			viewHolder.imageView.setImageResource(R.drawable.addphoto_button_pressed);
		} else {
			loader.displayImage("file://" + path, viewHolder.imageView, options);
		}
		viewHolder.checkBox.setTag(position);
		viewHolder.checkBox.setOnClickListener(this);
		viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					addAnimation(viewHolder.checkBox);
				}
			}
		});
		if (isInSelectedDataList(path)) {
			viewHolder.checkBox.setChecked(true);
		} else {
			viewHolder.checkBox.setChecked(false);
		}

		return convertView;
	}

	private boolean isInSelectedDataList(String selectedString) {
		if(selectedDataList != null){
			for (int i = 0; i < selectedDataList.size(); i++) {
				if (selectedDataList.get(i).equals(selectedString)) {
					return true;
				}
			}
		}
		return false;
	}
  
	@Override
	public void onClick(View view) {
		if (view instanceof CheckBox) {
			CheckBox toggleButton = (CheckBox) view;
			int position = (Integer) toggleButton.getTag();
			if (mList != null && mOnItemClickListener != null && position < mList.size()) {
				mOnItemClickListener.onItemClick(toggleButton, position, mList.get(position), toggleButton.isChecked());
			}
		}
	}

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}

	public interface OnItemClickListener {
		public void onItemClick(CheckBox toggleButton, int position, String path, boolean isChecked);
	}
    
	 /** 
     * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画  
     * @param view 
     */  
    private void addAnimation(View view){  
        float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};  
        AnimatorSet set = new AnimatorSet();  
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),   
                ObjectAnimator.ofFloat(view, "scaleY", vaules));  
                set.setDuration(150);  
        set.start();  
    } 
}
