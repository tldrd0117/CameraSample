package com.sungjae.cameraapp.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by iseongjae on 2016. 10. 23..
 */

public class SizeUtil {
    public static float getPxFromDp( int dp, Context context ) {
        return TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics() );
    }

    public static int getSoftButtonsBarHeight( Activity activity ) {
        // getRealMetrics is only available with API 17 and +
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics( metrics );
            int usableHeight = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics( metrics );
            int realHeight = metrics.heightPixels;
            if ( realHeight > usableHeight ) {
                return realHeight - usableHeight;
            }
            else {
                return 0;
            }
        }
        return 0;
    }
}
