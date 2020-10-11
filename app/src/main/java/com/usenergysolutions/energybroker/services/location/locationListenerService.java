package com.usenergysolutions.energybroker.services.location;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.usenergysolutions.energybroker.BuildConfig;
import com.usenergysolutions.energybroker.R;
import com.usenergysolutions.energybroker.config.Constants;
import com.usenergysolutions.energybroker.config.Dynamic;
import com.usenergysolutions.energybroker.model.outgoing.map.MyLocationModel;
import com.usenergysolutions.energybroker.remote.ApiController;
import com.usenergysolutions.energybroker.remote.MyLocationApi;
import com.usenergysolutions.energybroker.utils.CalendarUtils;
import com.usenergysolutions.energybroker.utils.PreferencesUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;


// Ref: https://stackoverflow.com/questions/20371113/android-gps-measured-distance-between-locations-continiously


public class locationListenerService extends Service {
    private static final String TAG = "locationListenerService";

    // Location Manager criteria
    private final String providerGPS = LocationManager.GPS_PROVIDER; // The name of the provider with which we would like to register.
    private final long minTime = 2000; //  Minimum time interval between location updates (in milliseconds).
    private final float minDistance = 10; // Minimum distance between location updates (in meters).
    public LocationListener listener = null; // LocationListener whose onLocationChanged(Location) method will be called for each location update.

