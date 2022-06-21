package com.example.android.politicalpreparedness.data.remote

import com.example.android.politicalpreparedness.data.remote.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.data.remote.models.ElectionResponse
import com.example.android.politicalpreparedness.data.remote.models.RepresentativeResponse
import com.example.android.politicalpreparedness.data.remote.models.VoterInfoResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

private const val BASE_URL = "https://www.googleapis.com/civicinfo/v2/"

private val moshi = Moshi.Builder()
    .add(ElectionAdapter())
    .add(KotlinJsonAdapterFactory())
    .add(Date::class.java,  Rfc3339DateJsonAdapter().nullSafe())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(CivicsHttpClient.getClient())
    .baseUrl(BASE_URL)
    .build()

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

interface CivicsApiService {
    @GET("elections")
    fun getElectionList(): Deferred<ElectionResponse>

    @GET("voterinfo?returnAllAvailableData=true")
    fun getVoterInfo(@Query("address") address: String,
                     @Query("electionId") electionId: Int?
    ): Deferred<VoterInfoResponse>

    @GET("representatives")
    fun getRepresentativeList(@Query("address") address: String): Deferred<RepresentativeResponse>
}

object CivicsApi {
    val retrofitService: CivicsApiService by lazy {
        retrofit.create(CivicsApiService::class.java)
    }
}