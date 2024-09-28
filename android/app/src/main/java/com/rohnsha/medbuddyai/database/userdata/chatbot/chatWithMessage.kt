package com.rohnsha.medbuddyai.database.userdata.chatbot

import androidx.room.Embedded
import androidx.room.Relation
import com.rohnsha.medbuddyai.database.userdata.chatbot.chats.chatEntity
import com.rohnsha.medbuddyai.database.userdata.chatbot.messages.messageEntity

data class chatWithMessage (
    @Embedded
    val chat: chatEntity,
    @Relation(parentColumn = "id", entityColumn = "chatId")
    val messages: List<messageEntity>
)