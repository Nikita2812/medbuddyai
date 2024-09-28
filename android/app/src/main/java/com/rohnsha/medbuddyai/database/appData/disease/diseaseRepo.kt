package com.rohnsha.medbuddyai.database.appData.disease

import com.rohnsha.medbuddyai.domain.dataclass.disease_data_dataClass

class diseaseRepo(private val diseaseDAO: diseaseDAO) {

    suspend fun addDisease(diseaseDataDataclass: disease_data_dataClass){
        diseaseDAO.addDiseaseData(diseaseDataDataclass)
    }

    suspend fun searchDB(domain: String, indexItem: String): disease_data_dataClass {
        return diseaseDAO.searchDB(domain, indexItem)
    }

    suspend fun searchDBbyName(name: String): disease_data_dataClass{
        return diseaseDAO.searchDBbyName(name)
    }

    suspend fun searchDBbyDomain(domain: String): List<disease_data_dataClass>{
        return diseaseDAO.searchDBbyDomain(domain)
    }

    suspend fun readDB(): List<disease_data_dataClass>{
        return  diseaseDAO.readDB()
    }

}