package com.rohnsha.medbuddyai.database.appData.disease_questions

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class questions(
    @PrimaryKey(autoGenerate = false)
    val question: String,
    val domain: Long,
    val index: Long
)

data class questionMsg(
    val questions: questions,
    val isBotMessage: Boolean,
    val isErrored: Boolean = false
)
