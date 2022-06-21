package com.example.android.politicalpreparedness.data.remote.models

import androidx.room.*
import com.squareup.moshi.*
import java.util.*

@Entity(tableName = "election_table")
@JsonClass(generateAdapter = true)
data class Election(
    @PrimaryKey @Json(name = "id") val id: Int,
    @ColumnInfo(name = "name") @Json(name = "name") val name: String,
    @ColumnInfo(name = "electionDay") @Json(name = "electionDay") val electionDay: Date,
    @Embedded(prefix = "division_") @Json(name = "ocdDivisionId") val division: Division
)