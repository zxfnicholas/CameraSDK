package com.muzhi.camerasdk.model;

import android.graphics.Bitmap;

public class Filter_Sticker_Info {

	public int id=0;					//贴纸id
	public int fid=0;					//父类id
	public String name="";				//贴纸名称
	public String url="";				//贴纸url
	public String local_path="";		//贴纸路径
	public String sample_url = "";      //范例图片Url
	public int useCount = 0;			//有多少人使用
	public int drawableId; 				//图像ID
	public Bitmap bitmap;				//图像
	
	
	public Filter_Sticker_Info(int drawableId) {
		this.drawableId = drawableId;
	}
	
	/*public Filter_Sticker_Info(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	*/
}
