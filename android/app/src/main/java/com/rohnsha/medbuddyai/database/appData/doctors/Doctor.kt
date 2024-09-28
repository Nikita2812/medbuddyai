package com.rohnsha.medbuddyai.database.appData.doctors

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctor")
data class doctor(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val area: String,
    val city: String,
    val experience: String,
    val name: String,
    val pricing: String,
    val speciality: String,
    @ColumnInfo(defaultValue = "NaN")
    val rating: String,
    @ColumnInfo(defaultValue = "https://www.practo.com/")
    val link: String
)
