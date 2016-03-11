package com.muzhi.camerasdk.widget.tag;


import com.muzhi.camerasdk.R;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TagView extends RelativeLayout implements View.OnClickListener {
	public Animation blackAnimation1;
	public Animation blackAnimation2;
	public Animation whiteAnimation;

	public TextView textview;// 文字描述显示View
	public ImageView blackIcon1;// 黑色圆圈View
	public ImageView blackIcon2;// 黑色圆圈View
	protected ImageView brandIcon;// 白色圆圈View
	protected ImageView geoIcon;// 白色定位圆圈View
	public ImageView viewPointer;// 指向brandIcon或者geoIcon，根据设置的类型的不同

	public boolean isShow = false;
	private TagViewListener listener;
	private Handler handler = new Handler();

	public interface TagViewListener {
		public void onTagViewClicked(View view, TagInfo info);
	}

	public TagView(Context context) {
		this(context, null);
	}

	public TagView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.blackAnimation1 = AnimationUtils.loadAnimation(context, R.anim.camerasdk_tag_black);
		this.blackAnimation2 = AnimationUtils.loadAnimation(context, R.anim.camerasdk_tag_black);
		this.whiteAnimation = AnimationUtils.loadAnimation(context, R.anim.camerasdk_tag_white);
		this.setOnClickListener(this);
	}

	public final void clearAnim(){
		this.blackIcon1.clearAnimation();
		this.blackIcon2.clearAnimation();
		this.viewPointer.clearAnimation();
		this.isShow = false;
	}

	public final void startBlackAnimation1(final ImageView imageView) {
		blackAnimation1.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (!isShow) {
					return;
				}
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						imageView.clearAnimation();
						blackAnimation1.reset();
						startBlackAnimation2(blackIcon2);
					}
				}, 10);
			}
		});
		imageView.clearAnimation();
		imageView.startAnimation(blackAnimation1);
	}

	public final void startBlackAnimation2(final ImageView imageView) {
		blackAnimation2.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (!isShow) {
					return;
				}
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						imageView.clearAnimation();
						blackAnimation2.reset();
						startWhiteAnimation(viewPointer);
					}
				}, 10);
			}
		});
		imageView.clearAnimation();
		imageView.startAnimation(blackAnimation2);
	}

	public final void startWhiteAnimation(final ImageView imageView) {
		whiteAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (!isShow) {
					return;
				}
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						imageView.clearAnimation();
						whiteAnimation.reset();
						startBlackAnimation1(blackIcon1);
					}
				}, 10);
			}
		});
		imageView.clearAnimation();
		imageView.startAnimation(whiteAnimation);
	}

	protected final void setVisible() {
		if ((textview != null) && (taginfo != null)) {
			if (TagInfo.Type.CustomPoint != taginfo.type && TagInfo.Type.OfficalPoint != taginfo.type) {
				this.viewPointer = this.brandIcon;
				this.geoIcon.setVisibility(View.VISIBLE);
				this.brandIcon.setVisibility(View.GONE);
			} else {
				this.viewPointer = this.geoIcon;
				this.geoIcon.setVisibility(View.GONE);
				this.brandIcon.setVisibility(View.VISIBLE);
			}
			textview.setText(taginfo.bname);
			textview.setVisibility(View.VISIBLE);
		}
		clearAnim();
		show();
	}

	public void show() {
		if (this.isShow) {
			return;
		}
		this.isShow = true;
		startWhiteAnimation(viewPointer);
	}

	private TagInfo taginfo;

	public void setData(TagInfo mTagInfo) {
		this.taginfo = mTagInfo;
		setVisible();
	}

	public TagInfo getData() {
		return taginfo;
	}

	public void setTagViewListener(TagViewListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View view) {
		if (null != listener) {
			listener.onTagViewClicked(view, taginfo);
		}
	}
}