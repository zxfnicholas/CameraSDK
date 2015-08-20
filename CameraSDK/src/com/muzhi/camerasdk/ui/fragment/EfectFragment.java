package com.muzhi.camerasdk.ui.fragment;

import java.io.File;
import java.util.ArrayList;

import com.muzhi.camerasdk.R;
import com.muzhi.camerasdk.model.Constants;
import com.muzhi.camerasdk.model.EditFilterImageInfo;
import com.muzhi.mtools.camerasdk.view.HSuperImageView;
import com.muzhi.mtools.camerasdk.view.HSuperImageView.OnStickerListener;
import com.muzhi.mtools.filter.GPUImageFilter;
import com.muzhi.mtools.filter.GPUImageView;
import com.muzhi.mtools.utils.ImageUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


public class EfectFragment extends Fragment {
	

	private Context mContext;
	private View mView;		
	private Bitmap sourceBitmap;
	private int position=0; //当前
	private GPUImageFilter mFilter;	
    private GPUImageView effect_main; // 需要修改的图像
    
    public static ArrayList<HSuperImageView> sticklist; // 保存贴纸图片的集合
	private int sticknum = -1;// 贴纸添加的序号
	
	private ArrayList<String> pathList;
	private ArrayList<EditFilterImageInfo> imageList;
	
	private boolean mSigleCropper=false;
	
    public static EfectFragment newInstance(ArrayList<String> paths,boolean flag) {
    	EfectFragment f = new EfectFragment();
		Bundle b = new Bundle();
		b.putStringArrayList("path", paths);
		b.putBoolean("flag", flag);
		f.setArguments(b);
		return f;
	}
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSigleCropper=getArguments().getBoolean("flag");
		pathList=getArguments().getStringArrayList("path");
		if(pathList==null){
			pathList=new ArrayList<String>();
		}
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
		
		/*if(!mSigleCropper){
			
			imageList=new ArrayList<EditFilterImageInfo>();
			int size=pathList.size();
			if(size>0){
				for(int i=0;i<size;i++){
					EditFilterImageInfo info=new EditFilterImageInfo();
					String path=pathList.get(i);
					Bitmap bitmap=ImageUtils.getBitmap(path);		
					info.setPath(path);
					info.setBitmap(bitmap);
					imageList.add(info);
				}						
			}
			
			if(imageList.size()>0){	
				showImageBitmap(imageList.get(0));
			}
			
		}
		else{
			//单图裁切
			sourceBitmap=Constants.bitmap;
			float width = sourceBitmap.getWidth();
			float height = sourceBitmap.getHeight();
			float ratio = width / height;
			effect_main.setRatio(ratio);
			effect_main.setImage(sourceBitmap);
		}*/
		
		
		imageList=new ArrayList<EditFilterImageInfo>();
		int size=pathList.size();
		if(size>0){
			for(int i=0;i<size;i++){
				EditFilterImageInfo info=new EditFilterImageInfo();
				String path=pathList.get(i);
				Bitmap bitmap=ImageUtils.getBitmap(path);		
				info.setPath(path);
				info.setBitmap(bitmap);
				imageList.add(info);
			}						
		}
		
		if(imageList.size()>0){	
			showImageBitmap(imageList.get(0));
		}
				
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
	
	//更换图片
	public void changeImage(String path){
		
		imageList.get(position).setFilter(mFilter);
		for(int i=0;i<imageList.size();i++){
			if(imageList.get(i).getPath().equals(path)){
				showImageBitmap(imageList.get(i));
				position=i;
				break;
			}
		}
	}
	
	public void showImageBitmap(EditFilterImageInfo info){
		sourceBitmap=info.getBitmap();
		float width = sourceBitmap.getWidth();
		float height = sourceBitmap.getHeight();
		float ratio = width / height;
		effect_main.setRatio(ratio);
		effect_main.setImage(sourceBitmap);
		addEffect(info.getFilter());
		
		//隐藏及显示贴纸
		ArrayList<Integer> ids=info.getStickIds();
		for(HSuperImageView hsiv : sticklist){
			boolean flag=false;
			for(Integer id : ids){
				if(hsiv.getId()==id){
					flag=true;
				}
			}
			if(flag){
				hsiv.setVisibility(View.VISIBLE);
			}
			else{
				hsiv.setVisibility(View.GONE);
			}
		}
		
	}
	
	//加特效
	public void addEffect(GPUImageFilter filter){		
		effect_main.setFilter(filter);		
		mFilter=filter;		
	}
	
	//加贴纸
	public void addSticker(int drawableId,String path){
		sticknum++;
		HSuperImageView imageView = new HSuperImageView(mContext, sticknum);
		imageView.setId(sticknum);
		
		for(HSuperImageView hsiv:sticklist){
			hsiv.setStickEditMode(false);
		}
		
		setStickerImg(drawableId,path, imageView);
		sticklist.add(imageView);
		imageList.get(position).addStickId(sticknum);
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
			Toast.makeText(mContext, "加载贴纸失败", Toast.LENGTH_SHORT).show();
	}
	
	//监听贴纸的事件
	private void eventStickerImage(final HSuperImageView imageView){
		
		imageView.setOnStickerListener(new OnStickerListener() {

			@Override
			public void onStickerModeChanged(int index, int flag) {
				// TODO Auto-generated method stub
				if(flag==1){
					//删除
					try{						
						effect_main.removeView(imageView);
						sticklist.remove(imageView);
						imageList.get(position).delStickId(imageView.getId());						
					}
					catch(Exception e){}
				}
				
			}
			
		});
	}
	
	public ArrayList<String> getAllPhoto(){
		ArrayList<String> list=new ArrayList<String>();
		for(EditFilterImageInfo info : imageList){
			changeImage(info.getPath());
			String path=getFilterImage();
			list.add(path);
		}
		return list;
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
			
			//最终合并生成图片
			String path=ImageUtils.saveAsBitmap(mContext, bitmap, Constants.folderName, null);
			return path;
			
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
