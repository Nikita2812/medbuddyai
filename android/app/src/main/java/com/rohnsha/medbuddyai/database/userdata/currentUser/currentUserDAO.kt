package com.rohnsha.medbuddyai.database.userdata.currentUser

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface currentUserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: fieldValueDC)

    @Query("SELECT * FROM currentUserTable where isDefaultUser like :isDefaultUserBool")
    suspend fun getFieldData(isDefaultUserBool: Boolean): fieldValueDC

    @Query("SELECT * FROM currentUserTable")
    suspend fun getAllUsers(): List<fieldValueDC>

    @Query("select * from currentUserTable where `index` like :userIndex")
    suspend fun getuserInformaton(userIndex: Int): fieldValueDC

    @Transaction
    @Query("delete from currentUserTable where `index` like :userIndex")
    suspend fun deleteUser(userIndex: Int)

    @Query("select count(*) from currentUserTable")
    suspend fun countEntries(): Int
}