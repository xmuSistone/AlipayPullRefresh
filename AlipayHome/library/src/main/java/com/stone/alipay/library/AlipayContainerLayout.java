package com.stone.alipay.library;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by xmuSistone on 2018/12/25.
 */

public class AlipayContainerLayout extends FrameLayout {

    private AlipayScrollView scrollView;
    private View topLayout, progressRootLayout;

    private float downX, downY;
    private int mTouchSlop;

    private Mode mode;
    private View touchingView;
    private Decorator decorator;
    private ProgressImageView progressImageView;

    public AlipayContainerLayout(@NonNull Context context) {
        this(context, null);
    }

    public AlipayContainerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlipayContainerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();

        // 初始化scrollView待用，并未addView
        this.scrollView = new AlipayScrollView(context, attrs);
        this.scrollView.setFocusable(true);
        this.scrollView.setFocusableInTouchMode(true);
        this.scrollView.setVerticalScrollBarEnabled(false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (null == decorator) {
            return super.onInterceptTouchEvent(ev);
        }

        // 1. 默认不作任何拦截
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (ev.getY() < topLayout.getBottom()) {
                // 手指按下，需要把时间传递给scrollView
                touchingView = topLayout;
                scrollView.onInterceptTouchEvent(ev);
            } else {
                touchingView = scrollView;
            }

            // 保存中间变量
            downX = ev.getX();
            downY = ev.getY();
            this.mode = Mode.IDLE;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            // 2. 拖动
            if (this.mode == Mode.IDLE) {
                // 判定一下是水平滑动还是垂直滑动？
                float distanceX = Math.abs(ev.getX() - downX);
                float distanceY = Math.abs(ev.getY() - downY);
                if (distanceX > distanceY && distanceX > mTouchSlop) {
                    // 水平滑动，由子View内部去消费
                    this.mode = Mode.HORIZONTAL;
                    if (touchingView == topLayout) {
                        scrollView.updateProcessY(ev.getRawY());
                        return true;
                    }
                } else if (distanceY > distanceX && distanceY > mTouchSlop) {
                    // 垂直滑动
                    this.mode = Mode.VERTICAL;
                    // 如果拖拽的是topLayout，则touch事件一律交给AlipayFrameLayout.onToucvhEvent，然后把事件转发给ScrollView
                    if (touchingView == topLayout) {
                        scrollView.updateProcessY(ev.getRawY());
                        return true;
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // ScrollView用margin下拉刷新时，会触发整体的View树重绘，此处需要同步topLayout位移
        if (null != topLayout) {
            int topLayoutTop = -scrollView.getScrollY();
            if (topLayoutTop < -topLayout.getMeasuredHeight()) {
                topLayoutTop = -topLayout.getMeasuredHeight();
            }
            topLayout.offsetTopAndBottom(topLayoutTop - topLayout.getTop());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null == decorator) {
            return super.onTouchEvent(event);
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // 拖动
            if (this.mode == Mode.IDLE) {
                // 判定一下是水平滑动还是垂直滑动？
                float distanceX = Math.abs(event.getX() - downX);
                float distanceY = Math.abs(event.getY() - downY);
                if (distanceX > distanceY && distanceX > mTouchSlop) {
                    this.mode = Mode.HORIZONTAL;
                } else if (distanceY > distanceX && distanceY > mTouchSlop) {
                    this.mode = Mode.VERTICAL;
                }
            }
        }

        // 自己把它消费掉，同时Touch事件转发给scrollView
        scrollView.onTouchEvent(event);
        return true;
    }

    public View getTopLayout() {
        return topLayout;
    }

    public ProgressImageView getProgressImageView() {
        return progressImageView;
    }

    public View getTouchingView() {
        return touchingView;
    }

    public AlipayScrollView getScrollView() {
        return scrollView;
    }

    /**
     * 绑定装饰器
     * @param decorator
     */
    public void setDecorator(Decorator decorator) {
        if (this.decorator != null) {
            throw new RuntimeException("不能重复绑定decorator");
        }
        this.decorator = decorator;

        // 1. 添加scrollView
        LinearLayout rootLayout = new LinearLayout(getContext());
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        this.addView(scrollView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        scrollView.addView(rootLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        View contentView = decorator.getContentView();
        rootLayout.addView(contentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // 2. 添加topLayout
        topLayout = decorator.getTopLayout();
        topLayout.setClickable(true);
        this.addView(topLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        topLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int topLayoutHeight = topLayout.getMeasuredHeight();
                if (topLayoutHeight > 0 && topLayoutHeight != progressRootLayout.getMeasuredHeight()) {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) progressRootLayout.getLayoutParams();
                    lp.height = topLayoutHeight;
                    progressRootLayout.setLayoutParams(lp);
                }
            }
        });

        // 3. 添加refresh progress进度条
        LayoutInflater inflater = LayoutInflater.from(getContext());
        progressRootLayout = inflater.inflate(R.layout.home_progress, null);
        progressImageView = progressRootLayout.findViewById(R.id.progress_imageview);
        rootLayout.addView(progressRootLayout, 0);

        View progressContainerFrameLayout = progressRootLayout.findViewById(R.id.progress_container);
        ViewGroup.LayoutParams lp = progressContainerFrameLayout.getLayoutParams();
        lp.height = scrollView.getProgressHeight();
        progressContainerFrameLayout.setLayoutParams(lp);
    }

    public interface Decorator {
        /**
         * 获取垂直滑动的View内容
         */
        View getContentView();

        /**
         * 获取progressBar顶部的View部分
         */
        View getTopLayout();
    }

    enum Mode {IDLE, HORIZONTAL, VERTICAL}
}
