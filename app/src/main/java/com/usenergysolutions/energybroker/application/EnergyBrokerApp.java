package com.usenergysolutions.energybroker.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.BuildConfig;
import io.fabric.sdk.android.Fabric;

import java.io.File;

public class EnergyBrokerApp extends Application {
    private static final String TAG = "EnergyBrokerApp";

    // Hold instance
    private static EnergyBrokerApp instance;

    public static EnergyBrokerApp getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return EnergyBrokerApp.instance.getApplicationContext();
    }

    public static File getCacheDirectory() {
        return instance.getCacheDir();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG == false) {
            Fabric.with(this, new Crashlytics());
        } else {
            Log.d(TAG, "Fabric NOT initiated");
        }

        instance = this;
    }
}
