package com.example.android.politicalpreparedness.data.remote.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class State (
    val name: String,
    val electionAdministrationBody: AdministrationBody
)