package com.stone.alipay.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * 遮罩RelativeLayout，alpha=0则交给底层View处理事件
 * Created by xmuSistone on 2019/1/8.
 */
public class MaskRelativeLayout extends RelativeLayout {

    public MaskRelativeLayout(Context context) {
        this(context, null);
    }

    public MaskRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getAlpha() == 0) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getAlpha() == 0) {
            return false;
        }
        super.onTouchEvent(event);
        return true;
    }
}
