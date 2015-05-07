package com.muzhi.camerasdk;

import java.util.ArrayList;
import java.util.List;

import com.muzhi.camerasdk.adapter.AlbumAdapter;
import com.muzhi.camerasdk.model.ImageBucket;
import com.muzhi.camerasdk.model.MediaStoreBucket;
import com.muzhi.camerasdk.model.MediaStoreCursorHelper;
import com.muzhi.camerasdk.utils.AlbumHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class AlbumActivity extends BaseActivity implements OnItemClickListener {
	private ListView mLVChancePhoto;
	private AlbumAdapter adapter;
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
		adapter = new AlbumAdapter(mActThis, mBuckets,loader,options);
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
			ArrayList<ImageBucket> list = MediaStoreCursorHelper.queryAllPhoto(this);
			ArrayList<String> listPath=new ArrayList<String>();
			for(ImageBucket ib : list){
				listPath.add(ib.bucketThumb);
			}
			intent.putExtra("listPath", listPath);
			intent.putExtra("selectedDataList", selectedDataList);
			intent.putExtra("name", getString(R.string.camerasdk_album_all));
			intent.putExtra("album", booleanExtra);
		} else {
			String id = item.getId();
			ArrayList<ImageBucket> list = MediaStoreCursorHelper.queryPhotoByBucketID(mActThis, id);
			ArrayList<String> listPath=new ArrayList<String>();
			for(ImageBucket ib : list){
				listPath.add(ib.bucketThumb);
			}
			intent.putExtra("listPath", listPath);
			intent.putExtra("selectedDataList", selectedDataList);
			intent.putExtra("name", item.getName());
			intent.putExtra("album", booleanExtra);
		}
		setResult(-1, intent);
		AlbumActivity.this.finish();
	}

	
}
