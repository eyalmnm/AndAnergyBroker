package com.usenergysolutions.energybroker.remote

import com.usenergysolutions.energybroker.BuildConfig
import com.usenergysolutions.energybroker.model.outgoing.map.MyLocationModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MyLocationApi {

    @Headers(
        "bearer: ${BuildConfig.APP_TOKEN}",
        "Content-Type: application/json"
    )

    @POST("updateMyLocation.php")
    fun updateMyPosition(@Body myLocation: MyLocationModel): Call<Boolean>
}