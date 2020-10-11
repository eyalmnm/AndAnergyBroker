package com.usenergysolutions.energybroker.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

class GradleUtil {
    companion object {

        fun getAppVersion(context: Context): String? {
            var appVersion: PackageInfo? = null

            try {
                appVersion = context.packageManager.getPackageInfo(context.packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return appVersion?.versionName
        }


        fun getAppVersionCode(context: Context): Int? {
            var appVersion: PackageInfo? = null

            try {
                appVersion = context.packageManager.getPackageInfo(context.packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return appVersion?.versionCode
        }
    }

}