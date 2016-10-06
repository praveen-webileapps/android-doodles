package com.android.mydoodles;

import android.content.res.Resources;

/**
 * Created by PraveenKatha on 06/10/16.
 */
public class MyViewUtils {

    public static int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
