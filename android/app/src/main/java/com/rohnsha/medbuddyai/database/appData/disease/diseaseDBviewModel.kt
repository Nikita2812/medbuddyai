package com.rohnsha.medbuddyai.database.appData.disease

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rohnsha.medbuddyai.ContextUtill
import com.rohnsha.medbuddyai.database.appData.appDataDB
import com.rohnsha.medbuddyai.database.appData.disease_questions.questionVM
import com.rohnsha.medbuddyai.database.appData.disease_questions.questions
import com.rohnsha.medbuddyai.database.appData.doctors.doctor
import com.rohnsha.medbuddyai.database.appData.doctors.doctorsDAO
import com.rohnsha.medbuddyai.database.appData.doctors.doctorsRepo
import com.rohnsha.medbuddyai.database.appData.symptoms.symptomDAO
import com.rohnsha.medbuddyai.database.appData.symptoms.symptomDC
import com.rohnsha.medbuddyai.database.appData.symptoms.symptomRepo
import com.rohnsha.medbuddyai.database.userdata.scan_history.scanHistory
import com.rohnsha.medbuddyai.domain.dataclass.disease_data_dataClass
import com.rohnsha.medbuddyai.domain.notifications.dbUpdateService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class diseaseDBviewModel(application: Application): AndroidViewModel(application) {

    private val diseaseRepo: diseaseRepo
    private val diseaseDAO: diseaseDAO

    private val symptomRepo: symptomRepo
    private val symptomDAO: symptomDAO

    private val doctorRepo: doctorsRepo
    private val doctorDAO: doctorsDAO

    private val _dataList= MutableStateFlow<List<disease_data_dataClass>>(emptyList())
    private val _symptomList= MutableStateFlow<List<symptomDC>>(emptyList())
    private val _questionsLists= MutableStateFlow<List<questions>>(emptyList())
    private val _doctorLists= MutableStateFlow<List<doctor>>(emptyList())
    val notificanService= dbUpdateService(ContextUtill.ContextUtils.getApplicationContext())

    private val _processUpdatingDB= MutableStateFlow(false)
    val updatingDiseaseDB= _processUpdatingDB.asStateFlow()

    private val _loadingBoolean= MutableStateFlow(true)
    val isLoadingBoolean= _loadingBoolean.asStateFlow()

    private val _erroredBoolean = MutableStateFlow(false)
    val isErroredBoolean = _erroredBoolean.asStateFlow()

    private val _dataLoaded= MutableStateFlow(disease_data_dataClass())
    val data= _dataLoaded.asStateFlow()

    private val _dataCached= MutableStateFlow(scanHistory(0L, "", "", 0f, Int.MAX_VALUE))
    private val _dataCachedReadOnly= MutableStateFlow(disease_data_dataClass())

    private val questionVM= ViewModelProvider.AndroidViewModelFactory
        .getInstance(ContextUtill.ContextUtils.getApplicationContext() as Application)
        .create(
            questionVM::class.java
        )

    init {
        diseaseDAO= appDataDB.getAppDBReference(application).diseaseDAO()
        diseaseRepo= diseaseRepo(diseaseDAO)
        symptomDAO= appDataDB.getAppDBReference(application).symptomDAO()
        symptomRepo= symptomRepo(symptomDAO)
        doctorDAO= appDataDB.getAppDBReference(application).doctorsDAO()
        doctorRepo= doctorsRepo(doctorDAO)
    }

    suspend fun queryDoctors(speciality: String): List<doctor>{
        return doctorRepo.queryDoctor(speciality = speciality)
    }

    suspend fun getUniqueDeptList(): List<String> {
        return doctorRepo.getUniqueDept()
    }

    suspend fun readSymptoms(): List<symptomDC>{
        return withContext(viewModelScope.coroutineContext){
            symptomRepo.readSymptoms()
        }
    }

    suspend fun searchSymptom(symptomDC: String): List<symptomDC>{
        return withContext(viewModelScope.coroutineContext){
            symptomRepo.searchSymp(symptomDC)
        }
    }

    suspend fun searchSymptomByAbbreviation(symptomAbbreviation: String): symptomDC{
        return withContext(viewModelScope.coroutineContext){
            symptomRepo.searchSymByAbb(symptomAbbreviation)
        }
    }

    suspend fun searchDB(domain: String, indexItem: String): disease_data_dataClass {
        return diseaseRepo.searchDB(domain, indexItem)
    }

    fun inputNameToSearch(dataCached: scanHistory, onCompleteLambda: () -> Unit){
        _dataCached.value= dataCached
        onCompleteLambda()
    }

    fun saveData(data: disease_data_dataClass, onCompleteLambda: () -> Unit){
        _dataCachedReadOnly.value= data
        onCompleteLambda()
    }

    fun retrieveData(){
        if (_dataCachedReadOnly.value.disease_name!=""){
            _dataLoaded.value= _dataCachedReadOnly.value
            _loadingBoolean.value= false
        }
    }

    suspend fun searchByName(){
        if (_dataCached.value.title!=""){
            _dataLoaded.value=diseaseRepo.searchDBbyName(_dataCached.value.title)
            _loadingBoolean.value= false
        }

        if (_dataLoaded.value.disease_name==""){
            _erroredBoolean.value= true
            _loadingBoolean.value= false
        }
    }

    suspend fun searchByDomain(domain: String): List<disease_data_dataClass>{
        return withContext(viewModelScope.coroutineContext){
            diseaseRepo.searchDBbyDomain(domain)
        }
    }

    suspend fun readDB(): List<disease_data_dataClass>{
        return withContext(viewModelScope.coroutineContext){
            diseaseRepo.readDB()
        }
    }

    suspend fun fetchUpdatedDB(versionName: String, context: Context){
        _processUpdatingDB.value= true
        val sharedPreferences= context.getSharedPreferences("packageVersionName", Context.MODE_PRIVATE)
        val versionNameSF= sharedPreferences.getString("packageVersionName", "null")

        if (versionName!=versionNameSF){
            try {
                addDiseaseData {
                    sharedPreferences.edit().putString("packageVersionName", versionName).apply()
                    notificanService.showNotification(
                        title = "Database Updated Successfully",
                        message = "If you want to update it again then head to preferences"
                    )
                    _processUpdatingDB.value= false
                }
            } catch (e: Exception){
                Log.d("dbStatus", "error occured: ${e.message}")
                _processUpdatingDB.value= false
            }
        } else {
            _processUpdatingDB.value= false
            Log.d("dbStatus", "same version name")
        }
    }

    private suspend fun addDiseaseData(
        onCompleteLambda: () -> Unit
    ){
        Log.d("dbStatus", "entering fetchDIsease()")
        fetchDiseaseData()
        Log.d("dbStatus", "done fetchDIsease()")
        try {
            for (data in _dataList.value){
                Log.d("dbStatus", "attempting to add entries")
                diseaseRepo.addDisease(data)
                Log.d("dbStatus", "entries added succesfully")
            }
            for (data in _symptomList.value){
                symptomRepo.addSymptom(data)
            }
            for (data in _questionsLists.value){
                questionVM.insert(data)
            }
            for (data in _doctorLists.value){
                doctorRepo.addDoctor(data)
            }
            if (_symptomList.value.isNotEmpty() && _questionsLists.value.isNotEmpty() && _dataList.value.isNotEmpty() && _doctorLists.value.isNotEmpty()){
                onCompleteLambda()
            }
        } catch (e: Exception) {
            Log.d("dbStatus", "entries found")
        }
    }

    private suspend fun fetchDiseaseData(){
        val list= mutableListOf<disease_data_dataClass>()
        val listSymtom= mutableListOf<symptomDC>()
        val questionsList= mutableListOf<questions>()
        val doctorList= mutableListOf<doctor>()
        try {
            val dataInstance= Firebase.firestore
            for (data in dataInstance.collection("diseaseData").get().await().documents.map { documentSnapshot -> documentSnapshot.data }){
                Log.d("dbStatus", data.toString())
                list.add(
                    disease_data_dataClass(
                        symptoms = data?.get("symptoms") as String,
                        thresholds = data["thresholds"] as String,
                        domain = data["domain"] as String,
                        diseaseIndex = data["diseaseIndex"] as String,
                        cover_link = data["cover_link"] as String,
                        disease_name = data["disease_name"] as String,
                        cure = data["cure"] as String,
                        cure_cycle = data["cure_cycle"] as String,
                        introduction = data["introduction"] as String
                    )
                )
            }
            for (data in dataInstance.collection("symptoms").get().await().documents.map { documentSnapshot -> documentSnapshot.data }){
                listSymtom.add(
                    symptomDC(
                        symptom = data?.get("symptom") as String,
                        symptomAbbreviation = data["symptomAbbreviation"] as String
                    )
                )
            }
            for (data in dataInstance.collection("disease_questions").get().await().documents.map { documentSnapshot -> documentSnapshot.data }){
                questionsList.add(
                    questions(
                        question = data?.get("question") as String,
                        domain = data["options"] as Long,
                        index = data["answer"] as Long
                    )
                )
            }
            for (data in dataInstance.collection("doctors").get().await().documents.map { documentSnapshot -> documentSnapshot.data }){
                val dataL= doctor(
                    city = data?.get("city") as? String ?: "",
                    name = data?.get("name") as? String ?: "",
                    area = data?.get("area") as? String ?: "",
                    speciality = data?.get("speciality") as? String ?: "",
                    experience = data?.get("experience") as? String ?: "",
                    pricing = data?.get("pricing") as? String ?: "",
                    rating = data?.get("rating") as? String ?: "NaN",
                    link = data?.get("link") as? String ?: "https://www.practo.com/"
                )
                Log.d("doctorsList", dataL.toString())
                doctorList.add(dataL)
            }
            Log.d("dbStatus", "list: $list")
            if (list.isEmpty() || listSymtom.isEmpty() || questionsList.isEmpty() || doctorList.isEmpty()){
                notificanService.showNotification(
                    "Database Update Failed",
                    "A database update was triggered but failed unintentionally. We will try again later!"
                )
            }
            _dataList.value= list
            _symptomList.value= listSymtom
            _questionsLists.value= questionsList
            _doctorLists.value= doctorList
        } catch (e: Exception){
            Log.d("dbStatus", e.toString())
            notificanService.showNotification(
                "error occured",
                "tap to know more"
            )
        }
    }
}