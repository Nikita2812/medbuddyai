package com.rohnsha.medbuddyai.domain.dataclass

data class classification(
    val indexNumber: Int,
    val confident: Float,
    val parentIndex: Int?=null
)