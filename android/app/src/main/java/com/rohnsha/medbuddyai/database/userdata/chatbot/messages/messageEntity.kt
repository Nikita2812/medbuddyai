package com.rohnsha.medbuddyai.database.userdata.chatbot.messages

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages"
)
data class messageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val message: String,
    val isBotMessage: Boolean,
    val timestamp: Long,
    val isError: Boolean,
    val chatId: Int,
    @ColumnInfo(name = "hasAttachments", defaultValue = "0")
    val hasAttachments: Long= 0L // contains timestamp as foreign key for retrieving scan_history data
)
