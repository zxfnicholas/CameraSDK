package com.muzhi.camerasdk.library.filter.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class ImageFilterUtil {
	
	public static int type_photo = 0; 
	public static int type_voice = 1;
	public static int type_temp = 2;
	

	public static void deleteDBs(String path) {  
		File db = new File(path);
        deleteFileOrDirectory(db);
    } 
	
	public static void deleteFileOrDirectory(File file) {
		try {
			deleteSubFiles(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteSubFiles(File file) {
		try {
			if (!file.exists()) {
				return;
			}
			String path = file.getAbsolutePath();
			if (file.isFile()) {
				file.delete();
			} 
			else if (file.isDirectory()) {
				String[] tempList = file.list();
				File temp = null;
				for (int i = 0; i < tempList.length; i++) {
					if (path.endsWith(File.separator)) {
						temp = new File(path + tempList[i]);
					} 
					else {
						temp = new File(path + File.separator + tempList[i]);
					}
					if (temp.isFile()) {
						temp.delete();
					}
					if (temp.isDirectory()) {
						deleteSubFiles(new File(path + "/" + tempList[i]));
					}
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean copyFile(String oldPath,  String newPath) {
		try {
			File oldfile = new File(oldPath);
			int byteread = 0;
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024 * 4];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();				
				fs.close();
				return true;
			}
			else{
				return false;
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    } 
    
    public static int px2dip(Context context, float pxValue) {  
    	final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f); 
    }
    
    public static int getscreenwidth(Context context){
		DisplayMetrics dm2 = context.getResources().getDisplayMetrics();  
		return dm2.widthPixels;
	}
	
	public static int getscreenheight(Context context){
		DisplayMetrics dm2 = context.getResources().getDisplayMetrics();  
		return dm2.heightPixels;
	}
	
	public static int getStatusBarHeight() {
        return Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
    }
	
	
	
	
	
	public static Bitmap decodeFile(File f,int REQUIRED_SIZE) {
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			
			if (width_tmp > 1000){
				width_tmp = o.outWidth;
				scale = 2;
				while (width_tmp/scale > 1000){
					scale++;
				}
			} 
			
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			try {
				FileInputStream is = new FileInputStream(f);
				return BitmapFactory.decodeStream(is, null, o2);
            } catch (OutOfMemoryError err) {
				err.printStackTrace();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Bitmap decodeImage(Bitmap bitmap,int REQUIRED_SIZE) {
		try {
			int width_tmp = bitmap.getWidth(), height_tmp = bitmap.getHeight();
			int tmp = width_tmp > height_tmp ? height_tmp : width_tmp;
			Matrix matrix = new Matrix();  
			if(tmp > REQUIRED_SIZE)
			{
				float scale = ((float)REQUIRED_SIZE) / tmp;
				matrix.postScale(scale, scale);
			}
			else{
				matrix.postScale(1, 1);
			}
			return Bitmap.createBitmap(bitmap, 0, 0, width_tmp, height_tmp, matrix, true);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}
	
	public static Bitmap getImage(Context context, String path, int size) {
		Bitmap bitmap = null;
		try {
			int angle = 0;
			ExifInterface exif = new ExifInterface(path);
			// We only recognize a subset of orientation tag values
			switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED)) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				angle = 90;
				break;

			case ExifInterface.ORIENTATION_ROTATE_180:
				angle = 180;
				break;

			case ExifInterface.ORIENTATION_ROTATE_270:
				angle = 270;
				break;

			default:
				angle = ExifInterface.ORIENTATION_UNDEFINED;
				break;
			}
			bitmap = decodeFile(new File(path),size);//根据Path读取资源图片
			if (angle != 0) {
				// 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
				Matrix m = new Matrix();
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				m.setRotate(angle); // 旋转angle度
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m,true);// 从新生成图片
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public static SpannableStringBuilder  gettextstyle(String str,String key,int color){
		int index = str.toUpperCase().indexOf(key.toUpperCase());
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		if (index != -1) {
			style.setSpan(new ForegroundColorSpan(color), index, index + key.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		}
		return style;
	}
	
	public static SpannableStringBuilder  gettextstyle(SpannableString str,String key,int color){
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new ForegroundColorSpan(color), 0,  key.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		return style;
	}
	
	public static SpannableStringBuilder  gettextstylefromend(SpannableString str,String key,int color){
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new ForegroundColorSpan(color), str.length()-key.length(),  str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		return style;
	}
	
	public static String inputStream2String(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		int n;
		while ((n = in.read(b)) != -1) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}
	
	public static String getCurDateforFileName(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}
	
	public static String getCurDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}
	
	public static String getCurDay(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}
	
	public static String getCurDatetoMd5(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return md5(formatter.format(curDate));
	}
	
	public static String getDate(long milliseconds){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(milliseconds);// 获取当前时间
		return formatter.format(curDate);
	}
	

	public static void openKeyboard(Context mContext) {
		((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
    }
	
	public static void closeKeyboard(Activity ac) {
		if(ac == null){
			return;
		}
        InputMethodManager imm = (InputMethodManager) ac.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(ac.getCurrentFocus() != null) { 
            imm.hideSoftInputFromWindow(ac.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
	
	
	/**
	 * 计算两个日期型的时间相差多少时间
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return
	 */
	public static String twoDateDistance(String startTime) {
		String result = "";
		Date startDate = null;
		if(startTime == null || startTime.trim().length() == 0) return result;
		try {
			startDate = ConverToDate(startTime);
			Date curDate = new Date(System.currentTimeMillis());
			long timeLong = curDate.getTime() - startDate.getTime();
			
			long oneMin = 60l  * 1000;
			long oneHour = 60l * 60 * 1000;
			long oneDay = 60l * 60 * 1000 * 24;
			long oneMonth = 60l * 60 * 1000 * 24 * 30;
			long oneYear = 60l * 60 * 1000 * 24 * 30 * 12;
			
			if (timeLong < oneMin) {  //3600 000
				result = "刚刚";
			} 
			else if (timeLong < oneHour && timeLong >= oneMin) {  //6 000----3600 000
				timeLong = timeLong / oneMin;
				result = timeLong + "分钟前";
			} 
			else if (timeLong < oneDay && timeLong >= oneHour) {  //3600 000----86400 000
				timeLong = timeLong / oneHour;
				result = timeLong + "小时前";
			} 
			else if (timeLong < oneMonth && timeLong >= oneDay) { //86400 000------2592000 000
				timeLong = timeLong / oneDay;
				result = timeLong + "天前";
			} 
			else if (timeLong < oneYear && timeLong >= oneMonth) { //2592000 000 -----31,104,000 000
				timeLong = timeLong / oneMonth;
				result = timeLong + "月前";
			} 
			else {
				timeLong = timeLong / oneYear;
				result = timeLong + "年前";
			} 
//			else {
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
//				result = sdf.format(startDate);
//			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String msgDatefromNow(String startTime) {
		String result = "";
		Date startDate = null;
		if(startTime == null || startTime.trim().length() == 0) return result;
		try {
			startDate = ConverToDate(startTime);
			Date curDate = new Date(System.currentTimeMillis());
			long timeLong = curDate.getTime() - startDate.getTime();
			
			if (timeLong < 60l * 1000){
				result = "刚刚";
			}
			else if (timeLong < 30 * 60l * 1000) {
				timeLong = timeLong / 1000 / 60;
				result = timeLong + "分钟前";
			} 
			else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
				result = sdf.format(startDate);
//				result = result.replaceFirst(curYear(), "");
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
//	public static String curYear(){
//        SimpleDateFormat sf = new SimpleDateFormat("yyyy");  
//        return sf.format(System.currentTimeMillis()) + "-";  
//	}
	
	public static boolean isDateOverNow(String time) {
		Date startDate = null;
		if(time == null || time.trim().length() == 0) return true;
		try {
			startDate = ConverToDate(time + " 00:00:00");
			Date curDate = new Date(System.currentTimeMillis());
			long timeLong = curDate.getTime() - startDate.getTime();
			
			return timeLong < 0;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}
	
	public static boolean twoDateDistance(String startTime,String endTime) {
		boolean result = false;
		try {
			Date startDate = ConverToDate(startTime);
			Date endDate = ConverToDate(endTime);
			long timeLong = endDate.getTime() - startDate.getTime();
			
			if (Math.abs(timeLong) < 60 * 1000 * 3){
				result = true;
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static boolean isTimeNew(String curTime,String mTime) {
		boolean result = false;
		try {
			Date curDate = ConverToDate(curTime);
			Date mDate = ConverToDate(mTime);
			
			result = curDate.getTime() - mDate.getTime() > 0;
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static Date ConverToDate(String strDate) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.parse(strDate);
	}
	

	


	

	
	
	
	public static void clipText(Context context, String text) {
		if (android.os.Build.VERSION.SDK_INT < 11) {
			android.text.ClipboardManager mClipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			mClipboard.setText(text);
		}
		else{
			ClipboardManager mClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("simple text", text);
			mClipboard.setPrimaryClip(clip);
		}
		Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
	}
	
	public static void updateMedia( Context context,String filepath ) {
		MediaScannerConnection.scanFile( context, new String[] { filepath }, null, null );
	}
	
	public static BitmapDrawable getDrawable(Context context, int resource_Id){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resource_Id);
		Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
		return new BitmapDrawable(context.getResources(), bm);
	}
}






