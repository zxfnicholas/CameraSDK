package com.muzhi.camerasdk.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.muzhi.camerasdk.R;
import com.muzhi.camerasdk.library.utils.ViewHolder;
import com.muzhi.camerasdk.model.Filter_Sticker_Info;



/**
 * 贴纸库Adapter
 */
public class StickerAdapter extends CommonListAdapter<Filter_Sticker_Info>{

	public StickerAdapter(Context context) {
		super(context);
		this.mContext=mContext;
		this.mLayoutId=R.layout.camerasdk_list_item_sticker;
	}

	@Override
	public void getCommonView(ViewHolder helper, Filter_Sticker_Info item) {
		// TODO Auto-generated method stub
		
		ImageView image = (ImageView)helper.getView(R.id.iv_sticker);
		
		Glide.with(mContext)
        .load(item.getImage())
        .placeholder(R.drawable.camerasdk_pic_loading)
        .error(R.drawable.camerasdk_pic_loading)
        .override(120, 120)
        .centerCrop()
        .into(image);
		
	}
}


