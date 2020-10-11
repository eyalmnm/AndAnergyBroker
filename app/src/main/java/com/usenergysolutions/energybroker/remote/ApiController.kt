package com.usenergysolutions.energybroker.remote

//import retrofit2.converter.moshi.MoshiConverterFactory
import android.util.Log
import com.google.gson.GsonBuilder
import com.usenergysolutions.energybroker.BuildConfig
import com.usenergysolutions.energybroker.application.EnergyBrokerApp
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

//  Ref: https://stackoverflow.com/questions/39918814/use-jsonreader-setlenienttrue-to-accept-malformed-json-at-line-1-column-1-path

class ApiController {
    companion object {
        private const val TAG = "ApiController"

        private const val DISK_CACHE_SIZE: Long = 10 * 1024 * 1024 // 10MB

        private const val BASE_URL: String = BuildConfig.SERVER_URL
        //private val httpClient: OkHttpClient? = OkHttpClient.Builder()
        private val httpClient: OkHttpClient? = OkHttpClient.Builder().cache(getCache()).build()

        private var gson = GsonBuilder()
            .setLenient()
            .create()

        private val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))

        fun <S> createService(serviceClass: Class<S>): S {
            Log.d(TAG, "BASE_URL: $BASE_URL")
            val okHttpClientBuilder = httpClient?.newBuilder()
            val modifiedOkHttpClient = okHttpClientBuilder?.addInterceptor(getHttpLoggingInterceptor())?.build()

            if (modifiedOkHttpClient != null) {
                builder.client(modifiedOkHttpClient)
            }
            builder.baseUrl(BASE_URL)

            val retrofit = builder.build()
            return retrofit.create(serviceClass)
        }

        private fun getCache(): Cache? {

            var cache: Cache? = null
            // Install an HTTP cache in the application cache directory.
            try {
                val cacheDir = File(EnergyBrokerApp.getCacheDirectory(), "http")
                cache = Cache(cacheDir, DISK_CACHE_SIZE)
            } catch (e: Exception) {
                Log.e(e.toString(), "Unable to install disk cache.")
            }

            return cache
        }

        private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            } else {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
            }
            return httpLoggingInterceptor
        }
    }
}