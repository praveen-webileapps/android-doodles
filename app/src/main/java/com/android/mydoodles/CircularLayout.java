package com.android.mydoodles;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by PraveenKatha on 26/09/16.
 */
public class CircularLayout extends ViewGroup {

    public static final float scaler = 10000f;
    private final GestureDetectorCompat mDetector;
    private int childSize;
    private float radius;
    private final float segmentAngle = 90f / 4;
    private float currentRotation = 0;
    private float viewSize;
    private Scroller mScroller;
    private ValueAnimator mScrollAnimator;
    private float viewInnerRadius;
    private float childRadius;
    private float minRotation;
    private float maxRotation;
    private RectF bgRect;
    private float pathWidth;
    private Adapter mAdapter;
    private int selectedIndex;

    interface Adapter {
        View getView(int index, ViewGroup parent);
        int getCount();
        OnClickListener getOnClickListener(int index);
    }

    public CircularLayout(Context context) {
        this(context, null);
    }

    public CircularLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(getContext());

        //mScroller.setFriction(.25f);
        mScroller.setFriction(ViewConfiguration.getScrollFriction() * 1000);
        mScrollAnimator = ValueAnimator.ofFloat(0, 1);
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                startScrollAnimation();
            }
        });

        mDetector = new GestureDetectorCompat(getContext(), new GestureListener());
        bgRect = new RectF();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        measureChildren(measureSpec, measureSpec);
        childSize = getChildAt(0).getMeasuredHeight();
        viewSize = getMeasuredWidth();
        childRadius = (float) (Math.sqrt(2) * childSize / 2);

        radius = viewSize - childRadius;

        viewInnerRadius = viewSize - 2 * childRadius;

        minRotation = 0;
        maxRotation = Math.round(Math.max(0, segmentAngle * getChildCount() - 90));

        pathWidth = viewSize - viewInnerRadius;

        bgRect.set(0, 0, 2 * viewSize, 2 * viewSize);
        bgRect.inset(pathWidth / 2, pathWidth / 2);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.save();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(30, 0, 0, 0));
        paint.setStrokeWidth(pathWidth);
        //paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawArc(bgRect, 0, 360, false, paint);
        canvas.restore();

        super.dispatchDraw(canvas);
    }

    private void startScrollAnimation() {
        if (mScroller.computeScrollOffset()) {
            setRotation(mScroller.getCurrY() / scaler);
        } else {
            mScrollAnimator.cancel();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    int cx, cy;
    float cTheta;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        for (int i = 0; i < getChildCount(); i++) {

            cTheta = (float) Math.toRadians(segmentAngle * i + segmentAngle / 2 - currentRotation);

            cx = (int) (viewSize - radius * Math.cos(cTheta));
            cy = (int) (viewSize - radius * Math.sin(cTheta));

            getChildAt(i).layout(cx - childSize / 2, cy - childSize / 2, cx + childSize / 2, cy + childSize / 2);

        }

    }

    public float getPathWidth() {
        return pathWidth;
    }

    public void setAdapter(CircularLayout.Adapter adapter) {

        removeAllViews();
        View childView;
        mAdapter = adapter;

        for (int i = 0; i < adapter.getCount(); i++) {

            childView = adapter.getView(i, this);

            if (selectedIndex == i) {
                childView.setSelected(true);
            }

            addView(childView);
        }

        requestLayout();
    }

    private boolean isAnimationRunning() {
        return !mScroller.isFinished();
    }

    private void stopScrolling() {
        mScroller.forceFinished(true);
    }


//    public int getChildIndexContainingPoint(int x, int y) {
//
//        float pointDistance = getRadius(x, y);
//
//        if (pointDistance < viewSize && pointDistance > viewInnerRadius) {
//
//            int startIndex, endIndex;
//
//            startIndex = (int) (currentRotation / segmentAngle);
//            endIndex = currentRotation;
//
//
//
//        }
//    }

    private float getRadius(float x, float y) {
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

//            float radius = getRadius(viewSize - e.getX(), viewSize - e.getY());
//
//            if (radius < viewSize && radius > viewInnerRadius) {
//
//                float angleRadians = (float) Math.toRadians(Math.atan2(viewSize - e.getY(), viewSize - e.getX()));
//
//                int index = getChildIndexOfRotation(angleRadians + currentRotation);
//
//                if (selectedIndex == index)
//                    return false;
//
//                getChildAt(selectedIndex).setSelected(false);
//
//                selectedIndex = index;
//
//                v.setSelected(!v.isSelected());
//
//                if (mAdapter.getOnClickListener(selectedIndex) != null) {
//                    mAdapter.getOnClickListener(selectedIndex).onClick(v);
//                }
//
//                requestLayout();
//
//                return true;
//
//            }

            return super.onSingleTapConfirmed(e);
        }


        @Override
        public boolean onDown(MotionEvent e) {

            if (isAnimationRunning()) {
                stopScrolling();
            }
            return true;

        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float radius = getRadius(viewSize - e2.getX(), viewSize - e2.getY());

            if (radius < viewSize && radius > viewInnerRadius) {

                float delta = getDelta(velocityX, velocityY, viewSize - e2.getX(), viewSize - e2.getY());

                System.out.println("Velocity " + delta);

                mScroller.fling(
                        0,
                        Math.round(currentRotation * scaler),
                        0,
                        Math.round(scaler * delta),
                        0,
                        0,
                        Math.round(scaler * minRotation),
                        Math.round(scaler * maxRotation));

                int duration = mScroller.getDuration();

                mScrollAnimator.setDuration(duration);
                mScrollAnimator.start();

//                System.out.println("delta angle velocity " + delta + "," + duration / 1000);
                return true;
            }

            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float radius = getRadius(viewSize - e2.getX(), viewSize - e2.getY());


            if (radius < viewSize && radius > viewInnerRadius) {

                float delta = (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
                float deltaAngle = (float) (Math.signum(distanceX) * Math.toDegrees(delta));

                scrollRotation(deltaAngle / radius);
                //System.out.println("delta angle " + deltaAngle + "," + distanceX + "," + distanceY);
                return true;
            }

            return false;
        }
    }

    public void setRotation(float rotation) {
        this.currentRotation = rotation;
        //android.util.Log.v("Rotation", "" + currentRotation);
        requestLayout();
    }

    private void scrollRotation(float deltaRotation) {

        if (deltaRotation == 0) {
            return;
        }

        if ((currentRotation == minRotation && deltaRotation < 0) || (currentRotation == maxRotation && deltaRotation > 0))
            return;

        currentRotation += deltaRotation;
        currentRotation = Math.max(currentRotation, minRotation);
        currentRotation = Math.min(currentRotation, maxRotation);

        requestLayout();
    }

    private float getDelta(float dx, float dy, float x, float y) {
        float l = (float) Math.sqrt(dx * dx + dy * dy);

        // decide if the scalar should be negative or positive by finding
        // the dot product of the vector perpendicular to (x,y).
        float crossX = -y;
        float crossY = x;

        float dot = (crossX * dx + crossY * dy);
        float sign = Math.signum(dot);

        return l * sign;
    }

}


