package com.rohnsha.medbuddyai.database.userdata.keys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "secrets")
data class keyDC(
    @PrimaryKey(autoGenerate = false)
    val serviceName: String,
    val secretKey: String
)
