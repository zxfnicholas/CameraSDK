package com.muzhi.camerasdk.library.utils;

import android.content.Context;
import android.content.res.Resources;

public class MResource {
	
	public static final String layout="layout";
	public static final String string="string";
	public static final String style="style";
	public static final String anim="anim";
	public static final String attr="attr";
	public static final String raw="raw";
	public static final String id="id";
	public static final String drawable="drawable";
	
	public static int getIdByName(Context context, String className, String name){
		
		String packageName = context.getPackageName();  
        Class r = null;  
        int id = 0;  
        try {  
            r = Class.forName(packageName + ".R"); 
            Class[] classes = r.getClasses();  
            Class desireClass = null;  
            for (int i = 0; i < classes.length; ++i) {  
                if (classes[i].getName().split("\\$")[1].equals(className)) {  
                    desireClass = classes[i];  
                    break;  
                }  
            } 
            if (desireClass != null)  
                id = desireClass.getField(name).getInt(desireClass);  
        } 
        catch (ClassNotFoundException e) {  
            e.printStackTrace();  
        } 
        catch (IllegalArgumentException e) {  
            e.printStackTrace();  
        } 
        catch (SecurityException e) {  
            e.printStackTrace();  
        } 
        catch (IllegalAccessException e) {  
            e.printStackTrace();  
        } 
        catch (NoSuchFieldException e) {  
            e.printStackTrace();  
        }  
  
        return id;  
    }
	
	public static int getIdRes(Context context,String name){
		Resources resources = context.getResources();
		int indentify = resources.getIdentifier(context.getPackageName()+":id/"+name, null, null);
		return indentify;
		
	}

	/*public static int getLayoutRes(Context context,String name){
		
		Resources resources = context.getResources();
		int indentify = resources.getIdentifier(context.getPackageName()+":drawable/"+iconName, null, null);
		if(indentify>0){
		icon = resources.getDrawable(indentify);
		}
		
	}*/
	

}
