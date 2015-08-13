package com.muzhi.camerasdk.example.model;

import java.io.Serializable;

/**
 *图片对像
 * 
 * @author zengxiaofeng
 * 
 */
public class ImageInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String source_image; //源图
	public boolean isAddButton = false; //是否是添加按钮
	
	
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
