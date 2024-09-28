package com.rohnsha.medbuddyai.database.userdata.keys

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface keyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecretKey(keyDC: keyDC)

    @Query("SELECT * FROM secrets")
    suspend fun querySecrets(): List<keyDC>

    @Query("SELECT * FROM secrets where serviceName like :serviceName")
    suspend fun getSecretKey(serviceName: String): keyDC

    @Query("DELETE FROM secrets")
    suspend fun clearAllPairs()

}