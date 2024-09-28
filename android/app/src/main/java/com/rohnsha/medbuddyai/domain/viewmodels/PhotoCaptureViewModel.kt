package com.rohnsha.medbuddyai.domain.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.rohnsha.medbuddyai.ContextUtill
import com.rohnsha.medbuddyai.api.verify.verifyDC
import com.rohnsha.medbuddyai.api.verify.verifyObj.verifyService
import com.rohnsha.medbuddyai.database.appData.disease.diseaseDBviewModel
import com.rohnsha.medbuddyai.database.userdata.keys.keyDC
import com.rohnsha.medbuddyai.domain.dataclass.classification
import com.rohnsha.medbuddyai.domain.dataclass.disease_data_dataClass
import com.rohnsha.medbuddyai.domain.dataclass.disease_version
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class photoCaptureViewModel: ViewModel() {

    val viewModelClassifier: classificationVM=ViewModelProvider.AndroidViewModelFactory
        .getInstance(ContextUtill.ContextUtils.getApplicationContext() as Application)
        .create(
            classificationVM::class.java
        )

    val viewModelDiseaseDB= ViewModelProvider.AndroidViewModelFactory
        .getInstance(ContextUtill.ContextUtils.getApplicationContext() as Application)
        .create(
            diseaseDBviewModel::class.java
        )

    private val _bitmaps = MutableStateFlow<Bitmap?>(null)
    val bitmaps = _bitmaps.asStateFlow()

    private val _listPrediction = MutableStateFlow<List<classification>>(emptyList())
    val unsortedClassification= _listPrediction.asStateFlow()

    private val _classificationData = MutableStateFlow(disease_data_dataClass())
    val classificationData = _classificationData.asStateFlow()

    private val _updatedClassificationData= MutableStateFlow(disease_data_dataClass())

    private val _maxIndex= MutableStateFlow(
        classification(confident = 0f, indexNumber = 404, parentIndex = 404)
    )
    val maxIndex= _maxIndex.asStateFlow()

    private val _notMaxElements= MutableStateFlow<List<classification>>(emptyList())
    val minIndex= _notMaxElements.asStateFlow()

    private val _loadingBoolean= MutableStateFlow(true)
    val isLoadingBoolean= _loadingBoolean.asStateFlow()

    private val _reloadingBoolean= MutableStateFlow(false)
    val isReLoadingBoolean= _reloadingBoolean.asStateFlow()

    private val _normalBoolean = MutableStateFlow(false)
    val isNormalBoolean = _normalBoolean.asStateFlow()

    private val _erroredBoolean = MutableStateFlow(false)
    val isErroredBoolean = _erroredBoolean.asStateFlow()

    fun onTakePhoto(bitmap: Bitmap) {
        _bitmaps.value = bitmap
    }

    fun flushValues(){
        _bitmaps.value= null
        _maxIndex.value= classification(confident = 0f, indexNumber = 404, parentIndex = 404)
        _notMaxElements.value= emptyList()
        _classificationData.value= disease_data_dataClass()
        _loadingBoolean.value= true
        _erroredBoolean.value= false
        _listPrediction.value= emptyList()
        _reloadingBoolean.value= false
    }

    fun resetReloadBoolean(){
        _reloadingBoolean.value= true
    }

    suspend fun onClassify(context: Context, index: Int){
        _listPrediction.value= emptyList()
        _normalBoolean.value=false
        _reloadingBoolean.value= true
        val rawClassification= try {
            viewModelClassifier.classify(context = context, bitmap = _bitmaps.value!!, scanOption = index)
        } catch (e: Exception){
            Log.d("successIndex", e.printStackTrace().toString())
            emptyList()
        }
        if (rawClassification.isEmpty()){
            _erroredBoolean.value=true
            _loadingBoolean.value=false
            _reloadingBoolean.value=false
        } else {
            _listPrediction.value= rawClassification.filter { it.indexNumber == 1 && it.confident > 0.75 }
            if (_listPrediction.value.isEmpty()){
                _normalBoolean.value=true
                _loadingBoolean.value=false
                _reloadingBoolean.value=false
            } else {
                classificationData(group_number = index)
            }
        }
        Log.d("successIndexVMNormal", _normalBoolean.value.toString())
        Log.d("successIndexVMErrored", _erroredBoolean.value.toString())
        Log.d("successIndexVMList", _listPrediction.value.toString())
    }

    private suspend fun classificationData(group_number: Int){
        try {
            val data= mutableStateOf(disease_data_dataClass())
            sortClassifiedList()
            Log.d("bugErrrored", data.value.toString())

            Log.d("bugErrrored", "attempting to start db search")
            Log.d("bugErrrored", "domain= $group_number\nIndexItem= ${_maxIndex.value.parentIndex}")
            data.value= viewModelDiseaseDB.searchDB(
                domain = group_number.toString(),
                indexItem = _maxIndex.value.parentIndex.toString()
            )
            Log.d("bugErrrored", "db search cmplt")

            Log.d("bugErrrored", data.value.toString())

            Log.d("maxIndex", _maxIndex.value.toString())
            if (data.value.disease_name != "") {
                _classificationData.value= data.value
                _loadingBoolean.value=false
                _reloadingBoolean.value=false
            } else {
                val collectionName= when(group_number){
                    0 -> "lung"
                    1 -> "brain"
                    else -> "null"
                }
                val dataInstance= Firebase.firestore.collection(collectionName)
                val querySnapshot=dataInstance.document(_maxIndex.value.parentIndex.toString()).get().await()
                data.value= querySnapshot.toObject<disease_data_dataClass>()!!
                Log.d("classificationDataInit", querySnapshot.data.toString())
            }
        } catch (e: Exception){
            Log.e("classificationError", e.message.toString())
            _erroredBoolean.value= true
            _loadingBoolean.value=false
            _reloadingBoolean.value=false
        }
    }

    private fun sortClassifiedList(){
        val maxValue= _listPrediction.value.maxByOrNull { it.confident }?.confident ?: 0
        val maximumElement= mutableListOf<classification>()
        val notMaxElements= mutableListOf<classification>()

        Log.d("classificationData", _listPrediction.value.toString())
        for (data in _listPrediction.value){
            if (data.confident==maxValue) _maxIndex.value= (data) else notMaxElements.add(data)
        }
        _notMaxElements.value= notMaxElements
        Log.d("classificationDataHighest", _maxIndex.value.toString())
        Log.d("classificationDataSorted", _notMaxElements.value.toString())
    }

    fun updateConfidence(confidence: Float){
        Log.d("percecntagesScan", "initla: ${_maxIndex.value.confident}, conf: $confidence")
        val updatedConfidence= (_maxIndex.value.confident + confidence)/2
        Log.d("percecntagesScan", "final: $updatedConfidence")
        if (updatedConfidence<=75){
            _normalBoolean.value= true
        } else _maxIndex.value= _maxIndex.value.copy(confident = updatedConfidence)
    }


    suspend fun verifyFromRemote(
        keyPair: keyDC,
        disease_name: String,
        isSuccessfulListerner: (Boolean, verifyDC) -> Unit
    ){
        val bitmapp = _bitmaps.value

        val stream = ByteArrayOutputStream()
        bitmapp?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()

        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", "image.jpg", requestBody)

        try {
            val call = verifyService.verifyRemoteModels(
                serviceName = keyPair.serviceName,
                secretCode = keyPair.secretKey,
                diseaseName = disease_name,
                bitmp = body
            )
            Log.d("verifySuccess", call.toString())
            isSuccessfulListerner(true, call)
        } catch (e: Exception){
            Log.d("verifyError", e.message.toString())
            isSuccessfulListerner(false, verifyDC(0.2, false))
        }
    }

    fun getDiseaseVersionData(
        group_number: Int,
        isMaxIndex: Boolean
    ): List<disease_version> {
        val reqList: MutableList<disease_version> = mutableListOf()
        Log.d("grPNumber", group_number.toString())

        val selectedDomain= when(group_number){
            0 -> {
                listOf(
                    disease_version("Acute Lymphoblastic Leukemia (Early)", "V2024.03.04", modelAccuracy = 98.9),
                    disease_version("Acute Lymphoblastic Leukemia (Pro)", "V2024.03.04", modelAccuracy = 100.0)
                )
            }
            1 -> {
                listOf(
                    disease_version("Colon Cancer", "V2024.02.05", modelAccuracy = 98.5)
                )
            }
            2 -> {
                listOf(
                    disease_version("Oral Cancer", modelAccuracy = 95.25, version = "V2024.02.03")
                )
            }
            3 -> {
                listOf(
                    disease_version("Gioma Tumor", "V2023.04.30", modelAccuracy = 97.70),
                    disease_version("Pituitary Tumor", "V2023.11.14", modelAccuracy = 97.60),
                    disease_version("Meningioma Tumor", "V2023.11.14", modelAccuracy = 97.60)
                )
            }
            4 -> {
                listOf(
                    disease_version("Breast Cancer", "V2024.03.02", modelAccuracy = 93.25)
                )
            }
            6 -> {
                listOf(
                    disease_version("Pneumonia", "V2023.04.30", modelAccuracy = 97.70),
                    disease_version("Tuberculosis", "V2023.11.14", modelAccuracy = 97.60),
                )
            }
            7 -> {
                listOf(
                    disease_version("Lung Adenocarcinoma", "V2024.03.04", modelAccuracy = 99.25),
                    disease_version("Lung Squamous Cell Carcinoma", "V2024.03.05", modelAccuracy = 100.0)
                )
            }
            8 -> {
                listOf(
                    disease_version("Acne", "V2024.03.10", modelAccuracy = 94.8),
                    disease_version("Ezcima", "V2024.03.10", modelAccuracy = 91.8),
                    disease_version("Infection", "V2024.03.10", modelAccuracy = 76.8),
                    disease_version("Pigmentation", "V2024.03.10", modelAccuracy = 81.6)
                )
            }
            9 -> {
                listOf(
                    disease_version("Skin Malignant Cancer", "V2024.03.10", modelAccuracy = 85.6)
                )
            }
            11 -> {
                listOf(
                    disease_version("Kindey Tumor", "V2024.02.02", modelAccuracy = 94.75)
                )
            }
            else -> { emptyList() }
        }

        Log.d("logError", "max values: ${_maxIndex.value}")
        Log.d("logError", "selected index: ${_maxIndex.value}")

        if (isMaxIndex){
            selectedDomain.let {
                reqList.add(
                    disease_version(
                        it[_maxIndex.value.parentIndex!!].disease_name,
                        it[_maxIndex.value.parentIndex!!].version,
                        _maxIndex.value.confident,
                        modelAccuracy = it[_maxIndex.value.parentIndex!!].modelAccuracy
                    ))
            }
            return reqList
        }
        _notMaxElements.value.forEach { classification ->
            selectedDomain.let {
                reqList.add(disease_version(
                    it[classification.parentIndex!!].disease_name,
                    it[classification.parentIndex].version,
                    classification.confident
                ))
            }
        }
        return reqList
    }
}