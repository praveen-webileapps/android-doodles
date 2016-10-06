package com.android.mydoodles;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Created by PraveenKatha on 03/10/16.
 */

public class MyLayout extends LinearLayout {
    public MyLayout(Context context) {
        super(context);
    }

    public MyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setStaticTransformationsEnabled(true);
    }

    public MyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setStaticTransformationsEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setStaticTransformationsEnabled(true);
    }


    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {

        t.clear();

        if(indexOfChild(child)  % 2 == 0) {
            int ht = child.getHeight();
            int width = child.getWidth();
            //t.getMatrix().postTranslate(width/2,0);
            t.getMatrix().postRotate(45,width/2,ht/2);
        }

        return true;
    }
}
