/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.muzhi.camerasdk.library.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.*;
import android.util.AttributeSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

import com.muzhi.camerasdk.library.filter.GPUImage.ScaleType;


public class GPUImageView_Simple extends GLSurfaceView{

	private Context mContext;

	private GPUImage mGPUImage;
	private GPUImageFilter mFilter;
	public Size mForceSize = null;
	private float mRatio = 0.0f;
	
	private int viewWidth,viewHeight;

	public static class Size{
		int width;
		int height;
		public Size(int width, int height){
			this.width = width;
			this.height = height;
		}
	}

	public GPUImageView_Simple(Context context, AttributeSet attrs){
		super(context, attrs);
		this.mContext = context;
		initView(context);
	}

	public GPUImageView_Simple(Context context){
		super(context);
		this.mContext = context;
		initView(context);
	}

	private void initView(Context context){
		mGPUImage = new GPUImage(context);
		mGPUImage.setGLSurfaceView(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		if (mRatio != 0.0f){
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);

			int newHeight;
			int newWidth;
			if (width / mRatio < height){
				newWidth = width;
				newHeight = Math.round(width / mRatio);
			} 
			else{
				newHeight = height;
				newWidth = Math.round(height * mRatio);
			}

			int newWidthSpec = 0, newHeightSpec = 0;

			if (mForceSize != null){
				newWidthSpec = MeasureSpec.makeMeasureSpec(mForceSize.width,MeasureSpec.EXACTLY);
				newHeightSpec = MeasureSpec.makeMeasureSpec(mForceSize.height,MeasureSpec.EXACTLY);
			} else{
				newWidthSpec = MeasureSpec.makeMeasureSpec(newWidth,MeasureSpec.EXACTLY);
				newHeightSpec = MeasureSpec.makeMeasureSpec(newHeight,MeasureSpec.EXACTLY);

			}
			super.onMeasure(newWidthSpec, newHeightSpec);

		} 
		else{
			if (mForceSize != null){
				super.onMeasure(MeasureSpec.makeMeasureSpec(mForceSize.width,MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mForceSize.height, MeasureSpec.EXACTLY));
			} else{
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			}

		}
	}

	public void setRatio(float ratio){
		mRatio = ratio;
		this.updateLayout();
		mGPUImage.deleteImage();
	}

	/**
	 * 设置缩放类型
	 * 
	 * @param 缩放类型
	 * 
	 */
	public void setScaleType(ScaleType type){
		mGPUImage.setScaleType(type);
	}

	/**
	 * 设置旋转角度
	 * 
	 * @param 角度类型
	 * 
	 */
	public void setRotation(Rotation rotation){
		mGPUImage.setRotation(rotation);
		this.updateLayout();
	}

	public void setImage(Bitmap bitmap){
		
		int Width = bitmap.getWidth() ;
		int Height = bitmap.getHeight();
		
//		if(Width > 720)
//		{
//			Width = 720;
//		}
//		if(Height > 1230)
//		{
//			Height = 1230;
//		}
		//mForceSize = new Size(Width, Height);
		
		
		mGPUImage.setImage(bitmap);
	}

	public void setImage(Uri uri){
		mGPUImage.setImage(uri);
	}

	/**
	 * 设置图片滤镜
	 * 
	 * @param 滤镜
	 * 
	 */
	public void setImage(File file){
		mGPUImage.setImage(file);
	}

	public void setFilter(GPUImageFilter filter){
		mFilter = filter;
		mGPUImage.setFilter(filter);
		this.updateLayout();
	}

	public GPUImage getGPUImage(){
		return mGPUImage;
	}

	/**
	 * 获得当前的滤镜
	 * 
	 * @return 滤镜
	 */
	public GPUImageFilter getFilter(){
		return mFilter;
	}

	public interface OnPictureSavedListener{
		void onPictureSaved(Uri uri);
	}

	public void updateLayout(){
		this.requestLayout();
	}

	public void updateRender(){
		this.requestRender();
	}

	/******************************************************************************************************/

	public Bitmap captureBitmap() throws InterruptedException{
		final Semaphore waiter = new Semaphore(0);

		final int width = this.getMeasuredWidth();
		final int height = this.getMeasuredHeight();

		// Take picture on OpenGL thread
		final int[] pixelMirroredArray = new int[width * height];
		mGPUImage.runOnGLThread(new Runnable(){
			@Override
			public void run(){
				final IntBuffer pixelBuffer = IntBuffer.allocate(width * height);
				GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
				int[] pixelArray = pixelBuffer.array();

				// Convert upside down mirror-reversed image to right-side up
				// normal image.
				for (int i = 0; i < height; i++){
					for (int j = 0; j < width; j++){
						pixelMirroredArray[(height - i - 1) * width + j] = pixelArray[i* width + j];
					}
				}
				waiter.release();
			}
		});
		requestRender();
		waiter.acquire();

		Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
		bitmap.copyPixelsFromBuffer(IntBuffer.wrap(pixelMirroredArray));
		return bitmap;
	}

	private class SaveTask extends AsyncTask<Void, Void, Void>{
		private final String mFolderName;
		private final String mFileName;
		private final int mWidth;
		private final int mHeight;
		private final OnPictureSavedListener mListener;
		private final Handler mHandler;

		public SaveTask(final String folderName, final String fileName,final OnPictureSavedListener listener){
			this(folderName, fileName, 0, 0, listener);
		}

		public SaveTask(final String folderName, final String fileName,int width, int height, final OnPictureSavedListener listener){
			mFolderName = folderName;
			mFileName = fileName;
			mWidth = width;
			mHeight = height;
			mListener = listener;
			mHandler = new Handler();
		}

		@Override
		protected Void doInBackground(final Void... params)
		{
			try{
				Bitmap result = captureBitmap();
				saveImage(mFolderName, mFileName, result);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
			return null;
		}

		private void saveImage(final String folderName, final String fileName,final Bitmap image){
			File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			File file = new File(path, folderName + "/" + fileName);
			try{
				file.getParentFile().mkdirs();
				image.compress(Bitmap.CompressFormat.JPEG, 100,new FileOutputStream(file));
				MediaScannerConnection.scanFile(getContext(),
						new String[] { file.toString() }, null,
						new MediaScannerConnection.OnScanCompletedListener()
						{
							@Override
							public void onScanCompleted(final String path,final Uri uri){
								if (mListener != null){
									mHandler.post(new Runnable(){
										@Override
										public void run(){
											mListener.onPictureSaved(uri);
										}
									});
								}
							}
						});
			} catch (FileNotFoundException e){
				e.printStackTrace();
			}
		}
	}

}
