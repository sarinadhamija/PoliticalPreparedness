package com.example.android.politicalpreparedness.data.remote

import okhttp3.OkHttpClient

class CivicsHttpClient: OkHttpClient() {

    companion object {

        const val API_KEY = "AIzaSyAYFYbr9V1KYxzExvRAOyVUiFoo_gQQcuU"

        fun getClient(): OkHttpClient {
            return Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val url = original
                                .url()
                                .newBuilder()
                                .addQueryParameter("key", API_KEY)
                                .build()
                        val request = original
                                .newBuilder()
                                .url(url)
                                .build()
                        chain.proceed(request)
                    }
                    .build()
        }

    }

}