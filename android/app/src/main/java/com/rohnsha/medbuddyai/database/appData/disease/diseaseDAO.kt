package com.rohnsha.medbuddyai.database.appData.disease

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rohnsha.medbuddyai.domain.dataclass.disease_data_dataClass

@Dao
interface diseaseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDiseaseData(diseaseDataDataclass: disease_data_dataClass)

    @Query("SELECT * FROM disease_data WHERE domain LIKE :domain AND diseaseIndex LIKE :indexItem")
    suspend fun searchDB(domain: String, indexItem: String): disease_data_dataClass

    @Query("SELECT * FROM disease_data WHERE disease_name LIKE :name")
    suspend fun searchDBbyName(name: String): disease_data_dataClass

    @Query("select * from disease_data")
    suspend fun readDB(): List<disease_data_dataClass>

    @Query("SELECT * FROM disease_data WHERE domain LIKE :domain")
    suspend fun searchDBbyDomain(domain: String): List<disease_data_dataClass>
}