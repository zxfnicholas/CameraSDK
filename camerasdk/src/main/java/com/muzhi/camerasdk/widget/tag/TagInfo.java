package com.muzhi.camerasdk.widget.tag;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

import java.io.Serializable;

public final class TagInfo implements Parcelable, Serializable {
	private static final long serialVersionUID = -2939266917839493174L;
	public String bname = "";
	public long bid = 0L;
	public double pic_x = 0.0D;
	public double pic_y = 0.0D;
	public Direction direct = Direction.Left;
	public Type type = Type.Undefined;
	
	public int leftMargin;
	public int topMargin;

	public enum Direction {
		Left("left"), Right("right");
		
		private Direction(String valString) {
			this.valueString = valString;
		}
		
		public static int size(){
			return Direction.values().length;
		}
		
		public String valueString;
		
		public String toString(){
			return valueString;
		}
		
		public Direction valueof(String vaString){
			if(vaString.equals("left")){
				return Direction.Left;
			}else if(vaString.equals("right")){
				return Direction.Right;
			}else{
				return null;
			}
		}
	}

	public enum Type {
		Undefined("undefined"),Exists("exists"),CustomPoint("custom_point"),OfficalPoint("offical_point");
		
		private Type(String typeString) {
			this.valueString = typeString;
		}
		
		public static int size(){
			return Type.values().length;
		}
		
		public String valueString;
		
		public String toString(){
			return valueString;
		}
		
		public Type valueof(String vaString){
			if(vaString.equals("undefined")){
				return Type.Undefined;
			}else if(vaString.equals("exists")){
				return Type.Exists;
			}else if(vaString.equals("custom_point")){
				return Type.CustomPoint;
			}else if(vaString.equals("offical_point")){
				return Type.OfficalPoint;
			}else{
				return null;
			}
		}
	}

	public TagInfo() {
	}

	private TagInfo(Parcel paramParcel) {
		this.bname = paramParcel.readString();
		this.bid = paramParcel.readLong();
		this.pic_x = paramParcel.readDouble();
		this.pic_y = paramParcel.readDouble();
		this.direct = Direction.valueOf(paramParcel.readString());
		this.type = Type.valueOf(paramParcel.readString());
	}

	public TagInfo(JSONObject paramJSONObject) {
		String str = null;
		try {
			this.bid = paramJSONObject.getLong("bid");
			this.bname = paramJSONObject.getString("bname");
			this.pic_x = paramJSONObject.getDouble("pic_x");
			this.pic_y = paramJSONObject.getDouble("pic_y");
			this.direct = Direction.valueOf(paramJSONObject.getString("direct"));
			if(null == direct){
				throw new RuntimeException("taginfo no direction");
			}
			this.type = Type.Undefined;
			if (!paramJSONObject.has("type")) {
				return;
			}
			str = paramJSONObject.getString("type");
			if (str.equals("exists")) {
				this.type = Type.Exists;
				return;
			}
			if (str.equals("custom_point")) {
				this.type = Type.CustomPoint;
				return;
			}
			if (str.equals("offical_point")) {
				this.type = Type.OfficalPoint;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TagInfo getInstance(JSONObject paramJSONObject) {
		return new TagInfo(paramJSONObject);
	}

	public final JSONObject getjson() {
		JSONObject jsonobject = new JSONObject();
		try {
			jsonobject.put("bid", bid);
			jsonobject.put("bname", bname);
			jsonobject.put("pic_x", String.valueOf(pic_x));
			jsonobject.put("pic_y", String.valueOf(pic_y));
			jsonobject.put("direct", direct.toString());
			jsonobject.put("type", type.toString());
			return jsonobject;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return jsonobject;
	}

	public final int describeContents() {
		return 0;
	}

	public final void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(bname);
		parcel.writeLong(bid);
		parcel.writeDouble(pic_x);
		parcel.writeDouble(pic_y);
		parcel.writeString(direct.toString());
		parcel.writeString(type.toString());
	}
}