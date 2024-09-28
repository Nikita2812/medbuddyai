package com.rohnsha.medbuddyai.domain.dataclass

data class modelMarketPlace(
    val modelName: String,
    val modelVersion: String,
    val description: String,
    val isCancerous: Boolean,
    val domainIndex: Int
)
