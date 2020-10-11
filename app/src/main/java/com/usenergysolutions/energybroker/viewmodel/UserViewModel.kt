package com.usenergysolutions.energybroker.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.usenergysolutions.energybroker.BuildConfig
import com.usenergysolutions.energybroker.config.Dynamic
import com.usenergysolutions.energybroker.model.DailyReportModel
import com.usenergysolutions.energybroker.model.DataWrapper
import com.usenergysolutions.energybroker.model.UserModel
import com.usenergysolutions.energybroker.model.incoming.user.GetDailyReportResponse
import com.usenergysolutions.energybroker.model.incoming.user.GetProfileResponse
import com.usenergysolutions.energybroker.model.incoming.user.LoginResponse
import com.usenergysolutions.energybroker.model.incoming.user.UploadFileResponse
import com.usenergysolutions.energybroker.model.outgoing.user.GetForDate
import com.usenergysolutions.energybroker.model.outgoing.user.LoginCredentials
import com.usenergysolutions.energybroker.model.outgoing.user.MyUuid
import com.usenergysolutions.energybroker.model.outgoing.user.UploadFile
import com.usenergysolutions.energybroker.remote.ApiController
import com.usenergysolutions.energybroker.remote.UserApi
import com.usenergysolutions.energybroker.utils.FileUtils
import com.usenergysolutions.energybroker.utils.RetrofitUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

// Ref: https://stackoverflow.com/questions/21306720/uploading-image-from-android-to-php-server

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = "UserViewModel"

    private var userApi: UserApi? = null

    init {
        userApi = ApiController.createService(UserApi::class.java)
    }


    fun login(email: String, password: String): LiveData<DataWrapper<LoginResponse>> {
        val liveData: MutableLiveData<DataWrapper<LoginResponse>> = MutableLiveData()
        val dataWrapper: DataWrapper<LoginResponse> = DataWrapper()

        userApi?.login(LoginCredentials(email, password))?.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG, "login -> onFailure", t)
                dataWrapper.throwable = t
                liveData.value = dataWrapper
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d(TAG, "login -> onResponse")
                if (response.isSuccessful) {
                    dataWrapper.data = response.body()
                } else {
                    dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                }
                liveData.value = dataWrapper
            }
        })
        return liveData
    }

    fun getMyProfileData(): LiveData<DataWrapper<UserModel>> {
        val liveData: MutableLiveData<DataWrapper<UserModel>> = MutableLiveData()
        val dataWrapper: DataWrapper<UserModel> = DataWrapper()

        userApi?.getProfile(BuildConfig.APP_TOKEN, MyUuid(Dynamic.uuid!!))
            ?.enqueue(object : Callback<GetProfileResponse> {
                override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                    dataWrapper.throwable = t
                    liveData.value = dataWrapper
                }

                override fun onResponse(call: Call<GetProfileResponse>, response: Response<GetProfileResponse>) {
                    if (response.isSuccessful) {
                        if (response.body()?.error == true) {
                            dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                        } else {
                            dataWrapper.data = response.body()?.profile
                        }
                    } else {
                        dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                    }
                    liveData.value = dataWrapper
                }
            })

        return liveData
    }

    fun uploadFile(file: File): LiveData<DataWrapper<String>> {  // Return the avatar uri
        val liveData: MutableLiveData<DataWrapper<String>> = MutableLiveData()
        val dataWrapper: DataWrapper<String> = DataWrapper()

        val imageBase64 = FileUtils.getStringFile(file)

        userApi?.uploadImage(BuildConfig.APP_TOKEN, UploadFile(Dynamic.uuid!!, file.name, imageBase64))
            ?.enqueue(object : Callback<UploadFileResponse> {
                override fun onFailure(call: Call<UploadFileResponse>, t: Throwable) {
                    dataWrapper.throwable = t
                    liveData.value = dataWrapper
                }

                override fun onResponse(call: Call<UploadFileResponse>, response: Response<UploadFileResponse>) {
                    if (response.isSuccessful) {
                        if (response.body()?.error == true) {
                            dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                        } else {
                            dataWrapper.data = response.body()?.user?.avatar
                        }
                    } else {
                        dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                    }
                    liveData.value = dataWrapper
                }
            })

        return liveData
    }

    fun getDailyReport(date: String): LiveData<DataWrapper<DailyReportModel>> {
        val liveData: MutableLiveData<DataWrapper<DailyReportModel>> = MutableLiveData()
        val dataWrapper: DataWrapper<DailyReportModel> = DataWrapper()

        userApi?.getDailyReport(BuildConfig.APP_TOKEN, GetForDate(Dynamic.uuid!!, date))
            ?.enqueue(object : Callback<GetDailyReportResponse> {
                override fun onFailure(call: Call<GetDailyReportResponse>, t: Throwable) {
                    dataWrapper.throwable = t
                    liveData.value = dataWrapper
                }

                override fun onResponse(
                    call: Call<GetDailyReportResponse>,
                    response: Response<GetDailyReportResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.error == true) {
                            dataWrapper.throwable = Throwable(response.body()?.errorMsg)
                        } else {
                            dataWrapper.data = response.body()?.report
                        }
                    } else {
                        dataWrapper.throwable = Throwable(RetrofitUtils.handleErrorResponse(response))
                    }
                    liveData.value = dataWrapper
                }
            })

        return liveData
    }

    //    fun register(name: String, username: String, email: String, password: String): LiveData<DataWrapper<String>> {
//        val liveData: MutableLiveData<DataWrapper<String>> = MutableLiveData()
//        val dataWrapper: DataWrapper<String> = DataWrapper()
//        Communicator.getInstance().signUp(name, username, password, email, object : CommListener {
//            override fun newDataArrived(response: String?) {
//                val jsonObject = JSONObject(response)
//                if (JSONUtils.getBooleanValue(jsonObject, Constants.ERROR)) {
//                    dataWrapper.throwable = Throwable(JSONUtils.getStringValue(jsonObject, Constants.ERROR_MSG))
//                } else {
//                    val userObj: JSONObject = JSONUtils.getJSONObjectValue(jsonObject, Constants.USER)
//                    val uuid: String = JSONUtils.getStringValue(userObj, Constants.UUID)
//                    dataWrapper.data = uuid
//                }
//                liveData.postValue(dataWrapper) // liveData.value = dataWrapper
//            }
//
//            override fun exceptionThrown(throwable: Throwable?) {
//                dataWrapper.throwable = throwable
//                liveData.postValue(dataWrapper) // liveData.value = dataWrapper
//            }
//        })
//
//        return liveData
//    }

}