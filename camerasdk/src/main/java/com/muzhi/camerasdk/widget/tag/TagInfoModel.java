package com.muzhi.camerasdk.widget.tag;

import java.io.Serializable;

/**
 * Created by jazzy
 */
public class TagInfoModel implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public float y;

	public float x;

	public String tag_name;
	
	
	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public String getTag_name() {
		return tag_name;
	}

	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
	}

	
}
