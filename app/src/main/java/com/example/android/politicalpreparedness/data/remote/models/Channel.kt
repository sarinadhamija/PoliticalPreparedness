package com.example.android.politicalpreparedness.data.remote.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Channel (
    val type: String,
    val id: String
) : Parcelable