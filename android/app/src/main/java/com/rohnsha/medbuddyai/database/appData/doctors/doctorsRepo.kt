package com.rohnsha.medbuddyai.database.appData.doctors

class doctorsRepo(private val doctorsDAO: doctorsDAO) {

    suspend fun addDoctor(doctorDC: doctor){
        doctorsDAO.addDoctors(doctorDC)
    }

    suspend fun queryDoctor(speciality: String): List<doctor>{
        return doctorsDAO.queryDoctor(speciality)
    }

    suspend fun getUniqueDept(): List<String>{
        return doctorsDAO.getUniqueDept()
    }

}