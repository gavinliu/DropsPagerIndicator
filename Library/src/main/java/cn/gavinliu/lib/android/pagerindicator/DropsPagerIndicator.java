package cn.gavinliu.lib.android.pagerindicator;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gavin on 15-4-10.
 */
public class DropsPagerIndicator extends View {

    private List<Integer> mColors;

    private ArrayList<ValueAnimator> mAnimators;

    private int mWidth;
    private int mHeight;

    private float leftCircleRadius;
    private float leftCircleX;

    private float rightCircleRadius;
    private float rightCircleX;

    private List<PointF> mPoints;

    private int mPagerCount;

    private float mMaxCircleRadius;
    private float mMinCircleRadius;

    private Paint mPaint;
    private Path mPath = new Path();

    private Mode mMode = Mode.Normal;

    public enum Mode {
        Normal, Bend
    }

    public DropsPagerIndicator(Context context) {
        this(context, null);
    }

    public DropsPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropsPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mAnimators = new ArrayList<>();
        mPoints = new ArrayList<>();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(leftCircleX, mHeight / 2, leftCircleRadius, mPaint);
        canvas.drawCircle(rightCircleX, mHeight / 2, rightCircleRadius, mPaint);

        switch (mMode) {
            case Normal:
                drawModeNormal(canvas);
                break;

            case Bend:
                drawModeBend(canvas);
                break;
        }
    }

    private void drawModeNormal(Canvas canvas) {
        mPath.reset();

        mPath.moveTo(rightCircleX, mHeight / 2);

        mPath.lineTo(rightCircleX, mHeight / 2 - rightCircleRadius);

        mPath.quadTo(rightCircleX,
                mHeight / 2 - rightCircleRadius,

                leftCircleX,
                mHeight / 2 - leftCircleRadius);

        mPath.lineTo(leftCircleX, mHeight / 2 + leftCircleRadius);

        mPath.quadTo(leftCircleX,
                mHeight / 2 + leftCircleRadius,

                rightCircleX,
                mHeight / 2 + rightCircleRadius);

        mPath.close();

        canvas.drawPath(mPath, mPaint);
    }

    private void drawModeBend(Canvas canvas) {
        float middleOffset = (leftCircleX - rightCircleX) / (mPoints.get(1).x - mPoints.get(0).x) * (mHeight / 10);

        mPath.reset();

        mPath.moveTo(rightCircleX, mHeight / 2);

        mPath.lineTo(rightCircleX, mHeight / 2 - rightCircleRadius);

        mPath.cubicTo(rightCircleX,
                mHeight / 2 - rightCircleRadius,

                rightCircleX + (leftCircleX - rightCircleX) / 2.0F,
                mHeight / 2 + middleOffset,

                leftCircleX,
                mHeight / 2 - leftCircleRadius);

        mPath.lineTo(leftCircleX, mHeight / 2 + leftCircleRadius);

        mPath.cubicTo(leftCircleX,
                mHeight / 2 + leftCircleRadius,

                rightCircleX + (leftCircleX - rightCircleX) / 2.0F,
                mHeight / 2 - middleOffset,

                rightCircleX,
                mHeight / 2 + rightCircleRadius);

        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mMaxCircleRadius = 0.45f * mHeight;
        mMinCircleRadius = 0.15f * mHeight;

        resetPoint();
    }

    private void createAnimator(int position) {
        if (mPoints.isEmpty()) {
            return;
        }
        mAnimators.clear();

        int next = Math.min(mPagerCount - 1, position + 1);

        float leftX = mPoints.get(position).x;
        float rightX = mPoints.get(next).x;

        ObjectAnimator rightPointAnimator = ObjectAnimator.ofFloat(this, "rightCircleX", leftX, rightX);
        rightPointAnimator.setDuration(5000L);
        rightPointAnimator.setInterpolator(new DecelerateInterpolator(0.8F));
        mAnimators.add(rightPointAnimator);

        ObjectAnimator leftPointAnimator = ObjectAnimator.ofFloat(this, "leftCircleX", leftX, rightX);
        leftPointAnimator.setDuration(5000L);
        leftPointAnimator.setInterpolator(new AccelerateInterpolator(1.5F));
        mAnimators.add(leftPointAnimator);

        ObjectAnimator rightCircleRadiusAnimator = ObjectAnimator.ofFloat(this, "rightCircleRadius", mMinCircleRadius, mMaxCircleRadius);
        rightCircleRadiusAnimator.setDuration(5000L);
        rightCircleRadiusAnimator.setInterpolator(new AccelerateInterpolator(1.5F));
        mAnimators.add(rightCircleRadiusAnimator);

        ObjectAnimator leftCircleRadiusAnimator = ObjectAnimator.ofFloat(this, "leftCircleRadius", mMaxCircleRadius, mMinCircleRadius);
        leftCircleRadiusAnimator.setDuration(5000L);
        leftCircleRadiusAnimator.setInterpolator(new DecelerateInterpolator(0.8F));
        mAnimators.add(leftCircleRadiusAnimator);

        int color1 = mColors.get(position);
        int color2 = mColors.get(next);
        ValueAnimator paintColorAnimator = ObjectAnimator.ofInt(color1, color2);
        paintColorAnimator.setDuration(5000L);
        paintColorAnimator.setEvaluator(new ArgbEvaluator());
        paintColorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        paintColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            public void onAnimationUpdate(ValueAnimator animator) {
                mPaint.setColor((Integer) animator.getAnimatedValue());
            }
        });

        mAnimators.add(paintColorAnimator);
    }

    private void seekAnimator(float offset) {
        for (ValueAnimator animator : mAnimators) {
            animator.setCurrentPlayTime((long) (5000.0F * offset));
        }

        postInvalidate();
    }

    public void setPositionAndOffset(int position, float offSet) {
        createAnimator(position);
        seekAnimator(offSet);
    }

    private void resetPoint() {
        mPoints.clear();
        for (int i = 0; i < mPagerCount; i++) {
            int x = mWidth / (mPagerCount + 1) * (i + 1);
            mPoints.add(new PointF(x, 0));
        }

        if (!mPoints.isEmpty()) {
            leftCircleX = mPoints.get(0).x;
            leftCircleRadius = mMaxCircleRadius;

            rightCircleX = mPoints.get(0).x;
            rightCircleRadius = mMinCircleRadius;
            postInvalidate();
        }
    }

    public void setPagerCount(int pagerCount) {
        mPagerCount = pagerCount;
    }

    public void setColors(List<Integer> colors) {
        mColors = colors;

        if (!colors.isEmpty()) {
            mPaint.setColor(colors.get(0));
        }
    }

    public void setMode(Mode mode) {
        this.mMode = mode;
    }

    public float getLeftCircleX() {
        return leftCircleX;
    }

    public void setLeftCircleX(float leftCircleX) {
        this.leftCircleX = leftCircleX;
    }

    public float getLeftCircleRadius() {
        return leftCircleRadius;
    }

    public void setLeftCircleRadius(float leftCircleRadius) {
        this.leftCircleRadius = leftCircleRadius;
    }

    public float getRightCircleRadius() {
        return rightCircleRadius;
    }

    public void setRightCircleRadius(float rightCircleRadius) {
        this.rightCircleRadius = rightCircleRadius;
    }

    public float getRightCircleX() {
        return rightCircleX;
    }

    public void setRightCircleX(float rightCircleX) {
        this.rightCircleX = rightCircleX;
    }
}
