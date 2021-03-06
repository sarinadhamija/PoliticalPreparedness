package com.example.android.politicalpreparedness.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ElectionOfficial(
    val name: String,
    val title: String,
    @Json(name="officePhoneNumber") val phone: String,
    @Json(name="faxNumber") val fax: String,
    val emailAddress: String
)