package com.muzhi.camerasdk.model;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class MediaStoreCursorHelper {

//    public static final String[] PHOTOS_PROJECTION = { Images.Media.DATA };
    public static final String[] PHOTOS_PROJECTION = {Images.Media._ID,Images.Media.MINI_THUMB_MAGIC,Images.Media.DATA, Images.Media.BUCKET_DISPLAY_NAME, Images.Media.BUCKET_ID};
    public static final String PHOTOS_ORDER_BY = Images.Media.DATE_ADDED + " desc";
    public static final Uri MEDIA_STORE_CONTENT_URI = Images.Media.EXTERNAL_CONTENT_URI;
    
    public static List<MediaStoreBucket> getBucket(Context context) {
		ArrayList<MediaStoreBucket> result = null;
		if (null != context) {
			result = new ArrayList<MediaStoreBucket>();
			result.add(MediaStoreBucket.getAllPhotosBucket(context));
			Cursor cursor = MediaStoreCursorHelper.openPhotosCursor(context, MediaStoreCursorHelper.MEDIA_STORE_CONTENT_URI);
			if (null != cursor) {
				MediaStoreCursorHelper.photosCursorToBucketList(context,cursor, result);
				if (VERSION.SDK_INT < 14){
					cursor.close();
				}
			}
		}
		return result;
	}

    public static ArrayList<ImageBucket> queryPhotoByBucketID(Context mContext, String bucketID) {
		ArrayList<ImageBucket> dataList = new ArrayList<ImageBucket>();
		String selection = Images.Media.BUCKET_ID + " = ?";
		String[] selectionArgs = { bucketID };
		Cursor queryCursor = mContext.getContentResolver().query(MEDIA_STORE_CONTENT_URI, PHOTOS_PROJECTION, selection, selectionArgs, PHOTOS_ORDER_BY);
		for (int i = 0; i < queryCursor.getCount(); i++) {
			queryCursor.moveToPosition(i);
			
			ImageBucket ib=new ImageBucket();
			int columnIndexOrThrow = queryCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			ib.bucketThumb=queryCursor.getString(columnIndexOrThrow);
			ib.count=queryCursor.getCount();
			dataList.add(ib);
		}
		if (VERSION.SDK_INT < 14){
			queryCursor.close();
		}
		return dataList;
	}
    
    
	/*public static ArrayList<String> queryAllPhoto(Activity activity) {
		ArrayList<String> dataList = new ArrayList<String>();
		Cursor imagecursor = activity.managedQuery(MEDIA_STORE_CONTENT_URI, PHOTOS_PROJECTION, null, null, PHOTOS_ORDER_BY);

		for (int i = 0; i < imagecursor.getCount(); i++) {
			imagecursor.moveToPosition(i);
			int dataColumnIndex = imagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			dataList.add(imagecursor.getString(dataColumnIndex));
		}
		if (VERSION.SDK_INT < 14){
			imagecursor.close();
		}
		return dataList;
	}*/
    
    
    
    public static ArrayList<ImageBucket> queryAllPhoto(Context mContext) {
		ArrayList<ImageBucket> dataList = new ArrayList<ImageBucket>();
		Cursor queryCursor = mContext.getContentResolver().query(MEDIA_STORE_CONTENT_URI, PHOTOS_PROJECTION, null, null, PHOTOS_ORDER_BY);

		for (int i = 0; i < queryCursor.getCount(); i++) {
			queryCursor.moveToPosition(i);
			/*int dataColumnIndex = imagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			dataList.add(imagecursor.getString(dataColumnIndex));*/
			
			ImageBucket ib=new ImageBucket();
			int columnIndexOrThrow = queryCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			ib.bucketThumb=queryCursor.getString(columnIndexOrThrow);
			ib.count=queryCursor.getCount();
			dataList.add(ib);
			
			
		}
		if (VERSION.SDK_INT < 14){
			queryCursor.close();
		}
		return dataList;
	}
    
	public static void photosCursorToBucketList(Context context,Cursor cursor, ArrayList<MediaStoreBucket> items) {
        final HashSet<String> bucketIds = new HashSet<String>();

        final int idColumn = cursor.getColumnIndex(ImageColumns.BUCKET_ID);
        final int nameColumn = cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME);
        
        if (cursor.moveToFirst()) {
            do {
                try {
                    final String bucketId = cursor.getString(idColumn);                    
                    if (bucketIds.add(bucketId)) {
                        items.add(new MediaStoreBucket(bucketId, cursor.getString(nameColumn)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
    }

    public static Cursor openPhotosCursor(Context context, Uri contentUri) {
        return context.getContentResolver().query(contentUri, PHOTOS_PROJECTION, null, null, PHOTOS_ORDER_BY);
               
    }

}
