package com.muzhi.camerasdk.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * viewpager
 * jazzy
 */
public class CustomViewPager extends ViewPager {

	private boolean enabled=true;//false;//默认不可滑动


    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        //this.enabled = true;
    }

    //触摸没有反应就可以了
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
        	 try {  
                 return super.onTouchEvent(event);  
             } catch (IllegalArgumentException ex) {  
                 ex.printStackTrace();  
             }  
        }
  
        return false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
        	try {  
                return super.onInterceptTouchEvent(event);  
            } catch (IllegalArgumentException ex) {  
                ex.printStackTrace();  
            } 
        }
        
        return false;
    }
 
    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
