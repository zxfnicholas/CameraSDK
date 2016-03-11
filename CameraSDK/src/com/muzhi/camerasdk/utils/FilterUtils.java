package com.muzhi.camerasdk.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.muzhi.camerasdk.R;
import com.muzhi.camerasdk.library.filter.util.ImageFilterTools.FilterType;
import com.muzhi.camerasdk.model.Filter_Effect_Info;
import com.muzhi.camerasdk.model.Filter_Sticker_Info;


/**
 * 特效文件
 */
public class FilterUtils {

	
	
	/**
	 * 获取特效列表
	 * @return
	 */
	public static ArrayList<Filter_Effect_Info> getEffectList(){
		
		ArrayList<Filter_Effect_Info> effect_list = new ArrayList<Filter_Effect_Info>();
		
		effect_list.add(new Filter_Effect_Info("原图", R.drawable.camerasdk_filter_normal,FilterType.I_1977));
		effect_list.add(new Filter_Effect_Info("创新", R.drawable.camerasdk_filter_in1977,FilterType.I_1977));
		effect_list.add(new Filter_Effect_Info("流年", R.drawable.camerasdk_filter_amaro,FilterType.I_AMARO));
		effect_list.add(new Filter_Effect_Info("淡雅", R.drawable.camerasdk_filter_brannan,FilterType.I_BRANNAN));
		effect_list.add(new Filter_Effect_Info("怡尚", R.drawable.camerasdk_filter_early_bird,FilterType.I_EARLYBIRD));
		effect_list.add(new Filter_Effect_Info("优格", R.drawable.camerasdk_filter_hefe,FilterType.I_HEFE));
		effect_list.add(new Filter_Effect_Info("胶片", R.drawable.camerasdk_filter_hudson,FilterType.I_HUDSON));
		effect_list.add(new Filter_Effect_Info("黑白", R.drawable.camerasdk_filter_inkwell,FilterType.I_INKWELL));
		effect_list.add(new Filter_Effect_Info("个性", R.drawable.camerasdk_filter_lomo,FilterType.I_LOMO));
		effect_list.add(new Filter_Effect_Info("回忆", R.drawable.camerasdk_filter_lord_kelvin,FilterType.I_LORDKELVIN));
		effect_list.add(new Filter_Effect_Info("不羁", R.drawable.camerasdk_filter_nashville,FilterType.I_NASHVILLE));
		effect_list.add(new Filter_Effect_Info("森系", R.drawable.camerasdk_filter_rise,FilterType.I_NASHVILLE));
		effect_list.add(new Filter_Effect_Info("清新", R.drawable.camerasdk_filter_sierra,FilterType.I_SIERRA));
		effect_list.add(new Filter_Effect_Info("摩登", R.drawable.camerasdk_filter_sutro,FilterType.I_SUTRO));
		effect_list.add(new Filter_Effect_Info("绚丽", R.drawable.camerasdk_filter_toaster,FilterType.I_TOASTER));
		effect_list.add(new Filter_Effect_Info("优雅", R.drawable.camerasdk_filter_valencia,FilterType.I_VALENCIA));
		effect_list.add(new Filter_Effect_Info("日系", R.drawable.camerasdk_filter_walden,FilterType.I_WALDEN));
		effect_list.add(new Filter_Effect_Info("新潮", R.drawable.camerasdk_filter_xproii,FilterType.I_XPROII));
		
		return effect_list;
		
	}
	
	
	
	/**
	 * 获取所有贴纸
	 * @param context
	 * @return
	 */
    public static ArrayList<Filter_Sticker_Info> getStickerList(){

    	ArrayList<Filter_Sticker_Info> stickerList = new ArrayList<Filter_Sticker_Info>();
		
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_1));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_2));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_33));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_4));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_5));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_6));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_7));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_8));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_9));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_10));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_11));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_12));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_13));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_14));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_15));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_16));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_17));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_18));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_19));
		stickerList.add(new Filter_Sticker_Info(R.drawable.sticker_20));
		//stickerList.add(new Filter_Sticker_Info(R.drawable.camerasdk_stickers,true));
		return stickerList;

    }

    
    /**
	 * 获取所有贴纸
	 * @return
	 */
	public static void initSticker(Context mContext){
		
		
		/*String fileName="sticker/sticker.txt";
    	try{
    		 InputStreamReader inputReader = new InputStreamReader(mContext.getAssets().open(fileName),Charset.defaultCharset()); 
    		 BufferedReader bufReader = new BufferedReader(inputReader);
             String line="";
             String Result="";
             while((line = bufReader.readLine()) != null){
            	 Result += line.trim(); 
             }
             bufReader.close();
             inputReader.close(); 
             if(!"".equals(Result)){                 	
             	emotions=getGson().fromJson(Result, new TypeToken<List<EmotionInfo>>(){}.getType());
             	getEmotionsTask();
             }
    	}
    	catch(Exception e){
    		 e.printStackTrace(); 
    	} */
		
		
		
		
		
		
	}
    
	/*private static void getEmotionsTask() {
    	
    	AssetManager assetManager = AppData.getInstance().getAssets();
    	InputStream inputStream;
    	for(EmotionInfo em : emotions){
    		String fileName="smiley/smileys/"+em.getFileName();
    		//String smileyName=em.getSmileyString();
    		try{
    			inputStream = assetManager.open(fileName);
    			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
    			if(bitmap!=null){
    				Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, CommonUtils.dip2px(20), CommonUtils.dip2px(20), true);
    				if(bitmap != scaledBitmap){
    					bitmap.recycle();
    					bitmap = scaledBitmap;
    				}	    				
    				em.setEmotionImage(bitmap);
    			} 
    			inputStream.close();
    		}
    		catch (IOException ignored) {

            }
    	}    	
    }*/
    
    
}
