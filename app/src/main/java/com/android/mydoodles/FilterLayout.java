package com.android.mydoodles;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by PraveenKatha on 06/10/16.
 */

public class FilterLayout extends FrameLayout {

    private final Typeface textTypeFace;
    private int marginBetweenChildren;
    private float marginScale;

    public FilterLayout(Context context) {
        this(context, null);
    }

    public FilterLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        marginScale = 0.1f;
        textTypeFace = Typeface.createFromAsset(getResources().getAssets(), "BalooThambi-Regular.ttf");

        addGenreFilterLayout(context);
        addCircularTextLayout(context, 1);
        addCircularTextLayout(context, 2);

    }

    private void addGenreFilterLayout(Context context) {
        CircularLayout cLayout;
        cLayout = new CircularLayout(context);
        cLayout.setLayoutParams(new LayoutParams(0, 0, Gravity.RIGHT | Gravity.BOTTOM));
        addView(cLayout, 0);
    }

    private void addCircularTextLayout(Context context, int index) {
        CircularTextLayout cLayout;
        cLayout = new CircularTextLayout(context);
        cLayout.setLayoutParams(new LayoutParams(0, 0, Gravity.RIGHT | Gravity.BOTTOM));
        //cLayout.setBackgroundColor(Color.WHITE);
        addView(cLayout, index);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());

        if(size <= 0)
            return;

        float fontOne, fontTwo, minFontSize;
        marginBetweenChildren = (int) (marginScale * size);

        CircularLayout genreLayout;
        CircularTextLayout cLayout;

        genreLayout = (CircularLayout) getChildAt(0);
        setChildMeasureSpec(size, genreLayout);
        size -= genreLayout.getPathWidth() + marginBetweenChildren;

        cLayout = (CircularTextLayout) getChildAt(1);
        setChildMeasureSpec(size, cLayout);

        fontOne = CircularTextLayout.getRecommendedFont(cLayout.getAdapter(), textTypeFace, cLayout.getMeasuredWidth());

        size -= cLayout.getBgStrokeWidth() + marginBetweenChildren;

        cLayout = (CircularTextLayout) getChildAt(2);
        setChildMeasureSpec(size, cLayout);
        fontTwo = CircularTextLayout.getRecommendedFont(cLayout.getAdapter(), textTypeFace, cLayout.getMeasuredWidth());

        minFontSize = Math.min(fontOne, fontTwo);

       ((CircularTextLayout) getChildAt(1)).setFontSize(minFontSize);
        ((CircularTextLayout) getChildAt(2)).setFontSize(minFontSize);

    }


    private void setChildMeasureSpec(int size, View childLayout) {
        childLayout.measure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
    }

    public void setPaidLayoutAdapter(CircularTextLayout.Adapter adapter) {
       ((CircularTextLayout) getChildAt(2)).setAdapter(adapter);
    }

    public void setStreamStatusAdapter(CircularTextLayout.Adapter adapter) {
        ((CircularTextLayout) getChildAt(1)).setAdapter(adapter);
    }


    public void setGenreFilterAdapter(CircularLayout.Adapter genreFilterAdapter) {
        ((CircularLayout) getChildAt(0)).setAdapter(genreFilterAdapter);
    }
}
