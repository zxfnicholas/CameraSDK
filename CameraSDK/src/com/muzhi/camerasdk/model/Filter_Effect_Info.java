package com.muzhi.camerasdk.model;

import com.muzhi.camerasdk.library.filter.util.ImageFilterTools;
import com.muzhi.camerasdk.library.filter.util.ImageFilterTools.FilterType;

public class Filter_Effect_Info {
	
	private String name = "";      
	private int iconId = 0;    
	private FilterType filterType;
	
	public Filter_Effect_Info(String name,int iconId,ImageFilterTools.FilterType type){
		this.name = name;
		this.iconId = iconId;
		this.filterType=type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}
	
}
