package com.usenergysolutions.energybroker.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log

class AlarmManagerHelper {
    companion object {
        const val TAG: String = "AlarmManagerHelper"

        // Intervals Calculation
        private const val INTERVAL_ONE_SECOND: Long = 1000
        private const val INTERVAL_ONE_MINUTE: Long = 60 * INTERVAL_ONE_SECOND
        private const val INTERVAL_TWO_MINUTES: Long = 2 * INTERVAL_ONE_MINUTE
        private const val INTERVAL_FIFTEEN_MINUTES: Long = AlarmManager.INTERVAL_FIFTEEN_MINUTES
        private const val INTERVAL_HALF_HOUR: Long = AlarmManager.INTERVAL_HALF_HOUR

        // TODO Check what is the best method for running the Location Service clock
        fun registerAlert(context: Context, intent: Intent, minutesInterval: Int) {
            Log.d(TAG, "registerAlert")

            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent: PendingIntent = PendingIntent.getService(context, 0, intent, 0)

            // Interval
//            alarmManager.setInexactRepeating(
//                AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() + INTERVAL_TWO_MINUTES,
//                INTERVAL_TWO_MINUTES,
//                pendingIntent
//            )

            // One Time
            alarmManager.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + (minutesInterval * INTERVAL_ONE_MINUTE),
                pendingIntent
            )
        }

        fun unRegisterAlert(context: Context, intent: Intent) {
            Log.d(TAG, "unRegisterAlert")

            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent: PendingIntent = PendingIntent.getService(context, 0, intent, 0)

            alarmManager.cancel(pendingIntent)
        }
    }
}