package com.android.mydoodles;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by PraveenKatha on 21/07/16.
 */
public class CircularView extends View {

    public static final float visibleTotalAngle = 90f;
    private ValueAnimator mScrollAnimator;
    private final Paint paint;
    private final Typeface customTypeFace;
    private final GestureDetectorCompat mDetector;
    private int currentRotation = 0;
    private float segmentAngle = visibleTotalAngle/4;

    private float iconRadius;

    private int viewPadding = dpToPx(10);

    private int iconSize = dpToPx(36); // 18 dp

    private RectF iconRectF = new RectF(-iconSize / 2, -iconSize / 2, iconSize / 2, iconSize / 2);


    private int labelPadding = dpToPx(5);
    private int viewRadius;
    private RectF labelRectF;
    private int viewInnerRadius;
    private int labelOffset;
    private int maxRotation;
    private int minRotation;
    private Scroller mScroller;
    private RectF pathRect;
    private Path labelPath;
    private float defStrokeWidth;
    private Bitmap defaultBitmap;
    private int startItemIndex;
    private float endItemIndex;
    private int drawItemIndex;

    public CircularView(Context context) {
        this(context, null);
    }

    public CircularView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        customTypeFace = Typeface.createFromAsset(getResources().getAssets(), "BalooThambi-Regular.ttf");
        paint.setTypeface(customTypeFace);
        paint.setTextSize(40f);

        defStrokeWidth = paint.getStrokeWidth();

        defaultBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_invert_colors_white_36dp);

        minRotation = 0;
        maxRotation = (int) Math.max(0, segmentAngle * getCount() - 90);

        mDetector = new GestureDetectorCompat(getContext(), new GestureListener());

        mScroller = new Scroller(getContext());

        mScroller.setFriction(0.25f);

        mScrollAnimator = ValueAnimator.ofFloat(0, 1);
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                startScrollAnimation();
            }
        });
    }

    private void startScrollAnimation() {
        if (mScroller.computeScrollOffset()) {
            setRotation(mScroller.getCurrY());
        } else {
            mScrollAnimator.cancel();
        }
    }

    private int getCount() {
        return 10;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    private void scrollRotation(int delta) {

        if (delta == 0) {
            return;
        }

        if ((currentRotation == minRotation && delta < 0) || (currentRotation == maxRotation && delta > 0))
            return;

        currentRotation += delta;
        currentRotation = Math.max(currentRotation, minRotation);
        currentRotation = Math.min(currentRotation, maxRotation);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(viewRadius, viewRadius);
        canvas.rotate(180 - currentRotation);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(viewRadius - viewInnerRadius);
        paint.setColor(Color.BLACK);
        //paint.setAlpha(100);
        canvas.drawArc(pathRect, 0, segmentAngle * getCount(), false, paint);
        //paint.setAlpha(255);

        paint.setStrokeWidth(defStrokeWidth);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        for (drawItemIndex = startItemIndex; drawItemIndex < endItemIndex + 1; drawItemIndex++) {
            drawSegment(canvas);
        }

    }

    private void initDrawMetrics() {

        int maxLabelHeight = getMaxLabelHeight();

        viewRadius = Math.min(getHeight(), getWidth());

        iconRadius = viewRadius - iconSize / 2 - viewPadding;

        int labelRadius = viewRadius - iconSize - viewPadding;
        labelRectF = new RectF(-labelRadius, -labelRadius, labelRadius, labelRadius);
        labelOffset = maxLabelHeight / 2 + labelPadding;
        viewInnerRadius = labelRadius - labelOffset - maxLabelHeight / 2 - viewPadding;

        float pathRadius = (viewRadius + viewInnerRadius) / 2f;
        pathRect = new RectF(-pathRadius, -pathRadius, pathRadius, pathRadius);

        setDrawIndexes();

    }

    private void setDrawIndexes() {
        startItemIndex = (int) (currentRotation / segmentAngle);
        endItemIndex =  Math.min(currentRotation + visibleTotalAngle, segmentAngle * getCount()) / segmentAngle;
    }

    private void drawSegment(Canvas canvas) {

//        canvas.save();
//        canvas.rotate(90 + drawItemIndex * segmentAngle + segmentAngle / 2);
//        canvas.translate(0, -1 * iconRadius);
//        canvas.drawBitmap(getBitmapFromAsset(drawItemIndex), null, iconRectF, null);
//        canvas.restore();

        drawSegmentLabel(canvas);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initDrawMetrics();
    }

//    private void clipPathAndDrawFromCache (Canvas canvas) {
//
//        Path path = new Path();
//        canvas.save();
//        canvas.rotate(-currentRotation, viewRadius, viewRadius);
//        int startAngle, sweepAngle;
//        if (prevRotation == -1) {
//            return;
//        } else if (currentRotation > prevRotation){
//            startAngle = 90 - currentRotation + prevRotation;
//            sweepAngle = currentRotation - prevRotation;
//            System.out.println("Drawing from cache ");
//            canvas.drawBitmap(Cache.getDrawingCache(),null,viewRect,paint);
//        } else {
//            startAngle = 0;
//            sweepAngle = prevRotation - currentRotation;
//            System.out.println("Drawing from cache ");
//            canvas.drawBitmap(Cache.getDrawingCache(),null,viewRect,paint);
//        }
//
//        path.arcTo(viewRect, startAngle, sweepAngle);
//        path.lineTo(viewRect.centerX(), viewRect.centerY());
//        path.close();
//        // canvas.clipPath(path);
//        canvas.restore();
//    }

    private Bitmap getBitmapFromAsset(int segmentId) {

        return defaultBitmap;
//        switch (segmentId) {
//            case 0:
//                return  BitmapFactory.decodeResource(getResources(),
//                        R.drawable.ic_invert_colors_white_36dp);
//            case 1:
//                return BitmapFactory.decodeResource(getResources(),
//                        R.drawable.ic_language_white_36dp);
//            case 2:
//                return BitmapFactory.decodeResource(getResources(),
//                        R.drawable.ic_opacity_white_36dp);
//            default:
//                return BitmapFactory.decodeResource(getResources(),
//                        R.drawable.ic_opacity_white_36dp);
//        }
    }

    private void drawSegmentLabel(Canvas canvas) {

        labelPath = new Path();
        // float startAngle = segmentId * segmentAngle;


        paint.setStyle(Paint.Style.STROKE);


        labelPath.arcTo(labelRectF, drawItemIndex * segmentAngle, segmentAngle);

        paint.setColor(Color.MAGENTA);
        //paint.setStyle(Paint.Style.FILL_AND_STROKE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            paint.setLetterSpacing(.15f);
        }


        paint.setTextAlign(Paint.Align.CENTER);

//        Rect r = new Rect();
//        paint.getTextBounds("Hello", 0, 5, r);
//        int l = r.centerY();


        // canvas.drawArc(labelRectF, startAngle, segmentAngle, false, paint);
        canvas.drawTextOnPath(getTextForSegment(drawItemIndex), labelPath, 0, labelOffset, paint);


    }

    private int getMaxLabelHeight() {

        int max = 0;
        Rect r;

        for (int i = 0; i < 5; i++) {
            r = new Rect();
            String text = getTextForSegment(i);
            paint.getTextBounds(text, 0, text.length(), r);
            max = Math.max(max, r.bottom - r.top);
        }

        return max;
    }

    private String getTextForSegment(int segmentId) {
        switch (segmentId) {
            case 0:
                return "SCIENCE";
            case 1:
                return "ADVENTURE";
            case 2:
                return "SPORTS";
            case 3:
                return "DRAMA";
            case 4:
                return "TECHNOLOGY";
            default:
                return "GENERAL";
        }
    }

    public void setRotation(int rotation) {
        // saveDrawingCache();
        this.currentRotation = rotation;
        invalidate();
    }

    @Override
    public void invalidate() {
        setDrawIndexes();
        super.invalidate();
    }

    //    private void saveDrawingCache() {
