package com.rohnsha.medbuddyai.database.userdata.chatbot.chats

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class chatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val mode: Int,
    val userIndex: Int
)
