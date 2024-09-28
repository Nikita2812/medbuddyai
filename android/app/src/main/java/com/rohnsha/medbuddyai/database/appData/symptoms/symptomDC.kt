package com.rohnsha.medbuddyai.database.appData.symptoms

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "symptoms")
data class symptomDC(
    val symptom: String,
    @PrimaryKey(autoGenerate = false)
    val symptomAbbreviation: String
)
