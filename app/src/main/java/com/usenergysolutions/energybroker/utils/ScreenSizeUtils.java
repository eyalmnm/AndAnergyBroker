package com.usenergysolutions.energybroker.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class ScreenSizeUtils {

    public static int differentDensityAndScreenSize(Context context) {
        int value = 20;
        String str = "";
        if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            switch (context.getResources().getDisplayMetrics().densityDpi) {
                case DisplayMetrics.DENSITY_LOW:
                    str = "small-ldpi";
                    value = 20;
                    break;
                case DisplayMetrics.DENSITY_MEDIUM:
                    str = "small-mdpi";
                    value = 20;
                    break;
                case DisplayMetrics.DENSITY_HIGH:
                    str = "small-hdpi";
                    value = 20;
                    break;
                case DisplayMetrics.DENSITY_XHIGH:
                    str = "small-xhdpi";
                    value = 20;
                    break;
                case DisplayMetrics.DENSITY_XXHIGH:
                    str = "small-xxhdpi";
                    value = 20;
                    break;
                case DisplayMetrics.DENSITY_XXXHIGH:
                    str = "small-xxxhdpi";
                    value = 20;
                    break;
                case DisplayMetrics.DENSITY_TV:
                    str = "small-tvdpi";
                    value = 20;
                    break;
                default:
                    str = "small-unknown";
                    value = 20;
                    break;
            }

        } else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            switch (context.getResources().getDisplayMetrics().densityDpi) {
                case DisplayMetrics.DENSITY_LOW:
                    str = "normal-ldpi";
                    value = 82;
                    break;
                case DisplayMetrics.DENSITY_MEDIUM:
                    str = "normal-mdpi";
                    value = 82;
                    break;
                case DisplayMetrics.DENSITY_HIGH:
                    str = "normal-hdpi";
                    value = 82;
                    break;
                case DisplayMetrics.DENSITY_XHIGH:
                    str = "normal-xhdpi";
                    value = 90;
                    break;
                case DisplayMetrics.DENSITY_XXHIGH:
                    str = "normal-xxhdpi";
                    value = 96;
                    break;
                case DisplayMetrics.DENSITY_XXXHIGH:
                    str = "normal-xxxhdpi";
                    value = 96;
                    break;
                case DisplayMetrics.DENSITY_TV:
                    str = "normal-tvdpi";
                    value = 96;
                    break;
                default:
                    str = "normal-unknown";
                    value = 82;
                    break;
            }
        } else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            switch (context.getResources().getDisplayMetrics().densityDpi) {
                case DisplayMetrics.DENSITY_LOW:
                    str = "large-ldpi";
                    value = 78;
                    break;
                case DisplayMetrics.DENSITY_MEDIUM:
                    str = "large-mdpi";
                    value = 78;
                    break;
                case DisplayMetrics.DENSITY_HIGH:
                    str = "large-hdpi";
                    value = 78;
                    break;
                case DisplayMetrics.DENSITY_XHIGH:
                    str = "large-xhdpi";
                    value = 125;
                    break;
                case DisplayMetrics.DENSITY_XXHIGH:
                    str = "large-xxhdpi";
                    value = 125;
                    break;
                case DisplayMetrics.DENSITY_XXXHIGH:
                    str = "large-xxxhdpi";
                    value = 125;
                    break;
                case DisplayMetrics.DENSITY_TV:
                    str = "large-tvdpi";
                    value = 125;
                    break;
                default:
                    str = "large-unknown";
                    value = 78;
                    break;
            }

        } else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            switch (context.getResources().getDisplayMetrics().densityDpi) {
                case DisplayMetrics.DENSITY_LOW:
                    str = "xlarge-ldpi";
                    value = 125;
                    break;
                case DisplayMetrics.DENSITY_MEDIUM:
                    str = "xlarge-mdpi";
                    value = 125;
                    break;
                case DisplayMetrics.DENSITY_HIGH:
                    str = "xlarge-hdpi";
                    value = 125;
                    break;
                case DisplayMetrics.DENSITY_XHIGH:
                    str = "xlarge-xhdpi";
                    value = 125;
                    break;
                case DisplayMetrics.DENSITY_XXHIGH:
                    str = "xlarge-xxhdpi";
                    value = 125;
                    break;
                case DisplayMetrics.DENSITY_XXXHIGH:
                    str = "xlarge-xxxhdpi";
                    value = 125;
                    break;
                case DisplayMetrics.DENSITY_TV:
                    str = "xlarge-tvdpi";
                    value = 125;
                    break;
                default:
                    str = "xlarge-unknown";
                    value = 125;
                    break;
            }
        }
        Toast.makeText(context, "" + str, Toast.LENGTH_LONG).show();

        return value;
    }
}
