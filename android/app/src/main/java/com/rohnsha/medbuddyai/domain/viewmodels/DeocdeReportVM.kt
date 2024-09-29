package com.rohnsha.medbuddyai.domain.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.rohnsha.medbuddyai.api.decodeReport.decodeObj.decodeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeocdeReportVM: ViewModel() {

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap= _bitmap.asStateFlow()

    fun onTakePhoto(bitmap: Bitmap){
        _bitmap.value= bitmap

    }

    fun recognizeText(
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ){
        val image= _bitmap.value?.let { InputImage.fromBitmap(it, 0) }
        val recognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        if (image != null) {
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val resultText = visionText.text
                    onSuccess(resultText)
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        }
    }

    suspend fun decode() {
        recognizeText(
            onSuccess = {
                viewModelScope.launch {
                    Log.d(
                        "DecodeReportVM", decodeService.decodeReport(
                            serviceName = "swasthai",
                            secretCode = "default",
                            message = "hi"
                        ).message.toString()
                    )
                }
            },
            onFailure = {

            }
        )
    }

}