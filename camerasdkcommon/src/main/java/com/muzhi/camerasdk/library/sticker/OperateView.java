package com.muzhi.camerasdk.library.sticker;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class OperateView extends View{
	private List<ImageObject> imgLists = new ArrayList<ImageObject>();
	private Rect mCanvasLimits;
	private Bitmap bgBmp;
	private Paint paint = new Paint();
//	private Context mContext;
	private boolean isMultiAdd;// true 代表可以添加多个水印图片（或文字），false 代表只可添加单个水印图片（或文字）
	private float picScale = 0.4f;
	
	
	int rotatedImageW;
	int rotatedImageH;
	public int wW = 0, wH = 0;// 外圈扩大，用于放2个图标
	Point iconP1, iconP2;// 图标p1，p2 中心点坐标。
	Point np1;
	Point np2;
	Point np3;
	Point np4;
	int dx, dy;
	
	
	
	
	/**
	 * 设置水印图片初始化大小
	 * @param picScale
	 */
	public void setPicScale(float picScale)
	{
		this.picScale = picScale;
	}
	/**
	 * 设置是否可以添加多个图片或者文字对象
	 * 
	 * @param isMultiAdd
	 *            true 代表可以添加多个水印图片（或文字），false 代表只可添加单个水印图片（或文字）
	 */
	public void setMultiAdd(boolean isMultiAdd)
	{
		this.isMultiAdd = isMultiAdd;
	}
	public OperateView(Context context, Bitmap resizeBmp)
	{
		super(context);
//		this.mContext = context;
		bgBmp = resizeBmp;
		int width = bgBmp.getWidth();
		int height = bgBmp.getHeight();
		mCanvasLimits = new Rect(0, 0, width, height);
	}

	/**
	 * 将图片对象添加到View中
	 * 
	 * @param imgObj
	 *            图片对象
	 */
	public void addItem(ImageObject imgObj)
	{
		if (imgObj == null)
		{
			return;
		}
		if (!isMultiAdd && imgLists != null)
		{
			imgLists.clear();
		}
		imgObj.setSelected(true);
		if (!imgObj.isTextObject)
		{
			imgObj.setScale(picScale);
		}
		ImageObject tempImgObj = null;
		for (int i = 0; i < imgLists.size(); i++)
		{
			tempImgObj = imgLists.get(i);
			tempImgObj.setSelected(false);
		}
		imgLists.add(imgObj);
		invalidate();
	}
	/**
	 * 画出容器内所有的图像
	 */
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		int sc = canvas.save();
		canvas.clipRect(mCanvasLimits);
		canvas.drawBitmap(bgBmp, 0, 0, paint);
		drawImages(canvas);
		canvas.restoreToCount(sc);
		for (ImageObject ad : imgLists){
			if (ad != null && ad.isSelected()){
				ad.drawIcon(canvas);
			}
		}
	}

	public void save()
	{
		ImageObject io = getSelected();
		if (io != null)
		{
			io.setSelected(false);
		}
		invalidate();
	}

	/**
	 * 根据触控点重绘View
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getPointerCount() == 1)
		{
			handleSingleTouchManipulateEvent(event);
		} else
		{
			handleMultiTouchManipulateEvent(event);
		}
		invalidate();

		super.onTouchEvent(event);
		return true;
	}

	private boolean mMovedSinceDown = false;
	private boolean mResizeAndRotateSinceDown = false;
	private float mStartDistance = 0.0f;
	private float mStartScale = 0.0f;
	private float mStartRot = 0.0f;
	private float mPrevRot = 0.0f;
	static public final double ROTATION_STEP = 2.0;
	static public final double ZOOM_STEP = 0.01;
	static public final float CANVAS_SCALE_MIN = 0.25f;
	static public final float CANVAS_SCALE_MAX = 3.0f;
	private Point mPreviousPos = new Point(0, 0); // single touch events
	float diff;
	float rot;

	/**
	 * 多点触控操作
	 * 
	 * @param event
	 */
	private void handleMultiTouchManipulateEvent(MotionEvent event)
	{
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_POINTER_UP :
				break;
			case MotionEvent.ACTION_POINTER_DOWN :
				float x1 = event.getX(0);
				float x2 = event.getX(1);
				float y1 = event.getY(0);
				float y2 = event.getY(1);
				float delX = (x2 - x1);
				float delY = (y2 - y1);
				diff = (float) Math.sqrt((delX * delX + delY * delY));
				mStartDistance = diff;
				// float q = (delX / delY);
				mPrevRot = (float) Math.toDegrees(Math.atan2(delX, delY));
				for (ImageObject io : imgLists)
				{
					if (io.isSelected())
					{
						mStartScale = io.getScale();
						mStartRot = io.getRotation();
						break;
					}
				}
				break;

			case MotionEvent.ACTION_MOVE :
				x1 = event.getX(0);
				x2 = event.getX(1);
				y1 = event.getY(0);
				y2 = event.getY(1);
				delX = (x2 - x1);
				delY = (y2 - y1);
				diff = (float) Math.sqrt((delX * delX + delY * delY));
				float scale = diff / mStartDistance;
				float newscale = mStartScale * scale;
				rot = (float) Math.toDegrees(Math.atan2(delX, delY));
				float rotdiff = mPrevRot - rot;
				for (ImageObject io : imgLists)
				{
					if (io.isSelected() && newscale < 10.0f && newscale > 0.1f)
					{
						float newrot = Math.round((mStartRot + rotdiff) / 1.0f);
						if (Math.abs((newscale - io.getScale()) * ROTATION_STEP) > Math
								.abs(newrot - io.getRotation()))
						{
							io.setScale(newscale);
						} else
						{
							io.setRotation(newrot % 360);
						}
						break;
					}
				}

				break;
		}
	}
	/**
	 * 获取选中的对象ImageObject
	 * 
	 * @return
	 */
	private ImageObject getSelected()
	{
		for (ImageObject ibj : imgLists)
		{
			if (ibj.isSelected())
			{
				return ibj;
			}
		}
		return null;
	}

	private long selectTime = 0;
	/**
	 * 单点触控操作
	 * 
	 * @param event
	 */
	private void handleSingleTouchManipulateEvent(MotionEvent event)
	{

		long currentTime = 0;
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN :

				mMovedSinceDown = false;
				mResizeAndRotateSinceDown = false;
				int selectedId = -1;

				for (int i = imgLists.size() - 1; i >= 0; --i)
				{
					ImageObject io = imgLists.get(i);
					if (io.contains(event.getX(), event.getY())
							|| io.pointOnCorner(event.getX(), event.getY(),
									OperateConstants.RIGHTBOTTOM)
							|| io.pointOnCorner(event.getX(), event.getY(),
									OperateConstants.LEFTTOP))
					{
						io.setSelected(true);
						imgLists.remove(i);
						imgLists.add(io);
						selectedId = imgLists.size() - 1;
						currentTime = System.currentTimeMillis();
						if (currentTime - selectTime < 300)
						{
							if (myListener != null)
							{
								if (getSelected().isTextObject())
								{
									myListener
											.onClick((TextObject) getSelected());
								}
							}
						}
						selectTime = currentTime;
						break;
					}
				}
				if (selectedId < 0)
				{
					for (int i = imgLists.size() - 1; i >= 0; --i)
					{
						ImageObject io = imgLists.get(i);
						if (io.contains(event.getX(), event.getY())
								|| io.pointOnCorner(event.getX(), event.getY(),
										OperateConstants.RIGHTBOTTOM)
								|| io.pointOnCorner(event.getX(), event.getY(),
										OperateConstants.LEFTTOP))
						{
							io.setSelected(true);
							imgLists.remove(i);
							imgLists.add(io);
							selectedId = imgLists.size() - 1;
							break;
						}
					}
				}
				for (int i = 0; i < imgLists.size(); ++i)
				{
					ImageObject io = imgLists.get(i);
					if (i != selectedId)
					{
						io.setSelected(false);
					}
				}

				ImageObject io = getSelected();
				if (io != null)
				{
					if (io.pointOnCorner(event.getX(), event.getY(),
							OperateConstants.LEFTTOP))
					{
						imgLists.remove(io);
					} else if (io.pointOnCorner(event.getX(), event.getY(),
							OperateConstants.RIGHTBOTTOM))
					{
						mResizeAndRotateSinceDown = true;
						float x = event.getX();
						float y = event.getY();
						float delX = x - io.getPoint().x;
						float delY = y - io.getPoint().y;
						diff = (float) Math.sqrt((delX * delX + delY * delY));
						mStartDistance = diff;
						mPrevRot = (float) Math.toDegrees(Math
								.atan2(delX, delY));
						mStartScale = io.getScale();
						mStartRot = io.getRotation();
					} else if (io.contains(event.getX(), event.getY()))
					{
						mMovedSinceDown = true;
						mPreviousPos.x = (int) event.getX();
						mPreviousPos.y = (int) event.getY();
					}
				}
				break;

			case MotionEvent.ACTION_UP :

				mMovedSinceDown = false;
				mResizeAndRotateSinceDown = false;

				break;

			case MotionEvent.ACTION_MOVE :
				// Log.i("jarlen"," 移动了");
				// 移动
				if (mMovedSinceDown)
				{
					int curX = (int) event.getX();
					int curY = (int) event.getY();
					int diffX = curX - mPreviousPos.x;
					int diffY = curY - mPreviousPos.y;
					mPreviousPos.x = curX;
					mPreviousPos.y = curY;
					io = getSelected();
					Point p = io.getPosition();
					int x = p.x + diffX;
					int y = p.y + diffY;
					if (p.x + diffX >= mCanvasLimits.left
							&& p.x + diffX <= mCanvasLimits.right
							&& p.y + diffY >= mCanvasLimits.top
							&& p.y + diffY <= mCanvasLimits.bottom)
						io.moveBy((int) (diffX), (int) (diffY));
				}
				// 旋转和缩放
				if (mResizeAndRotateSinceDown)
				{
					io = getSelected();
					float x = event.getX();
					float y = event.getY();
					float delX = x - io.getPoint().x;
					float delY = y - io.getPoint().y;
					diff = (float) Math.sqrt((delX * delX + delY * delY));
					float scale = diff / mStartDistance;
					float newscale = mStartScale * scale;
					rot = (float) Math.toDegrees(Math.atan2(delX, delY));
					float rotdiff = mPrevRot - rot;
					if (newscale < 10.0f && newscale > 0.1f)
					{
						float newrot = Math.round((mStartRot + rotdiff) / 1.0f);
						if (Math.abs((newscale - io.getScale()) * ROTATION_STEP) > Math
								.abs(newrot - io.getRotation()))
						{
							io.setScale(newscale);
						} else
						{
							io.setRotation(newrot % 360);
						}
					}
				}
				break;
		}

		cancelLongPress();

	}
	/**
	 * 循环画图像
	 * 
	 * @param canvas
	 */
	private void drawImages(Canvas canvas)
	{
		for (ImageObject ad : imgLists)
		{
			if (ad != null)
			{
				ad.draw(canvas);
			}
		}
	}

	/**
	 * 向外部提供双击监听事件（双击弹出自定义对话框编辑文字）
	 */
	MyListener myListener;

	public void setOnListener(MyListener myListener)
	{
		this.myListener = myListener;
	}

	public interface MyListener
	{
		public void onClick(TextObject tObject);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	// 画包围边框线
	
	public void drawRectR(int l, int t, int r, int b, float jd) {

		Point p1 = new Point(l, t);
		Point p2 = new Point(r, t);
		Point p3 = new Point(r, b);
		Point p4 = new Point(l, b);
		Point tp = new Point((l + r) / 2, (t + b) / 2);
		np1 = roationPoint(tp, p1, jd);
		np2 = roationPoint(tp, p2, jd);
		np3 = roationPoint(tp, p3, jd);
		np4 = roationPoint(tp, p4, jd);
		int w = 0;
		int h = 0;
		int maxn = 0;
		int mixn = 0;
		maxn = np1.x;
		mixn = np1.x;
		if (np2.x > maxn) {
			maxn = np2.x;
		}
		if (np3.x > maxn) {
			maxn = np3.x;
		}
		if (np4.x > maxn) {
			maxn = np4.x;
		}

		if (np2.x < mixn) {
			mixn = np2.x;
		}
		if (np3.x < mixn) {
			mixn = np3.x;
		}
		if (np4.x < mixn) {
			mixn = np4.x;
		}
		w = maxn - mixn;

		maxn = np1.y;
		mixn = np1.y;
		if (np2.y > maxn) {
			maxn = np2.y;
		}
		if (np3.y > maxn) {
			maxn = np3.y;
		}
		if (np4.y > maxn) {
			maxn = np4.y;
		}

		if (np2.y < mixn) {
			mixn = np2.y;
		}
		if (np3.y < mixn) {
			mixn = np3.y;
		}
		if (np4.y < mixn) {
			mixn = np4.y;
		}

		h = maxn - mixn;
		// +中心点位置计算。
		Point npc = intersects(np4, np2, np1, np3);
		// System.out.println("中心点坐标："+npc.x+"|"+npc.y);
		dx = w / 2 - npc.x;
		dy = h / 2 - npc.y;
		np1.x = np1.x + dx + wW;
		np2.x = np2.x + dx + wW;
		np3.x = np3.x + dx + wW;
		np4.x = np4.x + dx + wW;

		np1.y = np1.y + dy + wH;
		np2.y = np2.y + dy + wH;
		np3.y = np3.y + dy + wH;
		np4.y = np4.y + dy + wH;
		rotatedImageW = w;
		rotatedImageH = h;
		iconP1 = np1;
		iconP2 = np3;
	}
	// 2直线交点
	public Point intersects(Point sp3, Point sp4, Point sp1, Point sp2) {
		Point localPoint = new Point(0, 0);
		double num = (sp4.y - sp3.y) * (sp3.x - sp1.x) - (sp4.x - sp3.x)
				* (sp3.y - sp1.y);
		double denom = (sp4.y - sp3.y) * (sp2.x - sp1.x) - (sp4.x - sp3.x)
				* (sp2.y - sp1.y);
		localPoint.x = (int) (sp1.x + (sp2.x - sp1.x) * num / denom);
		localPoint.y = (int) (sp1.y + (sp2.y - sp1.y) * num / denom);
		return localPoint;
	}
	
	/**
	 * 
	 * @param target
	 *            围绕旋转的目标点
	 * @param source
	 *            要旋转的点
	 * @param degree
	 *            要旋转的角度 360
	 * @return
	 */
	public static Point roationPoint(Point target, Point source, float degree) {
		source.x = source.x - target.x;
		source.y = source.y - target.y;
		double alpha = 0;
		double beta = 0;
		Point result = new Point();
		double dis = Math.sqrt(source.x * source.x + source.y * source.y);// 2点间，距离
		if (source.x == 0 && source.y == 0) {
			return target;
			// 第一象限
		} else if (source.x >= 0 && source.y >= 0) {
			// 计算与x正方向的夹角
			alpha = Math.asin(source.y / dis);
			// 第二象限
		} else if (source.x < 0 && source.y >= 0) {
			// 计算与x正方向的夹角
			alpha = Math.asin(Math.abs(source.x) / dis);
			alpha = alpha + Math.PI / 2;
			// 第三象限
		} else if (source.x < 0 && source.y < 0) {
			// 计算与x正方向的夹角
			alpha = Math.asin(Math.abs(source.y) / dis);
			alpha = alpha + Math.PI;
		} else if (source.x >= 0 && source.y < 0) {
			// 计算与x正方向的夹角
			alpha = Math.asin(source.x / dis);
			alpha = alpha + Math.PI * 3 / 2;

		}
		// 弧度换算成角度
		alpha = radianToDegree(alpha);
		beta = alpha + degree;
		// 角度转弧度
		beta = degreeToRadian(beta);
		result.x = (int) Math.round(dis * Math.cos(beta));
		result.y = (int) Math.round(dis * Math.sin(beta));
		result.x += target.x;
		result.y += target.y;

		return result;
	}
	
	public static double radianToDegree(double radian) {
		return radian * 180 / Math.PI;
	}

	public static double degreeToRadian(double degree) {
		return degree * Math.PI / 180;
	}
	
	
	
	
	
	
	
	
	
	
	
}
