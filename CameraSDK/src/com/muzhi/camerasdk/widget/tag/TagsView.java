package com.muzhi.camerasdk.widget.tag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;


import java.util.List;

import com.muzhi.camerasdk.R;

/**
 * Created by Jazzy
 */
public class TagsView extends FrameLayout implements TagView.TagViewListener {
	public FixWidthImageView backImage;
	private FrameLayout tagsContainer;

	private List<TagInfoModel> tagInfoModels;

	private int width;
	private int height;
	
	private boolean hideTags=false;
	private Animation animIn, animOut;

	public TagsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TagsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TagsView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		animIn = AnimationUtils.loadAnimation(this.getContext(), R.anim.camerasdk_push_bottom_in);
		animIn.setDuration(100);
		animIn.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				tagsContainer.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		animOut = AnimationUtils.loadAnimation(this.getContext(), R.anim.camerasdk_push_up_out);
		animOut.setDuration(500);
		animOut.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				tagsContainer.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.camerasdk_view_tags, this, true);
		backImage = (FixWidthImageView) findViewById(R.id.backImage);
		//backImage.setBackgroundColor(Color.parseColor("#f7f7f7"));
		tagsContainer = (FrameLayout) findViewById(R.id.tagsContainer);
		backImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 单击图片隐藏标签
				if(hideTags){
					tagsContainer.setVisibility(View.VISIBLE);
					hideTags=false;
				}
				else{
					tagsContainer.setVisibility(View.GONE);
					hideTags=true;
				}
			}
		});
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthModel = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightModel = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width = 0;
		int height = 0;

		if (widthModel == MeasureSpec.EXACTLY || heightModel == MeasureSpec.EXACTLY) {
			if (widthModel == MeasureSpec.EXACTLY && heightModel == MeasureSpec.EXACTLY) {
				//取较大的长度
				if (widthSize > heightSize) {
					width = height = widthSize;
				} else {
					width = height = heightSize;
				}
			} else if (widthModel == MeasureSpec.EXACTLY) {
				width = height = widthSize;
			} else {
				width = height = heightSize;
			}
		}
		setMeasuredDimension(width, height);
		this.width = width;
		this.height = height;
		measureChildren(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
	}

	public void setImage(Bitmap bmp) {
		backImage.setImageBitmap(bmp);
	}
	public void setImageUrl(String url) {
		//AppData.bitmapUtils.display(backImage, url);
	}

	/*
	 * 设置标签信息
	 */
	public void setTagInfoModels(final List<TagInfoModel> tagInfoModels) {
		if (this.tagInfoModels == tagInfoModels) {
			return;
		}
		if (tagInfoModels == null) {
			tagsContainer.removeAllViews();
			return;
		}
		if (tagInfoModels.size() == 0) {
			tagsContainer.removeAllViews();
			return;
		}
		this.tagInfoModels = tagInfoModels;
		tagsContainer.removeAllViews();
		if (width == 0 || height == 0) {
			final ViewTreeObserver observer = getViewTreeObserver();
			observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					width = TagsView.this.getMeasuredWidth();
					height = TagsView.this.getMeasuredHeight();
					TagsView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					addTagViews();
				}
			});
		} else {
			addTagViews();
		}
	}

	public void showTags(boolean show) {
		if (show) {
			tagsContainer.clearAnimation();
			tagsContainer.startAnimation(animIn);
		} else {
			tagsContainer.clearAnimation();
			tagsContainer.startAnimation(animOut);
		}
	}

	private void addTagViews() {
		for (int i = 0; i < tagInfoModels.size(); i++) {
			TagInfoModel model = tagInfoModels.get(i);
			TagInfo tagInfo = new TagInfo();
			tagInfo.bid = 2L;
			tagInfo.bname = model.tag_name;
			tagInfo.direct = TagInfo.Direction.Left;
			tagInfo.pic_x = 50;
			tagInfo.pic_y = 50;
			tagInfo.type = TagInfo.Type.CustomPoint;
			//换算
			if (width == 0 || height == 0) {
				width = getMeasuredWidth();
				height = getMeasuredHeight();
			}
			int leftMargin = 0;
			int topMargin = 0;
			if (model.x > 1.0) {
				leftMargin = (int) (model.x / 640.0f * width);
				topMargin = (int) (model.y / 640.0f * height);
			} else {
				leftMargin = (int) (model.x * width);
				topMargin = (int) (model.y * height);
			}
			tagInfo.leftMargin = leftMargin;
			tagInfo.topMargin = topMargin;
			TagView tagView = new TagViewLeft(TagsView.this.getContext(), null);
			tagView.setData(tagInfo);
			tagView.setTagViewListener(TagsView.this);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.leftMargin = tagInfo.leftMargin;
			params.topMargin = tagInfo.topMargin;
			tagsContainer.addView(tagView, params);
		}
	}

	@Override
	public void onTagViewClicked(View view, TagInfo info) {
		//T.showShort(info.bname);
	}
}
