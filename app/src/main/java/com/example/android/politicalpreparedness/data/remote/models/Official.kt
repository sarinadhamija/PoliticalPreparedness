package com.example.android.politicalpreparedness.data.remote.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Official (
        val name: String,
        val address: List<Address>? = null,
        val party: String? = null,
        val phones: List<String>? = null,
        val urls: List<String>? = null,
        val photoUrl: String? = null,
        val channels: List<Channel>? = null
) : Parcelable