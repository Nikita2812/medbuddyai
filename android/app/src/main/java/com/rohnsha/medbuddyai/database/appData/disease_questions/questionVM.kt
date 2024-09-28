package com.rohnsha.medbuddyai.database.appData.disease_questions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.rohnsha.medbuddyai.database.appData.appDataDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class questionVM(application: Application): AndroidViewModel(application) {

    private val repo: questionRepo
    private val dao: questionDAO
    private val _questionList= MutableStateFlow(emptyList<questions>())
    val questionList = _questionList.asStateFlow()

    init {
        dao= appDataDB.getAppDBReference(application).questionsDAO()
        repo= questionRepo(dao)
    }

    suspend fun insert(questions: questions){
        repo.insert(questions)
    }

    suspend fun getQuestions(domain: Long, index: Long){
        _questionList.value= repo.getQuestions(domain = domain, index = index)
    }

}