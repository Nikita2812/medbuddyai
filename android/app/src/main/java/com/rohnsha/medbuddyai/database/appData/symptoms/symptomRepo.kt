package com.rohnsha.medbuddyai.database.appData.symptoms

class symptomRepo(private val symptomDAO: symptomDAO) {

    suspend fun addSymptom(symptom: symptomDC) {
        symptomDAO.addSymptom(symptom)
    }

    suspend fun readSymptoms(): List<symptomDC> {
        return symptomDAO.getAllSymptoms()
    }

    suspend fun searchSymp(symptom: String): List<symptomDC>{
        return symptomDAO.searchSymptom(symptom)
    }

    suspend fun searchSymByAbb(symptomAbbreviation: String): symptomDC{
        return symptomDAO.searchSymptomByAbbrev(symptomAbbreviation)
    }

}