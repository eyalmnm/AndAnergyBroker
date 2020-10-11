package com.usenergysolutions.energybroker.services.location

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Notification.PRIORITY_MIN
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.Nullable
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.usenergysolutions.energybroker.BuildConfig
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.config.Constants
import com.usenergysolutions.energybroker.config.Dynamic
import com.usenergysolutions.energybroker.model.outgoing.map.MyLocationModel
import com.usenergysolutions.energybroker.remote.ApiController
import com.usenergysolutions.energybroker.remote.MyLocationApi
import com.usenergysolutions.energybroker.utils.CalendarUtils
import com.usenergysolutions.energybroker.utils.PreferencesUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class LocationService : Service() {
    private val TAG: String = "LocationService"

    // Location Manager criteria
    private val providerGPS: String =
        LocationManager.GPS_PROVIDER  // The name of the provider with which we would like to register.
    private val minTime: Long = 2000    //  Minimum time interval between location updates (in milliseconds).
    private val minDistance: Float = 10F    // Minimum distance between location updates (in meters).
    private lateinit var listener: LocationListener // LocationListener whose onLocationChanged(Location) method will be called for each location update.

    // Time Format helper
    private val formatter: SimpleDateFormat =
        SimpleDateFormat("HH:mm:ss", Locale.US)  //  ("yyyy.MM.dd G 'at' hh:mm:ss a zzz");

    // Location components
    private var locationManager: LocationManager? = null
    private var location: Location? = null
    private lateinit var context: Context
    private lateinit var calendar: Calendar
    private var iterationsCounter = 0
    private var prevLocation: Location? = null
    private var gpsAvailability = false

    // Scheduler params
    private var scheduleTaskExecutor: ScheduledExecutorService? = null
    private var future: Future<*>? = null
    private var period = 5

    // Communication
    private var myLocationApi: MyLocationApi? = null

    private var timerRunnable: Runnable = Runnable {
        Log.d(TAG, "Looping every $period  seconds gpsAvailability: $gpsAvailability")
        location = getLastKnownLocation()
        if (null != location) {
            if (iterationsCounter > 24 && gpsAvailability) {
                period = 15
            }
            iterationsCounter++
            resetTimer()
        }
        sendBroadcastMessage(null != location)
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        var bestLocation: Location? = null

        try {
            val mLocationManager: LocationManager? =
                applicationContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val providers: List<String>? = mLocationManager?.getProviders(false)
            if (providers != null) {
                for (provider: String in providers) {
                    val l: Location = mLocationManager.getLastKnownLocation(provider) ?: continue
                    if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                        bestLocation = l
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "getLastKnownLocation", ex)
        }

        return bestLocation
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        isLocationEnabled()
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        context = this
        formatter.timeZone = TimeZone.getTimeZone("UTC")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //startForeground(1, new Notification());
            startForeground()
        }

        calendar = Calendar.getInstance()

        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        listener = object : LocationListener {

            override fun onLocationChanged(location: Location?) {
                Log.d(TAG, "onLocationChanged: ${location.toString()}")
                this@LocationService.location = location
                sendBroadcastMessage(true)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                val msg: String =
                    "onStatusChanged Provider $provider status changed to " + (if (status == LocationProvider.AVAILABLE) "" else "not ") + "available"
                Log.d(TAG, "onStatusChanged: $msg")
            }

            override fun onProviderEnabled(provider: String?) {
                val msg = "Provider $provider is available now"
                Log.d(TAG, "onProviderEnabled $msg")
                sendBroadcastMessage(true)
            }

            override fun onProviderDisabled(provider: String?) {
                val msg = "Provider $provider is NOT available"
                Log.d(TAG, "onProviderDisabled $msg")
                sendBroadcastMessage(false)
            }
        }
        try {
            locationManager?.requestLocationUpdates(providerGPS, minTime, minDistance, listener)
        } catch (ex: Exception) {
            Log.e(TAG, "requestLocationUpdates", ex)
        }
        initTimer()
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun isLocationEnabled() {
        var available = false
        if (locationManager != null) {
            available = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
        resetTimer()
        sendBroadcastMessage(available)
    }

    private fun sendBroadcastMessage(isAvailable: Boolean) {
        var action: String = Constants.LOCATION_NOT_AVAILABLE
        if (isAvailable) action = Constants.LOCATION_AVAILABLE
        gpsAvailability = isAvailable
        Log.d(TAG, "Transmit location action: $action")

        val intent = Intent(action)
        if (location != null) {
            val latitude: Double = location!!.latitude
            val longitude: Double = location!!.longitude
            val time: Long = location!!.time
            intent.putExtra(Constants.NAME_LATITUDE_DATA, latitude)
            intent.putExtra(Constants.NAME_LONGITUDE_DATA, longitude)
            intent.putExtra(Constants.NAME_SAMPLE_TIME, time)

            if (minDistance < getDistance(location!!, prevLocation) && location!!.speed != 0f) {
                // sendMyLocationSilently(latitude, longitude, time);
                sendMyLocationSilently(latitude, longitude)
                prevLocation = location
            }
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    private fun sendMyLocationSilently(latitude: Double, longitude: Double) {
        Log.d(TAG, "sendMyLocationSilently")
        if (isWorkingTime(CalendarUtils.getCurrentLocalTimeLong())) {
            if (myLocationApi == null) {
                myLocationApi = ApiController.createService(MyLocationApi::class.java)
            }
            var uuid: String? = Dynamic.uuid
            if (uuid == null) uuid = PreferencesUtils.getInstance(context).offLineUuid
            myLocationApi?.updateMyPosition(
                MyLocationModel(
                    uuid!!,
                    latitude,
                    longitude,
                    CalendarUtils.getCurrentLocalTimeString()
                )
            )?.enqueue(object : Callback<Boolean> {
                override fun onFailure(call: Call<Boolean>, t: Throwable) {

                }

                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {

                }
            })
        }
    }

    private fun startForeground() {
        var channelId = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("my_service", "My Background Service")
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.energybroker)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    private fun getDistance(location: Location, prevLocation: Location?): Float {
        return if (null == prevLocation) {
            java.lang.Float.MAX_VALUE
        } else {
            val results = FloatArray(1)
            Location.distanceBetween(
                prevLocation.latitude, prevLocation.longitude,
                location.latitude, location.longitude, results
            )
            Log.d(TAG, "getDistance distance: " + results[0])
            results[0]
        }
    }

    // TODO Do it more generic calculation regarding to real rules
    private fun isWorkingTime(time: Long): Boolean {
        if (BuildConfig.DEBUG) {
            return true
        }
        calendar.timeInMillis = time
        return if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            false
        } else calendar.get(Calendar.HOUR_OF_DAY) in 8..17
    }

    private fun initTimer() {
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5)
        future = scheduleTaskExecutor?.scheduleAtFixedRate(timerRunnable, 0, period.toLong(), TimeUnit.SECONDS)
    }

    private fun resetTimer() {
        future?.cancel(true)
        future =
            scheduleTaskExecutor?.scheduleAtFixedRate(timerRunnable, period.toLong(), period.toLong(), TimeUnit.SECONDS)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != locationManager) {
            locationManager?.removeUpdates(listener)
        }
        scheduleTaskExecutor?.shutdown()
        Log.d(TAG, "onDestroy")
    }
}