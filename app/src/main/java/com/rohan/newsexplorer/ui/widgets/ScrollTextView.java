package com.rohan.newsexplorer.ui.widgets;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;


@SuppressLint("AppCompatCustomView")
public class ScrollTextView extends TextView {

    public float DEFAULT_SPEED = 1149.0f;

    public Scroller scroller;
    public float speed = DEFAULT_SPEED;
    public boolean continuousScrolling = true;

    public ScrollTextView(Context context) {
        super(context);
        scrollerInstance(context);
    }

    public ScrollTextView(Context context, AttributeSet attributes) {
        super(context, attributes);
        scrollerInstance(context);
    }

    public void scrollerInstance(Context context) {
        scroller = new Scroller(context, new LinearInterpolator());
        setScroller(scroller);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (scroller.isFinished()) {
            scroll();
        }
    }

    public void scroll() {
        int viewHeight = getHeight();
        int visibleHeight = viewHeight - getPaddingBottom() - getPaddingTop();
        int lineHeight = getLineHeight();

        int offset = -1 * visibleHeight;
        int distance = visibleHeight + getLineCount() * lineHeight;
        int duration = (int) (distance * speed);


        scroller.startScroll(0, offset, 0, distance, duration);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isContinuousScrolling() {
        return continuousScrolling;
    }

    public void setContinuousScrolling(boolean continuousScrolling) {
        this.continuousScrolling = continuousScrolling;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (null == scroller)
            return;
        if (scroller.isFinished() && continuousScrolling) {
            scroll();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == scroller)
            return;
        if (scroller.isFinished() && continuousScrolling) {
            scroll();
        }
    }

}