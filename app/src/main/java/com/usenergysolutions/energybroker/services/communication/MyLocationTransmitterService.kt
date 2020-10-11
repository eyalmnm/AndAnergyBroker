package com.usenergysolutions.energybroker.services.communication

import android.app.IntentService
import android.content.Intent
import android.util.Log
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

class MyLocationTransmitterService : IntentService("MyLocationTransmitterService") {
    private val TAG: String = "MyLocationTransmitter"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onHandleIntent(intent: Intent?) {
        val lat = intent?.getDoubleExtra(Constants.NAME_LATITUDE_DATA, 0.0)
        val lng = intent?.getDoubleExtra(Constants.NAME_LONGITUDE_DATA, 0.0)
        if (lat != null && lng != null) {
            sendMyLocationSilently(lat, lng)
        }
    }

    private fun sendMyLocationSilently(latitude: Double, longitude: Double) {
        Log.d(TAG, "sendMyLocationSilently")
        println("$TAG  sendMyLocationSilently")
        val myLocationApi: MyLocationApi = ApiController.createService(MyLocationApi::class.java)
        val uuid =
            if (Dynamic.uuid != null) Dynamic.uuid else PreferencesUtils.getInstance(applicationContext).offLineUuid
        myLocationApi.updateMyPosition(
            MyLocationModel(
                uuid!!,
                latitude,
                longitude,
                CalendarUtils.getCurrentLocalTimeString()
            )
        ).enqueue(object :
            Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                // Log.d(TAG, "onResponse: " + response.body());
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                // Log.e(TAG, "onFailure call: " + call.toString(), t);
            }
        })
    }

}