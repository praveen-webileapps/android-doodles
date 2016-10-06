package com.android.mydoodles;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DemoActivity extends Activity {

    float BASE_FONT = 40f;
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setCircularViewDemo();
        //setCirclularLayout();
        //setAnimationDemo();
        //setCircularTextView();
        // setCircularTextLayout();
        setFilterLayout();
    }




    private void setFilterLayout () {
        setContentView(R.layout.activity_filter_layout);

        typeface = Typeface.createFromAsset(getResources().getAssets(), "BalooThambi-Regular.ttf");

        CircularTextLayout paidFilterLayouts = (CircularTextLayout) findViewById(R.id.paidFilterLayout);
        CircularTextLayout.Adapter adapterOne = getAdapter(new String[] {"Paid", "Unpaid"});
        paidFilterLayouts.setAdapter(adapterOne);

        CircularTextLayout streamStatusFilterLayout = (CircularTextLayout) findViewById(R.id.statusFilterLayout);
        CircularTextLayout.Adapter adapterTwo = getAdapter(new String[] {"Live","Recorded", "Scheduled"});
        streamStatusFilterLayout.setAdapter(adapterTwo);

        float fontOne = CircularTextLayout.getRecommendedFont(adapterOne, typeface, paidFilterLayouts.getLayoutParams().height);
        float fontTwo = CircularTextLayout.getRecommendedFont(adapterTwo, typeface, streamStatusFilterLayout.getLayoutParams().height);
        float fontSize = Math.min(fontOne,fontTwo);

        paidFilterLayouts.setFontSize(fontSize);
        streamStatusFilterLayout.setFontSize(fontSize);

    }


    private CircularTextLayout.Adapter getAdapter (final String[] texts) {

        return new CircularTextLayout.Adapter() {

            @Override
            public String getText(int index) {
                return texts[index];
            }

            @Override
            public View.OnClickListener getOnClickListener(int index) {
                return null;
            }

            @Override
            public int getCount() {
                return texts.length;
            }
        };
    }

    private void setCircularTextView() {
        setContentView(R.layout.activity_circular_tv_layout);
        CircularTextView tv = (CircularTextView) findViewById(R.id.tv);
    }


    private void setCircularTextLayout() {
        setContentView(R.layout.activity_circular_tv_list_layout);
        CircularTextLayout layout = (CircularTextLayout) findViewById(R.id.layout);
        final String[] array = {"Adventure", "Science", "Cool"};

        layout.setAdapter(new CircularTextLayout.Adapter() {
            @Override
            public String getText(int index) {
                return array[index];
            }

            @Override
            public View.OnClickListener getOnClickListener(final int index) {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                };
            }

            @Override
            public int getCount() {
                return array.length;
            }
        });
    }


    public static int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }




    private float getFontSize(float width, Rect r) {
        int textWidth = r.right - r.left;
        return width / textWidth * BASE_FONT;
    }

    private Rect getTextBounds(String text, float font) {

        Paint p = new Paint();

        p.setStyle(Paint.Style.STROKE);
        p.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "BalooThambi-Regular.ttf"));
        p.setTextSize(font);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            p.setLetterSpacing(.15f);
        }

        Rect r = new Rect();
        p.getTextBounds(text, 0, text.length(), r);
        return r;
    }


    private void setCirclularLayout() {
        setContentView(R.layout.activity_circular_layout);
        View circle = findViewById(R.id.circular_layout);
//        circle.setPivotX(0);
//        circle.setPivotY(0);

//        circle.setTranslationX(circle.getWidth());
//        circle.setTranslationY(circle.getHeight());

    }

    public void animate(View v) {

        final View circle = findViewById(R.id.circular_layout);

        int tx = circle.getScaleX() == 0 ? 1 : 0;
        int ty = circle.getScaleY() == 0 ? 1 : 0;

        //circle.setVisibility(View.VISIBLE);

        circle.animate().scaleX(tx).scaleY(ty).start();
    }

    private void setCircularViewDemo() {
        setContentView(R.layout.activity_demo);

        //initCircularView();

    }

    private void initCircularView() {
        //final CircularView circle = (CircularView) findViewById(circle);

        //circle.setBackgroundColor(Color.WHITE);

        TextView tv = (TextView) findViewById(R.id.cool);
        tv.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "BalooBhai-Regular.ttf"));
    }

    private void setAnimationDemo() {


        setContentView(R.layout.activity_demo_animation);

        final LinearLayout layout = (LinearLayout) findViewById(R.id.layout);

        LayoutTransition transition = new LayoutTransition();


        final Button btn1 = (Button) findViewById(R.id.btn1);
        final Button btn2 = (Button) findViewById(R.id.btn2);
        final Button btn3 = (Button) findViewById(R.id.btn3);

        final Button btn = (Button) findViewById(R.id.button);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn3.setVisibility(View.VISIBLE);
            }
        });
        //circle.setBackgroundColor(Color.WHITE);


    }
}
