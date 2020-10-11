package com.usenergysolutions.energybroker.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Retries the data from the App's Build.Gradle
 */
public class AppUtils {

    public static String getAppVersion(Context context) {
        android.content.pm.PackageInfo appVersion = null;

        try {
            appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (appVersion != null)
            return appVersion.versionName;
        else
            return "";
    }

    public static int getAppVersionCode(Context context) {
        android.content.pm.PackageInfo appVersion = null;

        try {
            appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (appVersion != null)
            return appVersion.versionCode;
        else
            return 0;

    }
}
