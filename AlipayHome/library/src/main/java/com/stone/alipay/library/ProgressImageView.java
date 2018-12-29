package com.stone.alipay.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

/**
 * Created by xmuSistone on 2018/12/25.
 */
public class ProgressImageView extends android.support.v7.widget.AppCompatImageView {

    public int progressColor;

    private MaterialProgressDrawable mProgress;
    private boolean running = false;

    public ProgressImageView(Context context) {
        this(context, null);
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.progress, 0, 0);
        progressColor = a.getColor(R.styleable.progress_color, Color.BLACK);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mProgress = new MaterialProgressDrawable(getContext(), this);
        mProgress.setBackgroundColor(Color.WHITE);
        mProgress.setColorSchemeColors(progressColor);
        mProgress.setProgressRotation(1f);
        mProgress.showArrow(false);
        mProgress.setAlpha(255);
        setImageDrawable(mProgress);
    }

    public void startProgress() {
        if (!running) {
            running = true;
            mProgress.start();
        }
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        if (null != mProgress) {
            mProgress.setColorSchemeColors(progressColor);
        }
    }

    public void stopProgress() {
        running = false;
        mProgress.stop();
    }

    public boolean isRunning() {
        return running;
    }

    public void setStartEndTrim(float start, float end) {
        mProgress.setStartEndTrim(start, end);
    }
}
