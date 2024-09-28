package com.rohnsha.medbuddyai.database.userdata.chatbot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rohnsha.medbuddyai.database.userdata.chatbot.chats.chatDAO
import com.rohnsha.medbuddyai.database.userdata.chatbot.chats.chatEntity
import com.rohnsha.medbuddyai.database.userdata.chatbot.messages.messageDAO
import com.rohnsha.medbuddyai.database.userdata.chatbot.messages.messageEntity
import com.rohnsha.medbuddyai.database.userdata.userDataDB
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class chatDB_VM(application: Application): AndroidViewModel(application) {

    private val repo: chatRepo
    private val dao: chatDAO
    private val messagesDA0: messageDAO

    init {
        dao= userDataDB.getUserDBRefence(application).chatDA0()
        messagesDA0= userDataDB.getUserDBRefence(application).messageDAO()
        repo= chatRepo(dao, messagesDA0)
    }

    suspend fun addChat(chatEntity: chatEntity){
        viewModelScope.launch {
            repo.addChat(chatEntity)
        }
    }

    suspend fun addMessages(messageEntity: messageEntity){
        viewModelScope.launch {
            repo.addMessages(messageEntity)
        }
    }

    suspend fun readChatHistory(): List<chatEntity> {
        return withContext(viewModelScope.coroutineContext){
            repo.readChatHistory()
        }
    }

    suspend fun getChatCounts(): Int{
        return withContext(viewModelScope.coroutineContext){
            repo.getChatCount()
        }
    }

    suspend fun readChatWithMessages(chatid: Int): List<chatWithMessage>{
        return withContext(viewModelScope.coroutineContext){
            repo.readChats(chatid)
        }
    }

    suspend fun deleteChats(userIndex: Int){
        viewModelScope.launch {
            repo.deleteChatwMessage(userIndex)
        }
    }

}