package com.rohnsha.medbuddyai.database.appData.disease_questions

class questionRepo(private val questionDAO: questionDAO) {

    suspend fun insert(questions: questions){
        questionDAO.insert(questions)
    }

    suspend fun getQuestions(domain: Long, index: Long): List<questions>{
        return questionDAO.searchQuestions(domain, index)
    }

}