package com.muzhi.camerasdk.model;

import java.io.Serializable;

import android.graphics.Bitmap;

public class Filter_Sticker_Info implements Serializable{


	private static final long serialVersionUID = 1L;
	
	
	private int id=0;					//贴纸id
	private int fid=0;					//父类id
	private String name="";				//贴纸名称
	private String image="";			//贴纸图片	
	private String sample_url = "";     //范例图片Url
	private String describe="";			//描述	
	private int count = 0;				//有多少人使用
	private float price;				//价格
	private String createtime;			//添加时间
	
	
	
	//扩展属性
	private String local_path="";		//贴纸本地路径
	private boolean isLib=false;		//为true则打开贴纸库
	private int drawableId; 			//本地资源ID
	private Bitmap bitmap;				//图像
	
	public Filter_Sticker_Info() {
		
	}
	
	public Filter_Sticker_Info(int drawableId) {
		this.drawableId = drawableId;
	}
	
	public Filter_Sticker_Info(int drawableId,boolean lib) {
		this.drawableId = drawableId;
		this.isLib=lib;
	}

	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFid() {
		return fid;
	}

	public void setFid(int fid) {
		this.fid = fid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getSample_url() {
		return sample_url;
	}

	public void setSample_url(String sample_url) {
		this.sample_url = sample_url;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getLocal_path() {
		return local_path;
	}

	public void setLocal_path(String local_path) {
		this.local_path = local_path;
	}

	public boolean isLib() {
		return isLib;
	}

	public void setLib(boolean isLib) {
		this.isLib = isLib;
	}

	public int getDrawableId() {
		return drawableId;
	}

	public void setDrawableId(int drawableId) {
		this.drawableId = drawableId;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
}