    // Time Format
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);  //  ("yyyy.MM.dd G 'at' hh:mm:ss a zzz");

    // Location components
    private LocationManager locationManager;
    private Location location;
    private Context context;
    private Calendar calendar;
    private int iterationsCounter = 0;

    // Scheduler params
    private ScheduledExecutorService scheduleTaskExecutor;
    private Future<?> future;
    private int period = 5;
    private Location prevLocation;
    private boolean gpsAvailability = false;

    // Communication
    private MyLocationApi myLocationApi = null;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Looping every " + period + " seconds gpsAvailability: " + gpsAvailability);
            location = getLastKnownLocation();
            if (null != location) {
                if (iterationsCounter > 24 && gpsAvailability) {
                    period = 15;
                }
                iterationsCounter++;
                resetTimer();
            }
            sendBroadcastMessage(null != location);
        }
    };

    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {
        Location bestLocation = null;
        try {
            LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(false);
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "getLastKnownLocation", ex);
        }
        return bestLocation;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        isLocationEnabled();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        context = this;
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //startForeground(1, new Notification());
            startForeground();
        }

        calendar = Calendar.getInstance();

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: " + location.toString());
                locationListenerService.this.location = location;
                sendBroadcastMessage(true);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                String msg = "onStatusChanged Provider " + s + " status changed to " + (i == LocationProvider.AVAILABLE ? "" : "not ") + "available";
                Log.d(TAG, "onStatusChanged: " + msg);
            }

            @Override
            public void onProviderEnabled(String s) {
                sendBroadcastMessage(true);
                String msg = "Provider " + s + " is available now";
                Log.d(TAG, "onProviderEnabled " + msg);
            }

            @Override
            public void onProviderDisabled(String s) {
                sendBroadcastMessage(false);
                String msg = "Provider " + s + " is NOT available";
                Log.d(TAG, "onProviderDisabled " + msg);
            }
        };
        try {
            locationManager.requestLocationUpdates(providerGPS, minTime, minDistance, listener);
        } catch (SecurityException ex) {
            Log.e(TAG, "requestLocationUpdates", ex);
        }
        initTimer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void isLocationEnabled() {
        boolean available = false;
        if (null != locationManager) {
            available = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        resetTimer();
        sendBroadcastMessage(available);
    }

    private void sendBroadcastMessage(boolean isAvailable) {
        String action = isAvailable ? Constants.LOCATION_AVAILABLE : Constants.LOCATION_NOT_AVAILABLE;
        gpsAvailability = isAvailable;
        Log.d(TAG, "Transmit location action: " + action);
        Intent intent = new Intent(action);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            long time = location.getTime();
            Log.d(TAG, "sendBroadcastMessage speed: " + location.getSpeed());
            Log.d(TAG, "sendBroadcastMessage location lat: " + latitude + " lng: " + longitude);
            Log.d(TAG, "sendBroadcastMessage time: " + formatter.format(time));
//            Toast.makeText(context, "Speed: " + location.getSpeed(), Toast.LENGTH_SHORT).show();
            intent.putExtra(Constants.NAME_LATITUDE_DATA, latitude);
            intent.putExtra(Constants.NAME_LONGITUDE_DATA, longitude);
            intent.putExtra(Constants.NAME_SAMPLE_TIME, time);
            if ((minDistance < getDistance(location, prevLocation)) && (location.getSpeed() != 0 || BuildConfig.DEBUG)) { // TODO Change the speed criteria
//                sendMyLocationSilently(latitude, longitude, time);
                sendMyLocationSilently(latitude, longitude);
                prevLocation = location;
            }
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // Saved for backup till the final decision
//    private void sendMyLocationSilently(double latitude, double longitude, long timeSample) {
//        Log.d(TAG, "sendMyLocationSilently");
//        System.out.println(TAG + " " + " sendMyLocationSilently");
//        if (isWorkingTime(CalendarUtils.getCurrentLocalTimeLong())) {
//            Intent intent = new Intent(context, MyLocationTransmitterService.class);
//            intent.putExtra(Constants.NAME_LATITUDE_DATA, latitude);
//            intent.putExtra(Constants.NAME_LONGITUDE_DATA, longitude);
//            intent.putExtra(Constants.NAME_SAMPLE_TIME, timeSample);
//            startService(intent);
//        }
//    }

    private void sendMyLocationSilently(double latitude, double longitude) {
        Log.d(TAG, "sendMyLocationSilently");
        System.out.println(TAG + " " + " sendMyLocationSilently");
        if (isWorkingTime(CalendarUtils.getCurrentLocalTimeLong())) {
            if (myLocationApi == null) {
                myLocationApi = ApiController.Companion.createService(MyLocationApi.class);
            }
            String uuid = Dynamic.Companion.getUuid() != null ? Dynamic.Companion.getUuid() : PreferencesUtils.getInstance(getApplicationContext()).getOffLineUuid();
            myLocationApi.updateMyPosition(new MyLocationModel(uuid, latitude, longitude, CalendarUtils.getCurrentLocalTimeString())).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                    Log.d(TAG, "onResponse: " + response.body());
                }

                @Override
                public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure call: " + call.toString(), t);
                }
            });
        }
    }

    private void startForeground() {
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("my_service", "My Background Service");
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.energybroker)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(101, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    private float getDistance(Location location, Location prevLocation) {
        if (null == prevLocation) {
            return Float.MAX_VALUE;
        } else {
            float[] results = new float[1];
            Location.distanceBetween(
                    prevLocation.getLatitude(), prevLocation.getLongitude(),
                    location.getLatitude(), location.getLongitude(), results);
            Log.d(TAG, "getDistance distance: " + results[0]);
            return results[0];
        }
    }

    // TODO Do it more generic calculation regarding to real rules
    private boolean isWorkingTime(long time) {
        if (BuildConfig.DEBUG) {
            return true;
        }
        calendar.setTimeInMillis(time);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return false;
        }
        return calendar.get(Calendar.HOUR_OF_DAY) <= 17 &&
                calendar.get(Calendar.HOUR_OF_DAY) >= 8;
    }

    private void initTimer() {
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        future = scheduleTaskExecutor.scheduleAtFixedRate(timerRunnable, 0, period, TimeUnit.SECONDS);
    }

    private void resetTimer() {
        future.cancel(true);
        future = scheduleTaskExecutor.scheduleAtFixedRate(timerRunnable, period, period, TimeUnit.SECONDS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != locationManager) {
            locationManager.removeUpdates(listener);
        }
        scheduleTaskExecutor.shutdown();
        Log.d(TAG, "onDestroy");
    }
}
