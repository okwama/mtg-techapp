package com.example.mytechnician.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // TODO: Replace with your actual API base URL
    private const val BASE_URL = "http://192.168.100.4:3000/" // Local API address
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    var authToken: String? = null
    
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val request = chain.request().newBuilder()
        authToken?.let {
            request.addHeader("Authorization", "Bearer $it")
        }
        chain.proceed(request.build())
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
    
    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val inspectionApi: InspectionApi = retrofit.create(InspectionApi::class.java)
    val shiftApi: ShiftApi = retrofit.create(ShiftApi::class.java)
    val partApi: PartApi = retrofit.create(PartApi::class.java)
    val serviceApi: ServiceApi = retrofit.create(ServiceApi::class.java)
}
