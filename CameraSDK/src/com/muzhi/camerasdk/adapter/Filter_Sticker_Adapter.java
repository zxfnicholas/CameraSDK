package com.muzhi.camerasdk.adapter;

import java.io.File;
import java.util.ArrayList;

import com.muzhi.camerasdk.R;
import com.muzhi.camerasdk.library.filter.util.ImageFilterUtil;
import com.muzhi.camerasdk.model.Filter_Sticker_Info;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * 贴纸
 * @author Administrator
 *
 */
public class Filter_Sticker_Adapter extends BaseAdapter {

	private LayoutInflater mInflater;
	//贴纸的个数
	private ArrayList<Filter_Sticker_Info> mData;
	private Context mContext;

	public Filter_Sticker_Adapter(Context context, ArrayList<Filter_Sticker_Info> mData) {
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
				convertView = mInflater.inflate(R.layout.camerasdk_item_sticker, null);
				holder.img = (ImageView) convertView.findViewById(R.id.sticker_img);
				holder.stickerlib_img = (ImageView) convertView.findViewById(R.id.stickerlib_img);
				holder.sticker_layout = (RelativeLayout)convertView.findViewById(R.id.sticker_layout);
				holder.pro_bar = (ProgressBar)convertView.findViewById(R.id.pro_bar);
				
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder) convertView.getTag();
				//ImageLoader.getInstance().cancelDisplayTask(holder.img);
			}
			
			Filter_Sticker_Info mSticker = mData.get(position);

			holder.img.setVisibility(View.VISIBLE);
			holder.stickerlib_img.setVisibility(View.GONE);
			holder.pro_bar.setVisibility(View.GONE);
			
			if(mSticker.isLib()){
				holder.sticker_layout.setBackgroundColor(Color.parseColor("#ffd83a"));
			}
			else{
				holder.sticker_layout.setBackgroundColor(Color.parseColor("#3b3f49"));
			}
			
			
			/*File file = new File(mSticker.pasterLocalpath);
			if(file.exists())
				holder.img.setImageBitmap(Util.decodeFile(file, Util.getscreenwidth(mContext)));
			else
				downLoadFile(mSticker,holder);*/
			holder.img.setImageResource(mSticker.getDrawableId());
			
			return convertView;
	}
	
	private void downLoadFile(final Filter_Sticker_Info mSticker,final ViewHolder holder){
		holder.pro_bar.setVisibility(View.VISIBLE);
		new Thread(){
			public void run(){
				//boolean flag = Utils.downLoadFile(mSticker,mSticker.pasterUrl);
				if(true){
					Message msg = new Message();
					msg.what = GETSTICKER_SUCC;
					msg.obj = new HandlerBody(mSticker,holder);
					handler.sendMessage(msg);
				}
			}
		}.start();
	}
	
	protected final int GETSTICKER_SUCC = 0;
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {				
			case GETSTICKER_SUCC:
				HandlerBody mBody = (HandlerBody) msg.obj;
				File file = new File(mBody.mSticker.getLocal_path());
				mBody.holder.pro_bar.setVisibility(View.GONE);
				mBody.holder.img.setImageBitmap(ImageFilterUtil.decodeFile(file, ImageFilterUtil.getscreenwidth(mContext)));
				break;
			}
		}
	};
	
	class HandlerBody{
		Filter_Sticker_Info mSticker;
		ViewHolder holder;
		
		public HandlerBody(Filter_Sticker_Info mSticker,ViewHolder holder){
			this.mSticker = mSticker;
			this.holder = holder;
		}
	}

   class ViewHolder {
		public ProgressBar pro_bar;
		public RelativeLayout sticker_layout;
		public ImageView stickerlib_img;
		public ImageView img; // 图像
		public TextView title;// 标题
	}
}



