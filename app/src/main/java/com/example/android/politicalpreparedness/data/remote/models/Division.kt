package com.example.android.politicalpreparedness.data.remote.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Division(
        val id: String,
        val country: String,
        val state: String
) : Parcelable