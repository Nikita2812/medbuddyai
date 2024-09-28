package com.rohnsha.medbuddyai.database.appData.disease_questions

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface questionDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: questions)

    @Query("SELECT * FROM questions where domain like :domain and `index` like :index")
    suspend fun searchQuestions(domain: Long, index: Long): List<questions>

}