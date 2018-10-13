package com.muzhi.camerasdk.widget.tag;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.TextView;

public class EmojiconTextView extends TextView {
	private int TextSize;

	public EmojiconTextView(Context paramContext) {
		super(paramContext);
		a(null);
	}

	public EmojiconTextView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		a(paramAttributeSet);
	}

	public EmojiconTextView(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		a(paramAttributeSet);
	}

	private void a(AttributeSet paramAttributeSet) {
		if (paramAttributeSet == null){
			this.TextSize = (int) getTextSize();
		}else{
//			TypedArray localTypedArray = getContext().obtainStyledAttributes(paramAttributeSet, Emojicon);
//			this.TextSize = (int) localTypedArray.getDimension(0, getTextSize());
//			localTypedArray.recycle();
		}
		setText(getText());
	}

	public void setEmojiconSize(int paramInt) {
		this.TextSize = paramInt;
	}

	public void setText(CharSequence mCharSequence, BufferType mBufferType) {
		SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(mCharSequence);
//		uv.a(getContext(), localSpannableStringBuilder, this.TextSize);
		super.setText(localSpannableStringBuilder, mBufferType);
	}
	
//	public static final int[] Emojicon = { 2130772074 };
//	public static final int Emojicon_emojiconSize;
}