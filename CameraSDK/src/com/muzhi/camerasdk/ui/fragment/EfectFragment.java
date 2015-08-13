package com.muzhi.camerasdk.ui.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.muzhi.camerasdk.R;
import com.muzhi.camerasdk.model.Filter_LocalPhotoInfo;
import com.muzhi.camerasdk.model.LocalStatic;
import com.muzhi.mtools.camerasdk.view.HSuperImageView;
import com.muzhi.mtools.camerasdk.view.HSuperImageView.OnStickerListener;
import com.muzhi.mtools.filter.GPUImageFilter;
import com.muzhi.mtools.filter.GPUImageView;
import com.muzhi.mtools.utils.ImageUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


public class EfectFragment extends Fragment {
	
	private final String folderName="CameraSDK";
	private Context mContext;
	private View mView;		
    private String mPath;
	private Bitmap sourceBitmap;
    private GPUImageView effect_main; // 需要修改的图像
    
    public static ArrayList<HSuperImageView> sticklist; // 保存贴纸图片的集合
	private int sticknum = -1;// 贴纸添加的序号
	
	public OnSlidingListener mListener;
    public interface OnSlidingListener {
        public void OnSlidingChanged(Boolean sliding);
    }       

    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	try {
    		mListener = (OnSlidingListener) activity;
    	} 
    	catch (ClassCastException e) { 
    		throw new ClassCastException(activity.toString() + " must implement OnSlidingListener");
    	}
    }
    
    public static EfectFragment newInstance(String path) {
    	EfectFragment f = new EfectFragment();
		Bundle b = new Bundle();
		b.putString("path", path);
		f.setArguments(b);
		return f;
	}
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPath = getArguments().getString("path");
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {			
		// TODO Auto-generated method stub		
		mView = inflater.inflate(R.layout.camerasdk_item_viewpage, container,false);  
		effect_main=(GPUImageView)mView.findViewById(R.id.effect_main);
		sticklist = new ArrayList<HSuperImageView>();
		
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		
		if(!mPath.equals("")){
			sourceBitmap=ImageUtils.getBitmap(mPath);
		}
		else{
			sourceBitmap=LocalStatic.bitmap;
		}
		float width = sourceBitmap.getWidth();
		float height = sourceBitmap.getHeight();
		float ratio = width / height;
		effect_main.setRatio(ratio);
		effect_main.setImage(sourceBitmap);
		
		initEvent();
	}
		
	
	
	private void initEvent(){
		
		effect_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//点击屏幕背景   将所有贴纸的外框全部去掉贴
				for (int i = 0; i < sticklist.size(); i++) {
					sticklist.get(i).setStickEditMode(false);
					sticklist.get(i).invalidate();
				}
			}
		});
	}
	
	
	//加特效
	public void addEffect(GPUImageFilter filter){
		effect_main.setFilter(filter);
		effect_main.requestRender();
	}
	
	//加贴纸
	public void addSticker(int drawableId,String path){
		sticknum++;
		HSuperImageView imageView = new HSuperImageView(getActivity(), sticknum);
		
		setStickerImg(drawableId,path, imageView);
		sticklist.add(imageView);
		effect_main.addView(imageView,new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
	}
	
	public void setStickerImg(final int drawableId,final String path, final HSuperImageView imageView) {
		File file = new File(path);
		if (file.exists()) {
			showStickerImg(drawableId,imageView);
			
		} else {
			new Thread() {
				public void run() {
					Message msg = new Message();
					msg.what = DOWNLOADSTICKER_SUCC;
					msg.arg1 = drawableId;
					msg.obj = imageView;
					handler.sendMessage(msg);
				}
			}.start();
		}
	}
	private void showStickerImg(int drawableId, HSuperImageView imageView) {
		//File file = new File(stickerList.get(position).pasterLocalpath);
		//Bitmap bmp = Util.decodeFile(file, Util.getscreenwidth(getApplicationContext()));
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawableId);
		if (bmp != null){
			imageView.init(bmp);// 设置控件图片
			eventStickerImage(imageView);
		}
		else
			Toast.makeText(getActivity(), "加载贴纸失败", Toast.LENGTH_SHORT).show();
	}
	
	//监听贴纸的事件
	private void eventStickerImage( HSuperImageView imageView){
		
		imageView.setOnTouchListener(new OnTouchListener() {
					
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()==MotionEvent.ACTION_UP){
					//viewpager可以滑动
					mListener.OnSlidingChanged(true);
				}
				else{
					//viewpager禁止滑动
					mListener.OnSlidingChanged(false);
				}
				
				return false;
			}
		});
		imageView.setOnStickerListener(new OnStickerListener() {

			@Override
			public void onStickerModeChanged(int position, int flag) {
				// TODO Auto-generated method stub
				if(flag==1){
					//删除
					try{
						effect_main.removeView(sticklist.get(position));
						sticklist.remove(position);
					}
					catch(Exception e){}
				}
				else if(flag==2){
					//点击
					boolean mode=sticklist.get(position).getStickEditMode();
					sticklist.get(position).setStickEditMode(!mode);
					sticklist.get(position).invalidate();
				}
				
			}
			
		});
	}
	
	//获取最终的图片
	public String getFilterImage(){
		
		effect_main.setDrawingCacheEnabled(true);
		Bitmap editbmp = Bitmap.createBitmap(effect_main.getDrawingCache());
		
		try{
			Bitmap fBitmap=effect_main.capture();
			Bitmap bitmap=Bitmap.createBitmap(fBitmap.getWidth(), fBitmap.getHeight(),Config.ARGB_8888);
			Canvas cv = new Canvas(bitmap);
			cv.drawBitmap(fBitmap, 0, 0, null);
			cv.drawBitmap(editbmp, 0, 0, null);
			//saveImage(bitmap);
			
			//最终合并生成图片
			String fileName = System.currentTimeMillis() + ".jpg";
			File parentpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			File file = new File(parentpath, folderName+"/" + fileName);
			file.getParentFile().mkdirs();
			try {
				bitmap.compress(CompressFormat.JPEG, 80, new FileOutputStream(file));
				MediaScannerConnection.scanFile(getActivity(), new String[] { file.toString() }, null, null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (file.exists()) {
				Filter_LocalPhotoInfo mLocalPhotoInfo = new Filter_LocalPhotoInfo(file.getAbsolutePath(), "file://"+ file.getAbsolutePath());
				//ResourceManager.bitmapsList.add(mLocalPhotoInfo);
			} else {
				Toast.makeText(getActivity(), "获取图片失败", Toast.LENGTH_SHORT).show();
			}
			bitmap.recycle();
			return file.getAbsolutePath();
			/*PhotoPickActivity.instance.getForResultComplate(file.getAbsolutePath());
	        finish();*/
			
			
		}
		catch(Exception e){
			return "";
		}
	}
	
	
	protected final int GETSTICKER_SUCC = 0;
	protected final int DOWNLOADSTICKER_SUCC = 1;
	
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {			
			case DOWNLOADSTICKER_SUCC:
				showStickerImg(msg.arg1, (HSuperImageView) msg.obj);
				break;
			}
		}
	};

	

	
	
}
