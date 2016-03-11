package com.muzhi.camerasdk.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.muzhi.camerasdk.library.filter.GPUImageFilter;
import com.muzhi.camerasdk.library.views.HSuperImageView;

import android.graphics.Bitmap;

/**
 * 图片实体
 */
public class EditFilterImageInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String path;
    private Bitmap bitmap;
    private ArrayList<HSuperImageView> sticklist; // 保存贴纸图片的集合
    private ArrayList<Integer> stickIds;			  //保存贴纸图片的ID集合
   	private GPUImageFilter filter;
	
   	
   	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public ArrayList<HSuperImageView> getSticklist() {
		if(this.sticklist==null){
			this.sticklist=new ArrayList<HSuperImageView>();
		}
		return sticklist;
	}
	public void setSticklist(ArrayList<HSuperImageView> sticklist) {
		this.sticklist = sticklist;
	}
	
	
	public GPUImageFilter getFilter() {
		return filter;
	}
	public void setFilter(GPUImageFilter filter) {
		this.filter = filter;
	}

	public void addSticker(HSuperImageView sticker) {
		if(this.sticklist==null){
			this.sticklist=new ArrayList<HSuperImageView>();
		}
		this.sticklist.add(sticker);
	}
	
	public ArrayList<Integer> getStickIds() {
		if(this.stickIds==null){
			this.stickIds=new ArrayList<Integer>();
		}
		return stickIds;
	}
	public void setStickIds(ArrayList<Integer> stickIds) {
		this.stickIds = stickIds;
	}
	
	
	public void addStickId(int id){
		if(this.stickIds==null){
			this.stickIds=new ArrayList<Integer>();
		}
		this.stickIds.add(id);
	}
	public void delStickId(Integer id){
		if(this.stickIds!=null){
			this.stickIds.remove(id);
		}
		
		/*for(Integer item:stickIds){
			if(item==id){
				stickIds.remove(item);
			}
		}*/
	}
}
