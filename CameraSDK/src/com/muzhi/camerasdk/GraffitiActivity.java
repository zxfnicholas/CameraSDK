package com.muzhi.camerasdk;

import com.muzhi.camerasdk.library.scrawl.DrawAttribute;
import com.muzhi.camerasdk.library.scrawl.DrawingBoardView;
import com.muzhi.camerasdk.library.scrawl.ScrawlTools;
import com.muzhi.camerasdk.library.utils.MResource;
import com.muzhi.camerasdk.model.Constants;
import com.muzhi.camerasdk.view.holocolorpicker.ColorPicker;
import com.muzhi.camerasdk.view.holocolorpicker.OpacityBar;
import com.muzhi.camerasdk.view.holocolorpicker.SVBar;
import com.muzhi.camerasdk.view.holocolorpicker.ColorPicker.OnColorChangedListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


/**
 * 涂鸦
 *
 */
public class GraffitiActivity extends BaseActivity implements OnSeekBarChangeListener{
  
    private TextView btn_done;
    private DrawingBoardView drawView;
    private Bitmap sourceMap;
    
    private ScrawlTools casualWaterUtil = null;
    
    private LinearLayout brush_layout;
    private int seekBar1,seekBar2;
    private SeekBar seekbar_brush,seekbar_eraser;
    private RadioButton btn_brush,btn_eraser;
    private View brush_view_color;
    
    private int brush_progress=10;
    private int eraser_progress=10;
    private int brush_color=Color.parseColor("#5d5d5d");
    
    private PopupWindow mpopupWindow;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camerasdk_activity_graffiti);
        showLeftIcon();
        setActionBarTitle("涂鸦");
        
        sourceMap=Constants.bitmap;
        
        findViews();
        
    }

    private void findViews() {
        btn_done=(TextView)findViewById(R.id.camerasdk_title_txv_right_text);
        btn_done.setVisibility(View.VISIBLE);
        btn_done.setText("确定");
        
        drawView = (DrawingBoardView)findViewById(R.id.drawView);
        
        seekBar1=MResource.getIdRes(mContext, "seekBar1");
        seekBar2=MResource.getIdRes(mContext, "seekBar2");
        
        seekbar_brush=(SeekBar)findViewById(seekBar1);
        seekbar_eraser=(SeekBar)findViewById(seekBar2);
        
        brush_layout=(LinearLayout)findViewById(MResource.getIdRes(mContext, "brush_layout"));
        btn_brush=(RadioButton)findViewById(MResource.getIdRes(mContext, "button_brush"));
        btn_eraser=(RadioButton)findViewById(MResource.getIdRes(mContext, "button_eraser"));
        
        brush_view_color=(View)findViewById(MResource.getIdRes(mContext, "brush_view_color"));
        brush_view_color.setBackgroundColor(brush_color);
        
        casualWaterUtil = new ScrawlTools(this, drawView, sourceMap);	
        
        brush_progress=10-seekbar_brush.getProgress();
        eraser_progress=10-seekbar_eraser.getProgress();
        
		initBrush();
		
        initEvent();
    }
	
    
	private void initEvent() {
		
		seekbar_brush.setOnSeekBarChangeListener(this);
		seekbar_eraser.setOnSeekBarChangeListener(this);
		
		
		btn_done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				done();
			}
		});
		btn_brush.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initBrush();
				brush_layout.setVisibility(View.VISIBLE);
				seekbar_eraser.setVisibility(View.GONE);
			}
		});
		btn_eraser.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initEraser();
				brush_layout.setVisibility(View.GONE);
				seekbar_eraser.setVisibility(View.VISIBLE);
			}
		});
		brush_view_color.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 颜色选择器
				showPopupColor(v);
			}
		});
	}
    
	
	//拖动值监听
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch){
		int type = 0;
		int vid=seekBar.getId();		
		if(vid==seekBar1){
			//画笔
			brush_progress=10-progress;
			initBrush();
		}
		else if(vid==seekBar2){
			//橡皮擦
			eraser_progress=10-progress;
			initEraser();
		}
		
		
	}
		
	public void onStartTrackingTouch(SeekBar seekBar){}
	//SeekBar 停止拖动
	public void onStopTrackingTouch(SeekBar seekBar){}
	
	
	
	//定义画笔
	private void initBrush(){	
		Options option = new Options();
		option.inSampleSize = brush_progress;
		Bitmap paintBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.camerasdk_brush, option);
		casualWaterUtil.creatDrawPainter(DrawAttribute.DrawStatus.PEN_WATER, paintBitmap,brush_color);
    }
	
	//定义橡皮擦
	private void initEraser(){
		Options option = new Options();
		option.inSampleSize = eraser_progress;
		Bitmap paintBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.camerasdk_eraser,option);
		casualWaterUtil.creatDrawPainter(DrawAttribute.DrawStatus.PEN_ERASER, paintBitmap,0xffadb8bd);
		
    }
	
	
	 /**
     * 创建颜色选择器弹出框
     */
    private void showPopupColor(View v) {
		
    	View view=getLayoutInflater().inflate(R.layout.camerasdk_popup_colorpick, null); 
		
    	ColorPicker picker = (ColorPicker)view.findViewById(R.id.picker);
		SVBar svBar = (SVBar)view.findViewById(R.id.svbar);
		OpacityBar opacityBar = (OpacityBar)view.findViewById(R.id.opacitybar);

		picker.setColor(brush_color);
		
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		
		picker.setOnColorChangedListener(new OnColorChangedListener() {
			
			@Override
			public void onColorChanged(int color) {
				// TODO Auto-generated method stub
				getPickColor(color);
			}
		});
		  	
		if(mpopupWindow==null){
			mpopupWindow=new PopupWindow(mContext);
	    	mpopupWindow.setWidth(LayoutParams.WRAP_CONTENT);
			mpopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			mpopupWindow.setFocusable(true);
			mpopupWindow.setOutsideTouchable(true);
		}
		
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mpopupWindow.dismiss();
			}
		});
		
		mpopupWindow.setContentView(view);		
		mpopupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
		mpopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
	}
	
    
    
    private void getPickColor(int color){
    	brush_color=color;
    	initBrush();
    	brush_view_color.setBackgroundColor(color);
    }
    
    private void done(){
    	
    	Constants.bitmap = casualWaterUtil.getBitmap();
    	setResult(Constants.RequestCode_Croper);
        finish();
    }
    
    
    
    
    
    
    
    
}
