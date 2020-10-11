package com.usenergysolutions.energybroker.remote

import com.usenergysolutions.energybroker.BuildConfig
import com.usenergysolutions.energybroker.model.incoming.user.GetDailyReportResponse
import com.usenergysolutions.energybroker.model.incoming.user.GetProfileResponse
import com.usenergysolutions.energybroker.model.incoming.user.LoginResponse
import com.usenergysolutions.energybroker.model.incoming.user.UploadFileResponse
import com.usenergysolutions.energybroker.model.outgoing.user.GetForDate
import com.usenergysolutions.energybroker.model.outgoing.user.LoginCredentials
import com.usenergysolutions.energybroker.model.outgoing.user.MyUuid
import com.usenergysolutions.energybroker.model.outgoing.user.UploadFile
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserApi {

    @Headers(
        "bearer: ${BuildConfig.APP_TOKEN}",
        "Content-Type: application/json"
    )

    @POST("login.php")
    fun login(@Body data: LoginCredentials): Call<LoginResponse>

    @POST("getProfile.php")
    fun getProfile(@Header("bearer") bearer: String, @Body myUuid: MyUuid): Call<GetProfileResponse>

    @POST("uploadProfileImage.php")
    fun uploadImage(
        @Header("bearer") bearer: String, @Body uploadFile: UploadFile
    ): Call<UploadFileResponse>

    @POST("getDailyReport.php")
    //@FormUrlEncoded
    fun getDailyReport(@Header("bearer") bearer: String, @Body getForDate: GetForDate): Call<GetDailyReportResponse>
}