package com.muzhi.camerasdk.model;


import java.io.Serializable;

/**
 * 一个图片对象
 * 
 * @author Administrator
 * 
 */
public class CameraImageInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String upload_image; //经过压缩处理图片
	public String source_image; //源图
	public boolean isAddButton = false; //是否是添加按钮
	
	
	public String getUpload_image() {
		return upload_image;
	}
	public void setUpload_image(String upload_image) {
		this.upload_image = upload_image;
	}
	public String getSource_image() {
		return source_image;
	}
	public void setSource_image(String source_image) {
		this.source_image = source_image;
	}
	public boolean isAddButton() {
		return isAddButton;
	}
	public void setAddButton(boolean isAddButton) {
		this.isAddButton = isAddButton;
	}
	
	
}
