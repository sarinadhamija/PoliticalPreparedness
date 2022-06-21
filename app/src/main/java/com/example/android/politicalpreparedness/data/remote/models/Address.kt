package com.example.android.politicalpreparedness.data.remote.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address (
        var line1: String = "",
        var line2: String? = null,
        var city: String = "",
        var state: String = "",
        var zip: String = ""
) : Parcelable {
    fun toFormattedString(): String {
        var output = line1.plus("\n")
        if (!line2.isNullOrEmpty()) output = output.plus(line2).plus("\n")
        output = output.plus("$city, $state $zip")
        return output
    }

    fun isEmpty(): Boolean = line1.isEmpty() || city.isEmpty() || state.isEmpty() || zip.isEmpty()
}