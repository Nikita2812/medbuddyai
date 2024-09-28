package com.rohnsha.medbuddyai.database.userdata.chatbot.chats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rohnsha.medbuddyai.database.userdata.chatbot.chatWithMessage

@Dao
interface chatDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addChat(chatEntity: chatEntity)

    @Query("select * from chats order by timestamp desc")
    suspend fun readChats(): List<chatEntity>

    @Transaction
    @Query("select * from chats where id = :chatId")
    suspend fun getChatWithMessages(chatId: Int): List<chatWithMessage>

    @Query("select count(*) from chats")
    suspend fun getChatCount(): Int

    @Transaction
    @Query("delete from chats where userIndex like :userIndex")
    suspend fun deleteChat(userIndex: Int)

    @Query("select id from chats where userIndex like :userIndex")
    suspend fun getChatIDwUserIndex(userIndex: Int): List<Int>
}