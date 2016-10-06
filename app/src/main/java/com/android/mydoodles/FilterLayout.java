package com.android.mydoodles;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by PraveenKatha on 06/10/16.
 */

public class FilterLayout extends FrameLayout {

    public FilterLayout(Context context) {
        this(context, null);
    }

    public FilterLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();



    }

}
