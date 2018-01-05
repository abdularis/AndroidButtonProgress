package com.github.abdularis.buttonprogress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdularis on 02/01/18.
 *
 */

public class DownloadButtonProgress extends View implements View.OnClickListener {

    public static final int STATE_IDLE = 1;
    public static final int STATE_INDETERMINATE = 2;
    public static final int STATE_DETERMINATE = 3;
    public static final int STATE_FINISHED = 4;

    private static final int BASE_START_ANGLE = -90;
    private static final int DEF_BG_COLOR = 0xB4000000;
    private static final boolean DEF_CANCELABLE = true;
    private static final int DEF_DETERMINATE_COLOR = Color.GREEN;
    private static final int DEF_INDETERMINATE_COLOR = Color.WHITE;
    private static final int DEF_PROGRESS_WIDTH = 8;
    private static final int DEF_PROGRESS_MARGIN = 5;

    private Drawable mIdleIcon;
    private Drawable mCancelIcon;
    private Drawable mFinishIcon;

    private boolean mCancelable;

    private int mIdleIconWidth;
    private int mIdleIconHeight;
    private int mCancelIconWidth;
    private int mCancelIconHeight;
    private int mFinishIconWidth;
    private int mFinishIconHeight;

    private int mCurrState;
    private int mMaxProgress;
    private int mCurrProgress;

    private Paint mBgPaint;
    private RectF mBgRect;

    private int mIdleBgColor;
    private int mFinishBgColor;
    private int mIndeterminateBgColor;
    private int mDeterminateBgColor;

    private Drawable mIdleBgDrawable;
    private Drawable mFinishBgDrawable;
    private Drawable mIndeterminateBgDrawable;
    private Drawable mDeterminateBgDrawable;

    private ValueAnimator mIndeterminateAnimator;
    private int mCurrIndeterminateBarPos;

    private int mProgressDeterminateColor;
    private int mProgressIndeterminateColor;
    private int mProgressMargin;
    private Paint mProgressPaint;
    private RectF mProgressRect;

    private List<OnClickListener> mClickListeners;

    public DownloadButtonProgress(Context context) {
        this(context, null);
    }

    public DownloadButtonProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.setOnClickListener(this);

        initIndeterminateAnimator();

        mClickListeners = new ArrayList<>();

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgRect = new RectF();

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setDither(true);
        mProgressPaint.setStrokeJoin(Paint.Join.ROUND);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setPathEffect(new CornerPathEffect(50f));

        mProgressRect = new RectF();

