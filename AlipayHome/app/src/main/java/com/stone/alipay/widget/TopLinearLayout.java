package com.stone.alipay.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by xmuSistone on 2018/12/29.
 */

public class TopLinearLayout extends LinearLayout {

    private ScrollView scrollView;
    private View blueLayout;

    public TopLinearLayout(Context context) {
        this(context, null);
    }

    public TopLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        syncScrollParallax();
    }

    public void syncScrollParallax() {
        if (null != scrollView) {
            int destPosition = scrollView.getScrollY() / 2;
            if (scrollView.getScrollY() > blueLayout.getMeasuredHeight()) {
                destPosition = blueLayout.getMeasuredHeight() / 2;
            }
            blueLayout.offsetTopAndBottom(destPosition - blueLayout.getTop());
        }
    }

    public void bindParallax(ScrollView scrollView, View blueLayout) {
        this.scrollView = scrollView;
        this.blueLayout = blueLayout;
    }
}
