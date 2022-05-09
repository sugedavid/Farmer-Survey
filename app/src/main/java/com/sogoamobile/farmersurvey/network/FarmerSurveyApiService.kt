package com.sogoamobile.farmersurvey.network

import com.sogoamobile.farmersurvey.database.SurveyResponseTable
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val BASE_URL =
    "https://run.mocky.io/v3/"
private const val BASE_DUMMY_URL =
    "https://jsonplaceholder.typicode.com/todos/1"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface FarmerSurveyApiService {
    @GET("d628facc-ec18-431d-a8fc-9c096e00709a")
    suspend fun getSurvey(): FarmerSurvey
    @POST("posts")
    fun postSurvey(@Body surveyResponseTable: List<SurveyResponseTable>): Call<List<SurveyResponseTable>>
}

object FarmerSurveyApi {
    val retrofitService: FarmerSurveyApiService by lazy {
        retrofit.create(FarmerSurveyApiService::class.java)
    }
}