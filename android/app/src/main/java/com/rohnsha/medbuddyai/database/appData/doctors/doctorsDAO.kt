package com.rohnsha.medbuddyai.database.appData.doctors

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface doctorsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDoctors(doctorDC: doctor)

    @Query("select * from doctor where speciality like :speciality")
    suspend fun queryDoctor(speciality: String): List<doctor>

    @Query("select distinct speciality from doctor order by speciality ")
    suspend fun getUniqueDept(): List<String>
}