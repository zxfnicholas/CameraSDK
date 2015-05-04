package com.muzhi.camerasdk;

import java.util.ArrayList;
import java.util.List;

import com.muzhi.camerasdk.model.MediaStoreBucket;
import com.muzhi.camerasdk.model.MediaStoreCursorHelper;
import com.muzhi.camerasdk.utils.AlbumHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AlbumActivity extends BaseActivity implements OnItemClickListener {
	private ListView mLVChancePhoto;
	private ChanceAdapter adapter;
	private ArrayList<MediaStoreBucket> mBuckets = new ArrayList<MediaStoreBucket>();
	private ArrayList<String> selectedDataList;
	private boolean booleanExtra;
	
	private AlbumHelper helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.camerasdk_activity_album_chance);
		//TextView actionbar_title = (TextView) findViewById(R.id.camerasdk_actionbar_title);
		//actionbar_title.setText("选择相册");
		TextView button_cancel = (TextView) findViewById(R.id.cancel_button);
		//button_cancel.setText("返回");
		button_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		selectedDataList=new ArrayList<String>();
		Bundle extras = getIntent().getExtras();
		if(extras!=null){
			selectedDataList = extras.getStringArrayList("selectedDataList");
			booleanExtra = extras.getBoolean("album");
		}
		mLVChancePhoto = (ListView) findViewById(R.id.chance_photo_lv);
		adapter = new ChanceAdapter(mActThis, mBuckets);
		mLVChancePhoto.setAdapter(adapter);
		mLVChancePhoto.setOnItemClickListener(this);
		
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
	}
	
    @Override
    protected void onStart() {
    	super.onStart();
    	loadBuckets();
    }
    
	private void loadBuckets() {
		List<MediaStoreBucket> buckets = MediaStoreCursorHelper.getBucket(mActThis);
		if (null != buckets && !buckets.isEmpty()) {
			mBuckets.clear();
			mBuckets.addAll(buckets);
			adapter.notifyDataSetChanged();
		}
		
	}

	private class ChanceAdapter extends BaseAdapter {
		private Context mActThis;
		private ArrayList<MediaStoreBucket> mBuckets;

		public ChanceAdapter(Context mActThis, ArrayList<MediaStoreBucket> mBuckets) {
			this.mActThis = mActThis;
			this.mBuckets = mBuckets;
		}

		@Override
		public int getCount() {
			return mBuckets.size();
		}

		@Override
		public Object getItem(int position) {
			return mBuckets.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemAlbum itemAlbum = null;
			if (convertView == null) {
				itemAlbum = new ItemAlbum();
				convertView = LayoutInflater.from(mActThis).inflate(R.layout.camerasdk_item_album_chance, null);
				itemAlbum.itemIVAlbum = (ImageView) convertView.findViewById(R.id.item_album_iv);
				itemAlbum.itemTVAlbum = (TextView) convertView.findViewById(R.id.item_album_tv);
				convertView.setTag(itemAlbum);
			} else {
				itemAlbum = (ItemAlbum) convertView.getTag();
			}
			MediaStoreBucket mediaStoreBucket = mBuckets.get(position);
			String id = mediaStoreBucket.getId();
			if( id != null){
				ArrayList<String> listPath = MediaStoreCursorHelper.queryPhoto((Activity) mActThis, id);
				String firstImgPath = listPath.get(0);				
				loader.displayImage("file://" + firstImgPath, itemAlbum.itemIVAlbum, options);
			} else {
				ArrayList<String> list = MediaStoreCursorHelper.queryAllPhoto((Activity) mActThis);
				if(list!=null && list.size()>0){
					String string = list.get(0);
					loader.displayImage("file://" + string, itemAlbum.itemIVAlbum, options);
				}
			}
			String name = mediaStoreBucket.getName();
			String ablum_name=getString(R.string.camerasdk_album_all);
			if (name.equals(ablum_name)) {
				itemAlbum.itemTVAlbum.setText(ablum_name);
			} else {
				itemAlbum.itemTVAlbum.setText(name);
			}
			return convertView;
		}
	}

	class ItemAlbum {
		ImageView itemIVAlbum;
		TextView itemTVAlbum;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MediaStoreBucket item = (MediaStoreBucket) parent.getItemAtPosition(position);
		if (null != item) {
			loadBucketId(item);
		}
	}

	private void loadBucketId(MediaStoreBucket item) {
		Intent intent = new Intent(this, PhotoPickActivity.class);
		if (item.getName().equals(getString(R.string.camerasdk_album_all))) {
			ArrayList<String> list = MediaStoreCursorHelper.queryAllPhoto(this);
			intent.putExtra("listPath", list);
			intent.putExtra("selectedDataList", selectedDataList);
			intent.putExtra("name", getString(R.string.camerasdk_album_all));
			intent.putExtra("album", booleanExtra);
		} else {
			String id = item.getId();
			ArrayList<String> listPath = MediaStoreCursorHelper.queryPhoto(this, id);
			intent.putExtra("listPath", listPath);
			intent.putExtra("selectedDataList", selectedDataList);
			intent.putExtra("name", item.getName());
			intent.putExtra("album", booleanExtra);
		}
		setResult(-1, intent);
		AlbumActivity.this.finish();
	}

	
}
