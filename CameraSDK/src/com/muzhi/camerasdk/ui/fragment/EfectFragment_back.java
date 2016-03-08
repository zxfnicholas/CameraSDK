package com.muzhi.camerasdk.ui.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.muzhi.camerasdk.R;
import com.muzhi.camerasdk.library.filter.GPUImageFilter;
import com.muzhi.camerasdk.library.filter.GPUImageView;
import com.muzhi.camerasdk.library.sticker.ImageObject;
import com.muzhi.camerasdk.library.sticker.OperateUtils;
import com.muzhi.camerasdk.library.sticker.OperateView;
import com.muzhi.camerasdk.library.utils.PhotoUtils;
import com.muzhi.camerasdk.library.views.HSuperImageView;
import com.muzhi.camerasdk.model.Constants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


public class EfectFragment_back extends Fragment {
	
	private Context mContext;
	private View mView;		
    private String mPath;
	private boolean mFlag=false; //true是正方形截图过来的
    private GPUImageView effect_main; // 需要修改的图像
    //public Bitmap sourceBitmap;	//当前的图片
    //public static ArrayList<HSuperImageView> sticklist; // 保存贴纸图片的集合
	//private int sticknum = -1;// 贴纸添加的序号
	
    private OperateView operateView;
	private OperateUtils operateUtils;
    private Timer timer = new Timer();
    
	public static EfectFragment_back newInstance(String path,boolean flag) {
    	EfectFragment_back f = new EfectFragment_back();
		Bundle b = new Bundle();
		b.putString("path", path);
		b.putBoolean("flag", flag);
		f.setArguments(b);
		return f;
	}
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPath = getArguments().getString("path");
		mFlag=getArguments().getBoolean("flag");
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {			
		// TODO Auto-generated method stub		
		mView = inflater.inflate(R.layout.camerasdk_item_viewpage, container,false);  
		effect_main=(GPUImageView)mView.findViewById(R.id.effect_main);
		//sticklist = new ArrayList<HSuperImageView>();
		
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		operateUtils = new OperateUtils(getActivity());
		
		if(mFlag){
			setSourceBitmap(Constants.bitmap);
		}
		else{
			effect_main.setImage(mPath);
		}
		
		initEvent();
		
		// 延迟每次延迟10 毫秒 隔1秒执行一次
		timer.schedule(task, 10, 1000);
	}
		
	TimerTask task = new TimerTask(){
		public void run(){
			Message message = new Message();
			message.what = 1;
			myHandler.sendMessage(message);
		}
	};
	final Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if (msg.what == 1){
				if (effect_main.getWidth() != 0){
					Log.i("LinearLayoutW", effect_main.getWidth() + "");
					Log.i("LinearLayoutH", effect_main.getHeight() + "");
					// 取消定时器
					timer.cancel();
					fillContent();
				}
			}
		}
	};
	
	private void fillContent(){
		Bitmap resizeBmp = BitmapFactory.decodeFile(mPath);
		//Bitmap resizeBmp = effect_main.getCurrentBitMap();
		operateView = new OperateView(mContext, resizeBmp);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(resizeBmp.getWidth(), resizeBmp.getHeight());
		
		operateView.setLayoutParams(layoutParams);
		effect_main.addView(operateView);
		//content_layout.addView(operateView);
		operateView.setMultiAdd(true); // 设置此参数，可以添加多个图片
	}
	
	
	private void initEvent(){
		
		/*effect_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//点击屏幕背景   将所有贴纸的外框全部去掉贴
				for (int i = 0; i < sticklist.size(); i++) {
					sticklist.get(i).setStickEditMode(false);
					sticklist.get(i).invalidate();
				}
			}
		});*/
	}
	
	/**
	 * 改变图片(截图)
	 */
	public void setBitMap(){		
		setSourceBitmap(Constants.bitmap);
	}
	/**
	 * 获取当前图片
	 * @return
	 */
	public Bitmap getCurrentBitMap(){
		return effect_main.getCurrentBitMap();
	}
	
	//加特效
	public void addEffect(GPUImageFilter filter){
		effect_main.setFilter(filter);
		effect_main.requestRender();
	}
	
	//加贴纸
	public void addSticker(int drawableId,String path){
		/*sticknum++;
		HSuperImageView imageView = new HSuperImageView(getActivity(), sticknum);
		
		setStickerImg(drawableId,path, imageView);
		sticklist.add(imageView);
		effect_main.addView(imageView,new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));*/
		
		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawableId);
		// ImageObject imgObject = operateUtils.getImageObject(bmp);
		ImageObject imgObject = operateUtils.getImageObject(bmp, operateView,5, 150, 100);
		operateView.addItem(imgObject);
		
		
	}
	
	public void setStickerImg(final int drawableId,final String path, final HSuperImageView imageView) {
		File file = new File(path);
		if (file.exists()) {
			showStickerImg(drawableId,imageView);
			
		} else {
			new Thread() {
				public void run() {
					//Utils.downLoadFile(stickerList.get(position), stickerList.get(position).pasterUrl);
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
		else{
			Toast.makeText(getActivity(), "加载贴纸失败", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	//监听贴纸的事件
	private void eventStickerImage( HSuperImageView imageView){
		
		/*imageView.setOnStickerListener(new OnStickerListener() {
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
		});*/
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
			String path=PhotoUtils.saveAsBitmap(mContext, bitmap);
			bitmap.recycle();
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
	
	
	private void setSourceBitmap(Bitmap sourceBitmap){
		float width = sourceBitmap.getWidth();
		float height = sourceBitmap.getHeight();
		float ratio = width / height;
	
		effect_main.setRatio(ratio);
		effect_main.setImage(sourceBitmap);
	}
	
}
