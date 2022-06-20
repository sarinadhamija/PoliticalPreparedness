package com.example.android.politicalpreparedness.representative.model

import com.example.android.politicalpreparedness.data.remote.models.Office
import com.example.android.politicalpreparedness.data.remote.models.Official

data class Representative (
        val official: Official,
        val office: Office
)