package com.android.mydoodles;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by PraveenKatha on 04/10/16.
 */

public class CircularTextView extends View {

    private final float defaultStrokeWidth;
    private double alphaRadians;
    public float alpha;
    public String text;
    public Path path;
    private Paint paint;
    private float vOffset;
    private RectF pathRectF;

    private float outerRadius;
    private float innerRadius;
    private boolean selected;
    private int pathPadding;

    public float getOuterRadius() {
        return outerRadius;
    }

    private float pathRadius;
    private Rect textBounds;


    public CircularTextView(Context context) {
        this(context, null);
    }

    public CircularTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        path = new Path();
        paint.setColor(Color.WHITE);
        defaultStrokeWidth = paint.getStrokeWidth();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            paint.setLetterSpacing(.15f);
        }

        textBounds = new Rect();
        pathRectF = new RectF();

        paint.setStyle(Paint.Style.STROKE);
        paint.setTextAlign(Paint.Align.CENTER);

        paint.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "BalooThambi-Regular.ttf"));
    }

    public CircularTextView(String text, Context context) {
        this(context);
        setText(text);
    }


    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        invalidate();
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    public static int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        paint.getTextBounds(text, 0, text.length(), textBounds);
        innerRadius = outerRadius - textBounds.height() - 2 * pathPadding;

        setMeasuredDimension(getDefaultSize(getRequiredWidth(outerRadius,alphaRadians),widthMeasureSpec),
                getDefaultSize(getRequiredHeight(outerRadius,innerRadius,alphaRadians),heightMeasureSpec));

        path.reset();

        pathRadius = (outerRadius + innerRadius) / 2;

        vOffset = -textBounds.centerY();

        pathRectF.set(getMeasuredWidth() / 2 - pathRadius, outerRadius - pathRadius,
                getMeasuredWidth() / 2 + pathRadius, outerRadius + pathRadius);

        path.addArc(pathRectF, -90 - alpha / 2, alpha);

    }

    private int getRequiredHeight(float outerRadius, float innerRadius, double alphaRadians) {
        return (int) (outerRadius - innerRadius * Math.cos(alphaRadians / 2));
    }

    private int getRequiredWidth(float outerRadius, double alphaRadians) {
        return (int) (2 * outerRadius * Math.sin(alphaRadians / 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(selected) {
            paint.setColor(Color.argb(20, 0, 0, 0));
            paint.setStrokeWidth(outerRadius - innerRadius);
            canvas.drawPath(path,paint);
        }


        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(defaultStrokeWidth);

        canvas.drawTextOnPath(text, path, 0, vOffset, paint);
    }

    public float getPathRadius() {
        return pathRadius;
    }

    public CircularTextView setOuterRadius(float R) {
        this.outerRadius = R;
        return this;
    }

    public CircularTextView setCurvature(float alpha) {
        this.alpha = alpha;
        this.alphaRadians = Math.toRadians(alpha);
        return this;
    }

    public CircularTextView setFont(float fontSize) {
        paint.setTextSize(fontSize);
        return this;
    }

    public CircularTextView setText(String text) {
        this.text = text;
        return this;
    }

    public float getInnerRadius() {
        return innerRadius;
    }

    public static void getTextBounds(String text, float font, Typeface typeface, Rect rect) {

        Paint p = new Paint();

        p.setStyle(Paint.Style.STROKE);

        p.setTypeface(typeface);
        p.setTextSize(font);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            p.setLetterSpacing(.15f);
        }

        p.getTextBounds(text, 0, text.length(), rect);
    }

    public CircularTextView setPathPadding(int pathPadding) {
        this.pathPadding = pathPadding;
        return this;
    }
}
