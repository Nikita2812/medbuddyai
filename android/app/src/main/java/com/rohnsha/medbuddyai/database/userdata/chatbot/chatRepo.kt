package com.rohnsha.medbuddyai.database.userdata.chatbot

import com.rohnsha.medbuddyai.database.userdata.chatbot.chats.chatDAO
import com.rohnsha.medbuddyai.database.userdata.chatbot.chats.chatEntity
import com.rohnsha.medbuddyai.database.userdata.chatbot.messages.messageDAO
import com.rohnsha.medbuddyai.database.userdata.chatbot.messages.messageEntity

class chatRepo(private val chatDAO: chatDAO, private val  messageDAO: messageDAO) {

    suspend fun readChatHistory(): List<chatEntity>{
        return chatDAO.readChats()
    }

    suspend fun addChat(chatEntity: chatEntity){
        chatDAO.addChat(chatEntity)
    }

    suspend fun getChatCount(): Int{
        return chatDAO.getChatCount()
    }

    suspend fun addMessages(messageEntity: messageEntity){
        messageDAO.addChat(messageEntity)
    }

    suspend fun deleteChatwMessage(userIndex: Int){
        chatDAO.getChatIDwUserIndex(userIndex = userIndex).forEach {
            messageDAO.deleteMessages(it)
        }
        chatDAO.deleteChat(userIndex)
    }

    suspend fun readChats(chatID: Int): List<chatWithMessage> {
        return chatDAO.getChatWithMessages(chatID)
    }

}