package com.rohnsha.medbuddyai.domain.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disease_data")
data class disease_data_dataClass(
    val cure: String="",
    val cure_cycle: String="",
    @PrimaryKey(autoGenerate = false)
    val disease_name: String="",
    val domain: String="",
    val diseaseIndex: String="",
    val introduction: String="",
    val symptoms: String="",
    val thresholds: String="",
    val cover_link: String="",
)