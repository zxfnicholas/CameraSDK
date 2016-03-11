package com.muzhi.camerasdk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.muzhi.camerasdk.adapter.FolderAdapter;
import com.muzhi.camerasdk.adapter.ImageGridAdapter;
import com.muzhi.camerasdk.model.CameraSdkParameterInfo;
import com.muzhi.camerasdk.model.FolderInfo;
import com.muzhi.camerasdk.model.ImageInfo;
import com.muzhi.camerasdk.utils.*;
import com.squareup.picasso.Picasso;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class PhotoPickActivity extends BaseActivity {

	public static PhotoPickActivity instance = null;
	
    private CameraSdkParameterInfo mCameraSdkParameterInfo=new CameraSdkParameterInfo();
    
    private ArrayList<String> resultList = new ArrayList<String>();// 结果数据
    private ArrayList<FolderInfo> mResultFolder = new ArrayList<FolderInfo>();// 文件夹数据
    private HashMap<String, ImageView> hashMap = new HashMap<String, ImageView>();//预览图片集
    
    // 不同loader定义
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    
    private TextView  mCategoryText,mTimeLineText,button_complate;
    private GridView mGridView;
    private PopupWindow mpopupWindow;
    private RelativeLayout camera_footer;
    private ImageGridAdapter mImageAdapter;
    private FolderAdapter mFolderAdapter;
    private boolean hasFolderGened = false;
    private File mTmpFile;
    private HorizontalScrollView scrollview;
    private LinearLayout selectedImageLayout;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camerasdk_activity_main);
		
		instance=this;
		
		/*int resId=MResource.getIdByName(this,MResource.layout, "camerasdk_activity_main");
		if (resId > 0) {
			setContentView(resId);
		}*/
		
		initExtra();
		initViews();
		initEvent();
		getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
		
		
	}
	
	//获取传过来的参数
	private void initExtra(){
		
		 Intent intent = getIntent();
		 try{
			 mCameraSdkParameterInfo=(CameraSdkParameterInfo)intent.getSerializableExtra(CameraSdkParameterInfo.EXTRA_PARAMETER);
			 resultList=mCameraSdkParameterInfo.getImage_list();
		 }
		 catch(Exception e){  }
		
	}
	
	private void initViews(){
		showLeftIcon();
		mCategoryText = (TextView)findViewById(R.id.camerasdk_actionbar_title);
		Drawable drawable= getResources().getDrawable(R.drawable.message_popover_arrow);  
		drawable.setBounds(10, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
		mCategoryText.setCompoundDrawables(null,null,drawable,null);  
		
		mTimeLineText = (TextView)findViewById(R.id.timeline_area);
		button_complate = (TextView)findViewById(R.id.button_complate);
		mGridView=(GridView)findViewById(R.id.gv_list);
		camera_footer = (RelativeLayout)findViewById(R.id.camera_footer);
		selectedImageLayout = (LinearLayout) findViewById(R.id.selected_image_layout);
		scrollview = (HorizontalScrollView) findViewById(R.id.scrollview);
		
		button_complate.setText("完成(0/"+mCameraSdkParameterInfo.getMax_image()+")");
		
		mImageAdapter = new ImageGridAdapter(mContext, mCameraSdkParameterInfo.isShow_camera(),mCameraSdkParameterInfo.isSingle_mode());
		mGridView.setAdapter(mImageAdapter);
		mFolderAdapter = new FolderAdapter(mContext);
		
		if(mCameraSdkParameterInfo.isSingle_mode()){
			camera_footer.setVisibility(View.GONE);
		}
	}
	
	//设置预览图
	private void initSelectImage() {
		if (resultList == null)
			return;
		
		selectedImageLayout.removeAllViews();
		for(String path :resultList){
			addImagePreview(path);
		}
	}
	private void initEvent(){
		
		mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	showPopupFolder(view);
            }
        });
		button_complate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(resultList.size()>0){
					selectComplate();
				}
			}
		});
		 mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
	            @Override
	            public void onScrollStateChanged(AbsListView absListView, int state) {

	                final Picasso picasso = Picasso.with(mContext);
	                if(state == SCROLL_STATE_IDLE || state == SCROLL_STATE_TOUCH_SCROLL){
	                    picasso.resumeTag(mContext);
	                }else{
	                    picasso.pauseTag(mContext);
	                }

	                if(state == SCROLL_STATE_IDLE){
	                    // 停止滑动，日期指示器消失
	                    mTimeLineText.setVisibility(View.GONE);
	                }else if(state == SCROLL_STATE_FLING){
	                    mTimeLineText.setVisibility(View.VISIBLE);
	                }
	            }

	            @Override
	            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	                if(mTimeLineText.getVisibility() == View.VISIBLE) {
	                    int index = firstVisibleItem + 1 == view.getAdapter().getCount() ? view.getAdapter().getCount() - 1 : firstVisibleItem + 1;
	                    ImageInfo imageInfo = (ImageInfo) view.getAdapter().getItem(index);
	                    if (imageInfo != null) {
	                        mTimeLineText.setText(TimeUtils.formatPhotoDate(imageInfo.path));
	                    }
	                }
	            }
	        });
		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {

                final int width = mGridView.getWidth();
                final int height = mGridView.getHeight();
               // mGridWidth = width;
               // mGridHeight = height;
                final int desireSize = getResources().getDimensionPixelOffset(R.dimen.image_size);
                final int numCount = width / desireSize;
                final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
                int columnWidth = (width - columnSpace*(numCount-1)) / numCount;
                mImageAdapter.setItemSize(columnWidth);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }else{
                    mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
		
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                
            	if(mImageAdapter.isShowCamera()){
            		if(i == 0){
            			if(mCameraSdkParameterInfo.getMax_image() == resultList.size()){
                            Toast.makeText(mContext, R.string.camerasdk_msg_amount_limit, Toast.LENGTH_SHORT).show();
                        }
            			else{
            				showCameraAction();
            			}
                        return;
                    }
            	}
            	ImageInfo imageInfo = (ImageInfo) adapterView.getAdapter().getItem(i);
                selectImageFromGrid(imageInfo);
            }
        });
	}
	
	 /**
     * 选择相机
     */
    private void showCameraAction() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(mContext.getPackageManager()) != null){
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = FileUtils.createTmpFile(mContext);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            startActivityForResult(cameraIntent, CameraSdkParameterInfo.TAKE_PICTURE_FROM_CAMERA);
        }else{
            Toast.makeText(mContext, R.string.camerasdk_msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 相机拍照完成后，返回图片路径
        if(requestCode == CameraSdkParameterInfo.TAKE_PICTURE_FROM_CAMERA){
            if(resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                	
                	
                	//加入content provider
                    ContentValues values = new ContentValues(7);
                    values.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, "");
                    values.put(MediaStore.Images.Media.DATE_TAKEN, "");
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.Images.Media.ORIENTATION, 0);
                    values.put(MediaStore.Images.Media.DATA, mTmpFile.getPath());
                    values.put(MediaStore.Images.Media.SIZE, mTmpFile.length());
                    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    
                    if(mCameraSdkParameterInfo.isSingle_mode()){
                		resultList.clear();
                		resultList.add(mTmpFile.getPath());
                		selectComplate();
                	}
                    else{
                    	resultList.add(mTmpFile.getPath());
                    }
                    
                	//selectComplate();
                }
            }else{
                if(mTmpFile != null && mTmpFile.exists()){
                    mTmpFile.delete();
                }
            }
        }
    }
    
    
    /**
     * 选择图片操作
     * @param imageInfo
     */
    private void selectImageFromGrid(ImageInfo imageInfo) {
        if(imageInfo != null) {
            // 多选模式
            if(!mCameraSdkParameterInfo.isSingle_mode()) {
                if (resultList.contains(imageInfo.path)) {
                    resultList.remove(imageInfo.path);
                    remoreImagePreview(imageInfo.path);
                } else {
                    // 判断选择数量问题
                    if(mCameraSdkParameterInfo.getMax_image() == resultList.size()){
                        Toast.makeText(mContext, R.string.camerasdk_msg_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    resultList.add(imageInfo.path);
                    addImagePreview(imageInfo.path);                    
                }
                mImageAdapter.select(imageInfo);
            }
            else{
                // 单选模式
            	resultList.clear();
            	resultList.add(imageInfo.path);
            	selectComplate();
            }
        }
    }
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.SIZE};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if(id == LOADER_ALL) {
                CursorLoader cursorLoader = new CursorLoader(mContext,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }else if(id == LOADER_CATEGORY){
                CursorLoader cursorLoader = new CursorLoader(mContext,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0]+" like '%"+args.getString("path")+"%'", null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
            	
                List<ImageInfo> imageInfos = new ArrayList<ImageInfo>();
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do{
                    	                    	
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        int size = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                        boolean show_flag=size>1024*10; //是否大于10K
                        ImageInfo imageInfo = new ImageInfo(path, name, dateTime);
                        if(show_flag){
                        	imageInfos.add(imageInfo);
                        }
                        
                        if( !hasFolderGened && show_flag) {
                            // 获取文件夹名称
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            FolderInfo folderInfo = new FolderInfo();
                            folderInfo.name = folderFile.getName();
                            folderInfo.path = folderFile.getAbsolutePath();
                            folderInfo.cover = imageInfo;
                            if (!mResultFolder.contains(folderInfo)) {
                                List<ImageInfo> imageList = new ArrayList<ImageInfo>();
                                imageList.add(imageInfo);
                                folderInfo.imageInfos = imageList;
                                mResultFolder.add(folderInfo);
                            } else {
                                // 更新                                          
                                FolderInfo f = mResultFolder.get(mResultFolder.indexOf(folderInfo));
                                f.imageInfos.add(imageInfo);
                            }
                        }

                    }while(data.moveToNext());

                    mImageAdapter.setData(imageInfos);

                    // 设定默认选择
                    if(resultList != null && resultList.size()>0){
                        mImageAdapter.setSelectedList(resultList);
                        initSelectImage();//预览图
                    }

                    mFolderAdapter.setData(mResultFolder);
                    hasFolderGened = true;

                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };
    
    //预览选择的图片
    private void addImagePreview(final String path){
    	int mItemSize=90;
    	ImageView imageView = (ImageView) LayoutInflater.from(PhotoPickActivity.this).inflate(R.layout.camerasdk_list_item_image_view, selectedImageLayout, false);
		selectedImageLayout.addView(imageView);
		button_complate.setText("完成(" + resultList.size() + "/"+mCameraSdkParameterInfo.getMax_image()+")");
		
		imageView.postDelayed(new Runnable() {
			@Override
			public void run() {

				int off = selectedImageLayout.getMeasuredWidth() - scrollview.getWidth();
				if (off > 0) {
					scrollview.smoothScrollTo(off, 0);
				}
			}
		}, 100);

		hashMap.put(path, imageView);
		File imageFile = new File(path);
		Picasso.with(mContext)
		.load(imageFile)
		.error(R.drawable.camerasdk_pic_loading)
		.resize(mItemSize, mItemSize)
        .centerCrop()
		.into(imageView);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				resultList.remove(path);              
                mImageAdapter.removeOne(path);
				remoreImagePreview(path);
			}
		});
    }

    //删除图片预览
    private boolean remoreImagePreview(String path) {
		if (hashMap.containsKey(path)) {
			selectedImageLayout.removeView(hashMap.get(path));
			hashMap.remove(path);
			button_complate.setText("完成(" + resultList.size() + "/"+mCameraSdkParameterInfo.getMax_image()+")");
			return true;
		} else {
			return false;
		}
	}
    /**
     * 创建弹出的文件夹ListView
     */
    private void showPopupFolder(View v) {
		
    	View view=getLayoutInflater().inflate(R.layout.camerasdk_popup_folder, null); 
		LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.camerasdk_push_up_in));
		
		ListView lsv_folder=(ListView)view.findViewById(R.id.lsv_folder);
		lsv_folder.setAdapter(mFolderAdapter);    	
		if(mpopupWindow==null){
			
			WindowManager manager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
	    	Display display = manager.getDefaultDisplay();
	    	int width=display.getWidth();
	    	int height=display.getHeight();
	    	
			mpopupWindow=new PopupWindow(mContext);
	    	mpopupWindow.setWidth(LayoutParams.MATCH_PARENT);
			mpopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			
			mpopupWindow.setFocusable(true);
			mpopupWindow.setOutsideTouchable(true);
		}
		
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mpopupWindow.dismiss();
			}
		});
		lsv_folder.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				// TODO Auto-generated method stub
				mFolderAdapter.setSelectIndex(arg2);
                final int index = arg2;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    	mpopupWindow.dismiss();
                    	if (index == 0) {
                            getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
                            mCategoryText.setText(R.string.camerasdk_album_all);
                            mImageAdapter.setShowCamera(mCameraSdkParameterInfo.isShow_camera());
                        } else {
                        	FolderInfo folderInfo=(FolderInfo)mFolderAdapter.getItem(index);
                            if (null != folderInfo) {
                                mImageAdapter.setData(folderInfo.imageInfos);
                                mCategoryText.setText(folderInfo.name);
                                // 设定默认选择
                                if (resultList != null && resultList.size() > 0) {
                                    mImageAdapter.setSelectedList(resultList);
                                }
                            }
                           // mImageAdapter.setShowCamera(false);
                        }
                    	// 滑动到最初始位置
                        mGridView.smoothScrollToPosition(0);
                       
                    }
                }, 100);
			}
		});
		mpopupWindow.setContentView(view);		
		mpopupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
		mpopupWindow.showAsDropDown(findViewById(R.id.layout_actionbar_root));
	}

    
    
    //选择完成实现跳转
    private void selectComplate(){
    	
    	mCameraSdkParameterInfo.setImage_list(resultList);
		Bundle b=new Bundle();
		b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
		
		Intent intent = new Intent();
		intent.putExtras(b);
		
		if(mCameraSdkParameterInfo.isSingle_mode()){
			//单选模式
			if(mCameraSdkParameterInfo.isCroper_image()){
				//跳转到图片裁剪	    		
				intent = new Intent(this, CropperImageActivity.class);
				intent.putExtras(b);
				startActivity(intent);
			}
			else if(mCameraSdkParameterInfo.isFilter_image()){
				//跳转到滤镜	    		
				intent = new Intent(this, FilterImageActivity.class);
				intent.putExtras(b);
				startActivity(intent);
			}
			else{
				 setResult(RESULT_OK, intent);
		         finish();
			}
		}
		else{
			//多选模式
			if(mCameraSdkParameterInfo.isFilter_image()){
				//跳转到滤镜	    		
				intent = new Intent(this, FilterImageActivity.class);
				intent.putExtras(b);
				startActivity(intent);
			}
			else{
				 setResult(RESULT_OK, intent);
		         finish();
			}
		}
    	 
    }
    
    //返回特效处理后的图片
    public void getFilterComplate(ArrayList<String> list){
    	mCameraSdkParameterInfo.setImage_list(list);
		Bundle b=new Bundle();
		b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
		
		Intent intent = new Intent();
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
        finish();
    }
        
    //返回裁剪后的图片
    public void getForResultComplate(String path){
    	
    	ArrayList<String> list=new ArrayList<String>();
    	list.add(path);
    	
    	Intent intent = new Intent();
    	mCameraSdkParameterInfo.setImage_list(list);
		Bundle b=new Bundle();
		b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
        finish();
    }
    
    
}