//        buildDrawingCache();
//
//        if (getDrawingCache() != null) {
//            Cache.setDrawingCache(Bitmap.createBitmap(getDrawingCache()));
//        } else {
//            System.out.println("no drawing cache " + isDrawingCacheEnabled());
//        }
//    }
//
//
//    static class Cache {
//
//        static Bitmap drawingCache;
//
//        public static Bitmap getDrawingCache() {
//            return drawingCache;
//        }
//
//        public static boolean hasDrawingCache() {
//            return drawingCache != null;
//        }
//
//        public static void setDrawingCache(Bitmap drawingCache) {
//            Cache.drawingCache = drawingCache;
//        }
//
//        public static void invalidate() {
//            if(Cache.hasDrawingCache()) {
//                drawingCache.recycle();
//                drawingCache = null;
//            }
//        }
//    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private float getRadius(float x, float y) {
            return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }


        @Override
        public boolean onDown(MotionEvent e) {
            float radius = getRadius(viewRadius - e.getX(), viewRadius - e.getY());
            if (radius < viewRadius && radius > viewInnerRadius) {

                if (isAnimationRunning()) {
                    stopScrolling();
                }
                return true;
            }

            return false;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float radius = getRadius(viewRadius - e2.getX(), viewRadius - e2.getY());
            double delta = Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2));


            float deltaAngle = (float) (Math.signum(velocityY) * Math.toDegrees(delta));


            if (radius < viewRadius && radius > viewInnerRadius) {

                mScroller.fling(
                        0,
                        currentRotation,
                        0,
                        (int) deltaAngle / 4,
                        0,
                        0,
                        minRotation,
                        maxRotation);

                // Start the animator and tell it to animate for the expected duration of the fling.
                int duration = mScroller.getDuration();

                mScrollAnimator.setDuration(duration);
                mScrollAnimator.start();

                //System.out.println("delta angle velocity " + (int) deltaAngle / 4 + "," + duration);
                return true;
            }

            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float radius = getRadius(viewRadius - e2.getX(), viewRadius - e2.getY());
            double delta = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));


            int deltaAngle = (int) (Math.signum(distanceX) * Math.toDegrees(delta));

            if (radius < viewRadius && radius > viewInnerRadius) {
                scrollRotation((int) (deltaAngle / radius));
                //System.out.println("delta angle " + deltaAngle + "," + distanceX + "," + distanceY);
                return true;
            }

            return false;
        }
    }

    private boolean isAnimationRunning() {
        return !mScroller.isFinished();
    }

    private void stopScrolling() {
        mScroller.forceFinished(true);
    }

    public static int dpToPx(int dp) {
        System.out.println("density " + Resources.getSystem().getDisplayMetrics().density);

        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
