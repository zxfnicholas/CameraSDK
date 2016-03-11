package com.muzhi.camerasdk.adapter;

import java.util.List;

import com.muzhi.camerasdk.R;
import com.muzhi.camerasdk.model.Filter_Effect_Info;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 特效
 * @author Administrator
 *
 */
public class EditFilterImageAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Filter_Effect_Info> mData;
	private Context mContext;
	private int selectItem = 0;

	public EditFilterImageAdapter(Context context, List<Filter_Effect_Info> mData) {
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
				convertView = mInflater.inflate(R.layout.camerasdk_item_effect, null);
				holder.img = (ImageView) convertView.findViewById(R.id.effect_img);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.item_back = (LinearLayout)convertView.findViewById(R.id.item_back);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder) convertView.getTag();
			}

			Filter_Effect_Info mEffect = mData.get(position);
			
			holder.img.setImageResource(mEffect.getIconId());
			holder.title.setText(mEffect.getName());

			if (position == selectItem) {
				holder.item_back.setBackgroundColor(Color.YELLOW);
			} 
			else {
				holder.item_back.setBackgroundColor(0xfff1f1f1);
			}

			return convertView;
	}

	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
		this.notifyDataSetChanged();
	}

	public final class ViewHolder {
		public LinearLayout item_back;
		public ImageView img; // 图像
		public TextView title;// 标题
	}
}



