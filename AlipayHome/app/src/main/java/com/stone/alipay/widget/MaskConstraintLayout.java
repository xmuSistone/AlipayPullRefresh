package com.stone.alipay.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 遮罩ConstraintLayout，alpha=0则交给底层View处理事件
 * Created by xmuSistone on 2019/1/8.
 */
public class MaskConstraintLayout extends ConstraintLayout {

    public MaskConstraintLayout(Context context) {
        this(context, null);
    }

    public MaskConstraintLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskConstraintLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
