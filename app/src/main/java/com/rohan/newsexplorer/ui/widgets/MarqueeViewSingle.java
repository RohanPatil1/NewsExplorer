package com.rohan.newsexplorer.ui.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Combined Marquee view with 2 elements.
 *
 * @author Sebastian Roth
 */
public class MarqueeViewSingle extends LinearLayout {
    private TextView mTextField1;

    private ScrollView mScrollView1;

    public static final int TEXTVIEW_VIRTUAL_WIDTH = 2000;

    private Animation mMoveText1TextOut = null;
    private Animation mMoveText1TextIn = null;

    private Paint mPaint;

    private float mText1TextWidth;

    private boolean mIsText1MarqueeNeeded = false;

    private static final String TAG = "MarqueeViewSingle";

    private float mText1Difference;

    /**
     * Control the speed. The lower this value, the faster it will scroll.
     */
    public static final int MS_PER_PX = 30;

    /**
     * Control the pause between the animations. Also, after starting this activity.
     */
    public static final int PAUSE_BETWEEN_ANIMATIONS = 0;
    private boolean mCancelled = false;
    private int mWidth;
    private Runnable mAnimation1StartRunnable;
    private static final boolean DEBUG = false;

    @SuppressWarnings({"UnusedDeclaration"})
    public MarqueeViewSingle(Context context) {
        super(context);
        init(context);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public MarqueeViewSingle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        initView(context);

        // init helper
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mWidth = getMeasuredWidth();

        // Calculate
        prepare();

        // Setup
        setupText1Marquee();

    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);

        mTextField1.setOnClickListener(l);
    }

    // Method to finally start the marquee.
    public void startMarquee() {
        prepare();
        prepareTextFields();

        // Start
        if (mIsText1MarqueeNeeded) {
            startTextField1Animation();
        }

        mCancelled = false;
    }

    private void startTextField1Animation() {
        mTextField1.startAnimation(mMoveText1TextOut);

//        mAnimation1StartRunnable = new Runnable() {
//            public void run() {
//            }
//        };
//        postDelayed(mAnimation1StartRunnable, PAUSE_BETWEEN_ANIMATIONS);
    }

    public void reset() {
        Log.d(TAG, "Resetting animation.");

        mCancelled = true;

        if (mAnimation1StartRunnable != null) {
            removeCallbacks(mAnimation1StartRunnable);
        }

        mTextField1.clearAnimation();

        prepareTextFields();

        mMoveText1TextOut.reset();
        mMoveText1TextIn.reset();

        mScrollView1.removeView(mTextField1);
        mScrollView1.addView(mTextField1);

        mTextField1.setEllipsize(TextUtils.TruncateAt.END);

        invalidate();
    }

    public void prepareTextFields() {
        mTextField1.setEllipsize(TextUtils.TruncateAt.END);
        cutTextView(mTextField1);
    }

    private void setupText1Marquee() {
        final int duration = (int) (mText1Difference * MS_PER_PX);

        mMoveText1TextOut = new TranslateAnimation(0, -mText1Difference, 0, 0);
        mMoveText1TextOut.setDuration(duration);
        mMoveText1TextOut.setInterpolator(new LinearInterpolator());
        mMoveText1TextOut.setFillAfter(true);

        mMoveText1TextIn = new TranslateAnimation(-mText1Difference, 0, 0, 0);
        mMoveText1TextIn.setDuration(duration);
        mMoveText1TextIn.setStartOffset(PAUSE_BETWEEN_ANIMATIONS);
        mMoveText1TextIn.setInterpolator(new LinearInterpolator());
        mMoveText1TextIn.setFillAfter(true);

        mMoveText1TextOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
//                mMoveText1TextOutPlaying = true;
                expandTextView(mTextField1);
            }

            public void onAnimationEnd(Animation animation) {
//                mMoveText1TextOutPlaying = false;

                if (mCancelled) {
                    return;
                }

                mTextField1.startAnimation(mMoveText1TextIn);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });

        mMoveText1TextIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {

                cutTextView(mTextField1);

                if (mCancelled) {
                    return;
                }
                startTextField1Animation();
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void prepare() {
        // Remember current state
        final float diff1 = mText1Difference;

        // Measure
        mPaint.setTextSize(mTextField1.getTextSize());
        mPaint.setTypeface(mTextField1.getTypeface());
        mText1TextWidth = mPaint.measureText(mTextField1.getText().toString());

        // See how much functions are needed at all
        mIsText1MarqueeNeeded = mText1TextWidth > mWidth;

        mText1Difference = Math.abs((mText1TextWidth - mWidth)) + 5;

        if (DEBUG) {
            Log.d(TAG, "mText1TextWidth: " + mText1TextWidth);
            Log.d(TAG, "getMeasuredWidth: " + mWidth);

            Log.d(TAG, "mIsText1MarqueeNeeded: " + mIsText1MarqueeNeeded);

            Log.d(TAG, "mText1Difference: " + mText1Difference);
        }

        if (diff1 != mText1Difference) {
            setupText1Marquee();
        }
    }

    private void initView(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, Gravity.LEFT));
        setPadding(5, 0, 0, 0);

        // Scroll View 1
        LayoutParams sv1lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        sv1lp.gravity = Gravity.CENTER_HORIZONTAL;
        mScrollView1 = new ScrollView(context);

        // Scroll View 1 - Text Field
        mTextField1 = new TextView(context);
        mTextField1.setSingleLine(true);
        mTextField1.setTextColor(Color.WHITE);
        mTextField1.setEllipsize(TextUtils.TruncateAt.END);
        mTextField1.setTypeface(null, Typeface.BOLD);
        mScrollView1.addView(mTextField1, new ScrollView.LayoutParams(TEXTVIEW_VIRTUAL_WIDTH, LayoutParams.WRAP_CONTENT));

        addView(mScrollView1, sv1lp);
    }

    public void setText1(String text) {
        mTextField1.setText(text);
    }

    public void setTextSize(float value) {
        mTextField1.setTextSize(value);
    }

    private void expandTextView(TextView textView) {
        ViewGroup.LayoutParams lp = textView.getLayoutParams();
        lp.width = 2000;
        textView.setLayoutParams(lp);
    }

    private void cutTextView(TextView textView) {
        if (textView.getWidth() != mWidth) {
            ViewGroup.LayoutParams lp = textView.getLayoutParams();
            lp.width = mWidth;
            textView.setLayoutParams(lp);
        }
    }

}