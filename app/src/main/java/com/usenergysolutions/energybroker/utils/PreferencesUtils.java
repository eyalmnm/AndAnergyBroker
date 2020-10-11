package com.usenergysolutions.energybroker.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtils {

    private static final String TAG = "PreferencesUtils";
    // Shared preferences file name
    private static final String PREF_NAME = "energy_broker";
    private static PreferencesUtils instance = null;
    // Shared preferences access components
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    // Application context
    private Context context;
    // Shared preferences working mode
    private int PRIVATE_MODE = 0;

    // Connection properties
    private String serverIp = null;
    private int serverPort = -1;

    private PreferencesUtils(Context context) {
        this.context = context;
        preferences = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public static PreferencesUtils getInstance(Context context) {
        if (null == instance) {
            instance = new PreferencesUtils(context);
        }
        return instance;
    }

    public String getUUID() {
        return preferences.getString("uuid", null);
    }

    public void setUUID(String uuid) throws Exception {
        if (!StringUtils.isNullOrEmpty(uuid)) {
            editor.putString("uuid", uuid);
            editor.commit();
            setOffLineUuid(uuid);
        } else {
            throw new Exception("Invalid UUID");
        }
    }

    public void removeUUID() {
        editor.remove("uuid");
        editor.commit();
    }

    public String getOffLineUuid() {
        return preferences.getString("offLineUUID", null);
    }

    public void setOffLineUuid(String uuid) throws Exception {
        if (!StringUtils.isNullOrEmpty(uuid)) {
            editor.putString("offLineUUID", uuid);
            editor.commit();
        } else {
            throw new Exception("Invalid UUID");
        }
    }

    public boolean isClockIn() {
        return preferences.getBoolean("clockIn", false);
    }

    public void setClockIn(boolean clockIn) {
        editor.putBoolean("clockIn", clockIn);
        editor.commit();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        editor = null;
        preferences = null;
    }
}
