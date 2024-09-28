package com.rohnsha.medbuddyai.database.appData.symptoms

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface symptomDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSymptom(symptom: symptomDC)

    @Query( "SELECT * FROM symptoms WHERE symptom LIKE '%' || :symptom || '%'")
    suspend fun searchSymptom(symptom: String): List<symptomDC>

    @Query( "SELECT * FROM symptoms WHERE symptomAbbreviation LIKE :symptomAbbreviation")
    suspend fun searchSymptomByAbbrev(symptomAbbreviation: String): symptomDC

    @Query("SELECT * FROM symptoms order by RANDOM()")
    suspend fun getAllSymptoms(): List<symptomDC>
}