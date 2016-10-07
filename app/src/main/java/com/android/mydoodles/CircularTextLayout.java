package com.android.mydoodles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;


/**
 * Created by PraveenKatha on 05/10/16.
 */

public class CircularTextLayout extends ViewGroup {

    public static final float CURVATURE_FRACTION = 0.7f;
    private final int pathPadding;
    private float childrenRadius;
    private float segmentAngle;
    private float currentRotation = 0;
    private RectF bgRect;
    private static float BASE_FONT = 60f;
    private float childHeight;
    private float childWidth;
    private float childPivotY;
    private float bgStrokeWidth;
    private Adapter mAdapter;
    private int selectedIndex = 0;
    private float childPivotX;

    interface Adapter {
        String getText(int index);

        OnClickListener getOnClickListener(int index);

        int getCount();
    }

    public CircularTextLayout(Context context) {
        this(context, null);
    }

    public CircularTextLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setStaticTransformationsEnabled(true);
        pathPadding = MyViewUtils.dpToPx(20);
    }

    private void initDimensions(int segments) {
        segmentAngle = 90f / segments;
    }


    public Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(Adapter adapter) {

        removeAllViews();
        CircularTextView tv;
        mAdapter = adapter;

        initDimensions(mAdapter.getCount());

        for (int i = 0; i < adapter.getCount(); i++) {

            tv = new CircularTextView(adapter.getText(i), getContext());
//
            if (selectedIndex == i) {
                tv.setSelected(true);
            }

            final int index = i;
            tv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (selectedIndex == index)
                        return;

                    getChildAt(selectedIndex).setSelected(false);

                    selectedIndex = index;

                    v.setSelected(!v.isSelected());

                    if (mAdapter.getOnClickListener(selectedIndex) != null) {
                        mAdapter.getOnClickListener(selectedIndex).onClick(v);
                    }

                    requestLayout();
                }
            });

            addView(tv);
        }

        requestLayout();
    }

    public static float getRecommendedFont(Adapter adapter, Typeface typeface, float layoutSize) {

        float width;

        float fontSize = -1;

        Rect textRect = new Rect();

        float segmentAngleRadians = (float) Math.toRadians(90f / adapter.getCount());

        for (int i = 0; i < adapter.getCount(); i++) {

            CircularTextView.getTextBounds(adapter.getText(i), BASE_FONT, typeface, textRect);

            width = (float) (2 * layoutSize * Math.sin(segmentAngleRadians * CURVATURE_FRACTION / 2));

            if (fontSize == -1) {
                fontSize = getFontSize(width, textRect.width());
            } else {
                fontSize = Math.min(fontSize, getFontSize(width, textRect.width()));
            }
        }

        return fontSize;
    }


    private static float getFontSize(float width, float widthOfTextWithBaseFont) {
        return width / widthOfTextWithBaseFont * BASE_FONT;
    }


    private float layoutSize;


    public void setFontSize(float fontSize) {
        for (int i = 0; i < getChildCount(); i++) {
            tv = (CircularTextView) getChildAt(i);
            tv.setFont(fontSize);
        }
    }

    private void setChildDimensions() {

        for (int i = 0; i < getChildCount(); i++) {
            tv = (CircularTextView) getChildAt(i);
            tv.setCurvature(segmentAngle).setPathPadding(pathPadding).setOuterRadius(layoutSize);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        layoutSize = Math.min(getMeasuredWidth(),getMeasuredHeight());

        setChildDimensions();

        int measureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        measureChildren(measureSpec, measureSpec);

        tv = (CircularTextView) getChildAt(0);

        childrenRadius = tv.getPathRadius();

        childPivotX = tv.getMeasuredWidth() / 2;

        childPivotY = tv.getOuterRadius() - tv.getPathRadius();

        bgRect = new RectF(0, 0, 2 * layoutSize, 2 * layoutSize);

        bgRect.inset(layoutSize - tv.getPathRadius(), layoutSize - tv.getPathRadius());

        bgStrokeWidth = tv.getOuterRadius() - tv.getInnerRadius();

    }


    public float getBgStrokeWidth() {
        return bgStrokeWidth;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
//
        canvas.save();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(30, 0, 0, 0));
        paint.setStrokeWidth(bgStrokeWidth);
        //paint.setStyle(Paint.Style.FILL_AND_STROKE);

        canvas.drawArc(bgRect, 0, 360, false, paint);
        canvas.restore();

        super.dispatchDraw(canvas);
    }

    private float cx, cy;
    private float cTheta;

    private CircularTextView tv;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {


        for (int i = 0; i < getChildCount(); i++) {

            tv = (CircularTextView) getChildAt(i);

            childHeight = tv.getMeasuredHeight();
            childWidth = tv.getMeasuredWidth();

            cTheta = (float) Math.toRadians(segmentAngle * i + segmentAngle / 2 - currentRotation);

            cx = (float) (layoutSize - childrenRadius * Math.cos(cTheta));
            cy = (float) (layoutSize - childrenRadius * Math.sin(cTheta));

            tv.layout((int) (cx - childWidth / 2), (int) (cy - childPivotY), (int) (cx + childWidth / 2),
                    (int) (cy - childPivotY + childHeight));

        }

        tv = null;

    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {

        int i = indexOfChild(child);
        t.getMatrix().setRotate(-90 + i * segmentAngle + segmentAngle / 2, childPivotX, childPivotY);

        return true;
    }
}