        Resources res = context.getResources();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DownloadButtonProgress, 0, 0);

            initBackgroundDrawableFromAttribs(res, a);

            mCurrState = a.getInteger(R.styleable.DownloadButtonProgress_state, STATE_IDLE);
            mCancelable = a.getBoolean(R.styleable.DownloadButtonProgress_cancelable, DEF_CANCELABLE);
            mProgressDeterminateColor = a.getColor(R.styleable.DownloadButtonProgress_progressDeterminateColor, DEF_DETERMINATE_COLOR);
            mProgressIndeterminateColor = a.getColor(R.styleable.DownloadButtonProgress_progressIndeterminateColor, DEF_INDETERMINATE_COLOR);
            mProgressPaint.setStrokeWidth(
                    a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_progressWidth, DEF_PROGRESS_WIDTH)
            );
            mProgressMargin = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_progressMargin, DEF_PROGRESS_MARGIN);
            mCurrProgress = a.getInteger(R.styleable.DownloadButtonProgress_progress, 0);
            mMaxProgress = a.getInteger(R.styleable.DownloadButtonProgress_maxProgress, 100);

            int icIdleDrawableId = a.getResourceId(R.styleable.DownloadButtonProgress_idleIconDrawable, R.drawable.ic_default_download);
            mIdleIcon = res.getDrawable(icIdleDrawableId);
            mIdleIconWidth = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_idleIconWidth, mIdleIcon.getMinimumWidth());
            mIdleIconHeight = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_idleIconHeight, mIdleIcon.getMinimumHeight());

            int icCancelDrawableId = a.getResourceId(R.styleable.DownloadButtonProgress_cancelIconDrawable, R.drawable.ic_default_cancel);
            mCancelIcon = res.getDrawable(icCancelDrawableId);
            mCancelIconWidth = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_cancelIconWidth, mCancelIcon.getMinimumWidth());
            mCancelIconHeight = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_cancelIconHeight, mCancelIcon.getMinimumHeight());

            int icFinishDrawableId = a.getResourceId(R.styleable.DownloadButtonProgress_finishIconDrawable, R.drawable.ic_default_finish);
            mFinishIcon = res.getDrawable(icFinishDrawableId);
            mFinishIconWidth = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_finishIconWidth, mFinishIcon.getMinimumWidth());
            mFinishIconHeight = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_finishIconHeight, mFinishIcon.getMinimumHeight());

            a.recycle();
        } else {
            mCurrState = STATE_IDLE;
            mCancelable = DEF_CANCELABLE;
            mProgressDeterminateColor = DEF_DETERMINATE_COLOR;
            mProgressIndeterminateColor = DEF_INDETERMINATE_COLOR;
            mProgressPaint.setStrokeWidth(DEF_PROGRESS_WIDTH);
            mProgressMargin = DEF_PROGRESS_MARGIN;
            mCurrProgress = 0;
            mMaxProgress = 100;

            mIdleBgColor = DEF_BG_COLOR;
            mFinishBgColor = DEF_BG_COLOR;
            mIndeterminateBgColor = DEF_BG_COLOR;
            mDeterminateBgColor = DEF_BG_COLOR;

            mIdleIcon = res.getDrawable(R.drawable.ic_default_download);
            mIdleIconWidth = mIdleIcon.getMinimumWidth();
            mIdleIconHeight = mIdleIcon.getMinimumHeight();

            mCancelIcon = res.getDrawable(R.drawable.ic_default_cancel);
            mCancelIconWidth = mCancelIcon.getMinimumWidth();
            mCancelIconHeight = mCancelIcon.getMinimumHeight();

            mFinishIcon = res.getDrawable(R.drawable.ic_default_finish);
            mFinishIconWidth = mFinishIcon.getMinimumWidth();
            mFinishIconHeight = mFinishIcon.getMinimumHeight();
        }

        if (mCurrState == STATE_INDETERMINATE)
            setIndeterminate();
    }

    private void initBackgroundDrawableFromAttribs(Resources res, TypedArray attrs) {
        int idleResId = attrs.getResourceId(R.styleable.DownloadButtonProgress_idleBackgroundDrawable, -1);
        int finishResId = attrs.getResourceId(R.styleable.DownloadButtonProgress_finishBackgroundDrawable, -1);
        int indeterminateResId = attrs.getResourceId(R.styleable.DownloadButtonProgress_indeterminateBackgroundDrawable, -1);
        int determinateResId = attrs.getResourceId(R.styleable.DownloadButtonProgress_determinateBackgroundDrawable, -1);

        if (idleResId != -1) mIdleBgDrawable = res.getDrawable(idleResId);
        if (finishResId != -1) mFinishBgDrawable = res.getDrawable(finishResId);
        if (indeterminateResId != -1) mIndeterminateBgDrawable = res.getDrawable(indeterminateResId);
        if (determinateResId != -1) mDeterminateBgDrawable = res.getDrawable(determinateResId);

        mIdleBgColor = attrs.getColor(R.styleable.DownloadButtonProgress_idleBackgroundColor, DEF_BG_COLOR);
        mFinishBgColor = attrs.getColor(R.styleable.DownloadButtonProgress_finishBackgroundColor, DEF_BG_COLOR);
        mIndeterminateBgColor = attrs.getColor(R.styleable.DownloadButtonProgress_indeterminateBackgroundColor, DEF_BG_COLOR);
        mDeterminateBgColor = attrs.getColor(R.styleable.DownloadButtonProgress_determinateBackgroundColor, DEF_BG_COLOR);
    }

    public int getCurrState() {
        return mCurrState;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public int getProgress() {
        return mCurrProgress;
    }

    public Drawable getIdleIcon() {
        return mIdleIcon;
    }

    public Drawable getCancelIcon() {
        return mCancelIcon;
    }

    public Drawable getFinishIcon() {
        return mFinishIcon;
    }

    public boolean isCancelable() {
        return mCancelable;
    }

    public int getIdleIconWidth() {
        return mIdleIconWidth;
    }

    public int getIdleIconHeight() {
        return mIdleIconHeight;
    }

    public int getCancelIconWidth() {
        return mCancelIconWidth;
    }

    public int getCancelIconHeight() {
        return mCancelIconHeight;
    }

    public int getFinishIconWidth() {
        return mFinishIconWidth;
    }

    public int getFinishIconHeight() {
        return mFinishIconHeight;
    }

    public int getIdleBgColor() {
        return mIdleBgColor;
    }

    public int getFinishBgColor() {
        return mFinishBgColor;
    }

    public int getIndeterminateBgColor() {
        return mIndeterminateBgColor;
    }

    public int getDeterminateBgColor() {
        return mDeterminateBgColor;
    }

    public Drawable getIdleBgDrawable() {
        return mIdleBgDrawable;
    }

    public Drawable getFinishBgDrawable() {
        return mFinishBgDrawable;
    }

    public Drawable getIndeterminateBgDrawable() {
        return mIndeterminateBgDrawable;
    }

    public Drawable getDeterminateBgDrawable() {
        return mDeterminateBgDrawable;
    }

    public int getProgressDeterminateColor() {
        return mProgressDeterminateColor;
    }

    public int getProgressIndeterminateColor() {
        return mProgressIndeterminateColor;
    }

    public int getProgressMargin() {
        return mProgressMargin;
    }

    public void setProgress(int progress) {
        if (mCurrState != STATE_DETERMINATE) return;
        mCurrProgress = Math.min(progress, mMaxProgress);
        invalidate();
    }

    public void setIdle() {
        mCurrState = STATE_IDLE;
        invalidate();
    }

    public void setIndeterminate() {
        mCurrIndeterminateBarPos = BASE_START_ANGLE;
        mCurrState = STATE_INDETERMINATE;
        invalidate();

        mIndeterminateAnimator.start();
    }

    public void setDeterminate() {
        mIndeterminateAnimator.end();

        mCurrProgress = 0;
        mCurrState = STATE_DETERMINATE;
        invalidate();
    }

    public void setFinish() {
        mCurrProgress = 0;
        mCurrState = STATE_FINISHED;
        invalidate();
    }

    public void setIdleIcon(Drawable idleIcon) {
        mIdleIcon = idleIcon;
        invalidate();
    }

    public void setCancelIcon(Drawable cancelIcon) {
        mCancelIcon = cancelIcon;
        invalidate();
    }

    public void setFinishIcon(Drawable finishIcon) {
        mFinishIcon = finishIcon;
        invalidate();
    }

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        invalidate();
    }

    public void setIdleIconWidth(int idleIconWidth) {
        mIdleIconWidth = idleIconWidth;
        invalidate();
    }

    public void setIdleIconHeight(int idleIconHeight) {
        mIdleIconHeight = idleIconHeight;
        invalidate();
    }

    public void setCancelIconWidth(int cancelIconWidth) {
        mCancelIconWidth = cancelIconWidth;
        invalidate();
    }

    public void setCancelIconHeight(int cancelIconHeight) {
        mCancelIconHeight = cancelIconHeight;
        invalidate();
    }

    public void setFinishIconWidth(int finishIconWidth) {
        mFinishIconWidth = finishIconWidth;
        invalidate();
    }

    public void setFinishIconHeight(int finishIconHeight) {
        mFinishIconHeight = finishIconHeight;
        invalidate();
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
        invalidate();
    }

    public void setIdleBgColor(int idleBgColor) {
        mIdleBgColor = idleBgColor;
        invalidate();
    }

    public void setFinishBgColor(int finishBgColor) {
        mFinishBgColor = finishBgColor;
        invalidate();
    }

    public void setIndeterminateBgColor(int indeterminateBgColor) {
        mIndeterminateBgColor = indeterminateBgColor;
        invalidate();
    }

    public void setDeterminateBgColor(int determinateBgColor) {
        mDeterminateBgColor = determinateBgColor;
        invalidate();
    }

    public void setIdleBgDrawable(Drawable idleBgDrawable) {
        mIdleBgDrawable = idleBgDrawable;
        invalidate();
    }

    public void setFinishBgDrawable(Drawable finishBgDrawable) {
        mFinishBgDrawable = finishBgDrawable;
        invalidate();
    }

    public void setIndeterminateBgDrawable(Drawable indeterminateBgDrawable) {
        mIndeterminateBgDrawable = indeterminateBgDrawable;
        invalidate();
    }

    public void setDeterminateBgDrawable(Drawable determinateBgDrawable) {
        mDeterminateBgDrawable = determinateBgDrawable;
        invalidate();
    }

    public void setProgressDeterminateColor(int progressDeterminateColor) {
        mProgressDeterminateColor = progressDeterminateColor;
        invalidate();
    }

    public void setProgressIndeterminateColor(int progressIndeterminateColor) {
        mProgressIndeterminateColor = progressIndeterminateColor;
        invalidate();
    }

    public void setProgressMargin(int progressMargin) {
        mProgressMargin = progressMargin;
        invalidate();
    }

    public void addOnClickListener(OnClickListener listener) {
        if (!mClickListeners.contains(listener))
            mClickListeners.add(listener);
    }

    public void removeOnClickListener(OnClickListener listener) {
        mClickListeners.remove(listener);
    }

    @Override
    public void onClick(View v) {
        if (!mCancelable && (mCurrState == STATE_INDETERMINATE || mCurrState == STATE_DETERMINATE))
            return;

        if (mCurrState == STATE_IDLE) {
            for (OnClickListener listener : mClickListeners)
                listener.onIdleButtonClick(v);
        }
        else if (mCurrState == STATE_INDETERMINATE || mCurrState == STATE_DETERMINATE) {
            for (OnClickListener listener : mClickListeners)
                listener.onCancelButtonClick(v);
        }
        else if (mCurrState == STATE_FINISHED) {
            for (OnClickListener listener : mClickListeners)
                listener.onFinishButtonClick(v);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setProgressRectBounds();
    }

    private void drawIdleState(Canvas canvas) {
        if (mIdleBgDrawable != null) {
            mIdleBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            mIdleBgDrawable.draw(canvas);
        } else {
            mBgRect.set(0, 0, getWidth(), getHeight());
            mBgPaint.setColor(mIdleBgColor);
            canvas.drawOval(mBgRect, mBgPaint);
        }

        drawDrawableInCenter(mIdleIcon, canvas, mIdleIconWidth, mIdleIconHeight);
    }

    private void drawFinishState(Canvas canvas) {
        if (mFinishBgDrawable != null) {
            mFinishBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            mFinishBgDrawable.draw(canvas);
        } else {
            mBgRect.set(0, 0, getWidth(), getHeight());
            mBgPaint.setColor(mFinishBgColor);
            canvas.drawOval(mBgRect, mBgPaint);
        }

        drawDrawableInCenter(mFinishIcon, canvas, mFinishIconWidth, mFinishIconHeight);
    }

    private void drawIndeterminateState(Canvas canvas) {
        if (mIndeterminateBgDrawable != null) {
            mIndeterminateBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            mIndeterminateBgDrawable.draw(canvas);
        } else {
            mBgRect.set(0, 0, getWidth(), getHeight());
            mBgPaint.setColor(mIndeterminateBgColor);
            canvas.drawOval(mBgRect, mBgPaint);
        }

        if (mCancelable) {
            drawDrawableInCenter(mCancelIcon, canvas, mCancelIconWidth, mCancelIconHeight);
        }

        mProgressPaint.setColor(mProgressIndeterminateColor);
        canvas.drawArc(mProgressRect, mCurrIndeterminateBarPos, 90, false, mProgressPaint);
    }

    private void drawDeterminateState(Canvas canvas) {
        if (mDeterminateBgDrawable != null) {
            mDeterminateBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            mDeterminateBgDrawable.draw(canvas);
        } else {
            mBgRect.set(0, 0, getWidth(), getHeight());
            mBgPaint.setColor(mDeterminateBgColor);
            canvas.drawOval(mBgRect, mBgPaint);
        }

        if (mCancelable) {
            drawDrawableInCenter(mCancelIcon, canvas, mCancelIconWidth, mCancelIconHeight);
        }

        mProgressPaint.setColor(mProgressDeterminateColor);
        canvas.drawArc(mProgressRect, BASE_START_ANGLE, getDegrees(), false, mProgressPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCurrState == STATE_IDLE) {
            drawIdleState(canvas);
        }
        else if (mCurrState == STATE_INDETERMINATE) {
            drawIndeterminateState(canvas);
        }
        else if (mCurrState == STATE_DETERMINATE) {
            drawDeterminateState(canvas);
        }
        else if (mCurrState == STATE_FINISHED) {
            drawFinishState(canvas);
        }
    }

    private void setProgressRectBounds() {
        float halfStroke = mProgressPaint.getStrokeWidth() / 2.0f;
        float totalMargin = mProgressMargin + halfStroke;
        mProgressRect.set(totalMargin, totalMargin, getWidth() - totalMargin, getHeight() - totalMargin);
    }

    private void initIndeterminateAnimator() {
        mIndeterminateAnimator = ValueAnimator.ofInt(0, 360);
        mIndeterminateAnimator.setInterpolator(new LinearInterpolator());
        mIndeterminateAnimator.setDuration(1000);
        mIndeterminateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mIndeterminateAnimator.setRepeatMode(ValueAnimator.RESTART);
        mIndeterminateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mCurrIndeterminateBarPos = value - BASE_START_ANGLE;
                invalidate();
            }
        });
    }

    private float getDegrees() {
        return ((float)mCurrProgress / (float)mMaxProgress) * 360;
    }

    private void drawDrawableInCenter(Drawable drawable, Canvas canvas, int width, int height) {
        int left = (getWidth() / 2) - (width / 2);
        int top = (getHeight() / 2) - (height / 2);
        drawable.setBounds(left, top, left + width, top + height);
        drawable.draw(canvas);
    }

    public interface OnClickListener {
        void onIdleButtonClick(View view);
        void onCancelButtonClick(View view);
        void onFinishButtonClick(View view);
    }
}