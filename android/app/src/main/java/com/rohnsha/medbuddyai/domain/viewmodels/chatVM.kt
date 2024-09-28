package com.rohnsha.medbuddyai.domain.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.rohnsha.medbuddyai.api.chatbot.chatbot_obj.chatService
import com.rohnsha.medbuddyai.database.appData.disease.diseaseDBviewModel
import com.rohnsha.medbuddyai.database.appData.symptoms.symptomDC
import com.rohnsha.medbuddyai.database.userdata.chatbot.chatDB_VM
import com.rohnsha.medbuddyai.database.userdata.chatbot.chats.chatEntity
import com.rohnsha.medbuddyai.database.userdata.chatbot.messages.messageEntity
import com.rohnsha.medbuddyai.database.userdata.keys.keyVM
import com.rohnsha.medbuddyai.database.userdata.scan_history.scanHistory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.io.IOException
import kotlin.math.pow

class chatVM: ViewModel() {

    private val _listMessages= MutableSharedFlow<messageEntity>()
    val messagesList= _listMessages.asSharedFlow()

    private val _messageCount= MutableStateFlow(0)
    val messageCount= _messageCount.asStateFlow()

    private val _isChatbotResponding= MutableStateFlow(false)
    val isChatbotResponding= _isChatbotResponding.asStateFlow()

    private val _messageWithAttachment= MutableStateFlow(messageEntity(
        message = "",
        isBotMessage = false,
        timestamp = System.currentTimeMillis(),
        isError = false,
        chatId = 0,
    ))
    val messageWAttachment= _messageWithAttachment.asStateFlow()
    val _lastScanAsAttachment= MutableStateFlow(
        scanHistory(
            timestamp = 0L,
            title = "",
            domain = 0.toString(),
            confidence = 0f,
            userIndex = 0
        )
    )

    private var retryCount = 0

    suspend fun endSymptomTest(mode: Int, chatID: Int){
        if (mode==1){
            Log.d("chatVM", "symptoms init")
            _listMessages.emit(
                messageEntity(
                    message = "Either send the message for getting the final disease, or add more symptoms by searching",
                    isBotMessage = true,
                    timestamp = System.currentTimeMillis(),
                    isError = true,
                    chatId = chatID
                )
            )
            Log.d("chatVM", "symptom done")
            _messageCount.value += 1
            Log.d("chatVM", "incremnetd done")

        }
        Log.d("chatVM", _listMessages.toString())
    }

    suspend fun symptomChat(
        symptom: String,
        selectedDisease: symptomDC,
        vmChat: chatDB_VM,
        currentUserIndex: Int,
        diseaseDBviewModel: diseaseDBviewModel,
        chatID: Int,
        outcome: ((symptomDC) -> Unit),
        isPredictionNull: ((Boolean) -> Unit?)? = null
    ){
        if (_messageCount.value==0){
            vmChat.addChat(
                chatEntity(timestamp = System.currentTimeMillis(), mode = 1, id = chatID, userIndex = currentUserIndex)
            )
        }
        Log.d("chatVMSymptom", symptom)
        Log.d("chatVMSymptom", symptom.substringAfterLast(", ").trim())
        val messageBody= messageEntity(
            message = "Selected symptom: ${selectedDisease.symptomAbbreviation}",
            isBotMessage =  false,
            timestamp = System.currentTimeMillis(),
            isError = true,
            chatId = chatID
        )
        vmChat.addMessages(messageBody)
        _listMessages.emit(
            messageBody
        )
        _messageCount.value += 1
        Log.d("symptomsCheckDebug", symptom)
        val dynamicURL= "https://api-jjtysweprq-el.a.run.app/next_symptom/$symptom"
        try {
            Log.d("apiDebugging", "calling HTTP")
            val response= chatService.getChatReply(dynamicURL)
            Log.d("apiDebugging", "called HTTP")
            Log.d("apiDebugging", response.message.toString())
            response.message?.let { Log.d("symptomsCheck", it) }
            response.message?.let{
                Log.d("symptomsCheck", it)
                val symptomData= diseaseDBviewModel.searchSymptomByAbbreviation(it)
                val resultAPI= messageEntity(
                    message = "You may also have: ${symptomData.symptom}",
                    isBotMessage = true,
                    timestamp = System.currentTimeMillis(),
                    isError = false,
                    chatId = chatID
                )
                outcome(symptomData)
                _listMessages.emit(resultAPI)
                _messageCount.value += 1
                vmChat.addMessages(resultAPI)
                Log.d("errorChat", it)
            }
            if (isPredictionNull != null && response.message == null) {
                endSymptomTest(mode = 1, chatID = chatID)
                isPredictionNull(true)
            }
        } catch (e: Exception){
            Log.e("messageError", e.message.toString())
        }
    }

    suspend fun chat(
        message: String,
        resetMessageFeild: () -> Unit,
        vmChat: chatDB_VM,
        currentUserIndex: Int,
        chatID: Int,
        isRetrying: Boolean= false,
        timeStampAttachment: Long = 0L,
        mode: Int, //0 -> qna, 1 -> ai_symptoms_checker,
        keyVM: keyVM
    ){
        _isChatbotResponding.value= true
        val defaultService= keyVM.defaultEngine.value
        if (!isRetrying){
            val messageBody= messageEntity(
                message = message,
                isBotMessage =  false,
                timestamp = System.currentTimeMillis(),
                isError = false,
                chatId = chatID,
                hasAttachments = timeStampAttachment
            )
            vmChat.addMessages(messageBody)
            _listMessages.emit(
                messageBody
            )
            _messageCount.value += 1
            resetMessageFeild()
        }
        val newChat = chatEntity(
            timestamp = System.currentTimeMillis(),
            mode = mode,
            id = chatID,
            userIndex = currentUserIndex
        )
        Log.d("chatVM", "Adding new chat: $newChat")
        Log.d("chatVM", "service name: $defaultService")
        vmChat.addChat(newChat)
        Log.d("chatVM", "added new chat: $newChat")

        try {
            val response= when(mode){
                0, 2 -> {
                    chatService.sendMessage(
                        serviceName = defaultService.serviceName,
                        secretCode = defaultService.secretKey,
                        message = message
                    )
                }
                1 -> {
                    chatService.getChatReply(url = "https://api-jjtysweprq-el.a.run.app/symptoms/$message")
                }
                3, 4, 5 -> {
                    chatService.sendTaskMsg(
                        serviceName = defaultService.serviceName,
                        secretCode = defaultService.secretKey,
                        message = message,
                        task = mode
                    )
                }
                else -> {
                    chatService.getChatReply(url = "https://api-jjtysweprq-el.a.run.app/symptoms/$message")
                }
            }
            val resultAPI= messageEntity(
                message = response.message!!,
                isBotMessage = true,
                timestamp = System.currentTimeMillis(),
                isError = false,
                chatId = chatID
            )
            _listMessages.emit(resultAPI)
            _messageCount.value += 1
            vmChat.addMessages(resultAPI)
            _isChatbotResponding.value= false
            Log.d("errorChat", response.message)
        } catch (e: Exception){
            when (e) {
                is retrofit2.HttpException -> {
                    val errorMessage = when (e.code()) {
                        504 -> "Gateway timeout: The server is not responding"
                        500 -> "Internal server error"
                        404 -> "Resource not found"
                        401 -> {
                            val errorBody = e.response()!!.errorBody()?.string()
                            try {
                                val jsonObject = JSONObject(errorBody!!)
                                jsonObject.optString("message", "Unauthorized: Invalid service name or secret code")
                            } catch (jsonException: Exception) {
                                "Unauthorized: Invalid service name or secret code"
                            }
                        }
                        // Add more cases for other HTTP error codes
                        else -> "An HTTP error occurred: ${e.code()} ${e.message()}"
                    }
                    Log.d("errorChat", e.stackTrace.toString())
                    Log.d("errorChat", errorMessage)
                    Log.d("errorChat", e.toString())
                    val errorData= messageEntity(
                        message = errorMessage,
                        isBotMessage = true,
                        timestamp = System.currentTimeMillis(),
                        isError = true,
                        chatId = chatID,
                        hasAttachments = timeStampAttachment
                    )
                    _listMessages.emit(errorData)
                    vmChat.addMessages(errorData)
                    if (e.code()==404 || e.code()==401){
                        _messageCount.value += 1
                        _isChatbotResponding.value= false
                        return
                    }
                }
                is IOException -> {
                    Log.d("errorChat", e.stackTrace.toString())
                    Log.d("errorChat", "Network error: ${e.message}")
                    Log.d("errorChat", e.toString())
                    val errorData= messageEntity(
                        message = "Network error occurred, please check your connection",
                        isBotMessage = true,
                        timestamp = System.currentTimeMillis(),
                        isError = true,
                        chatId = chatID,
                        hasAttachments = timeStampAttachment
                    )
                    _listMessages.emit(errorData)
                    vmChat.addMessages(errorData)
                } else -> {
                    Log.d("errorChat", e.stackTrace.toString())
                    Log.d("errorChat", e.message ?: "An unknown error occurred")
                    Log.d("errorChat", e.toString())
                    val errorData= messageEntity(
                        message = "An unknown error occurred, please try again later",
                        isBotMessage = true,
                        timestamp = System.currentTimeMillis(),
                        isError = true,
                        chatId = chatID,
                        hasAttachments = timeStampAttachment
                    )
                    _listMessages.emit(errorData)
                    vmChat.addMessages(errorData)
                }
            }
            _messageCount.value += 1
            _isChatbotResponding.value= false
        }
    }

    private fun calculateExponentialBackoff(retryCount: Int): Long {
        // Exponential backoff formula: base * 2^retryCount
        val base = 1000L // Base delay in milliseconds (1 second)
        val delayMillis = base * (2.0).pow(retryCount.toDouble()).toLong()
        return delayMillis.coerceAtMost(30000L) // Cap the maximum delay to 30 seconds
    }

    fun importChatWithAttachment(
        lastScanData: scanHistory,
        messageEntity: messageEntity){
        _lastScanAsAttachment.value= lastScanData
        _messageWithAttachment.value= messageEntity
    }

    fun resetChatWAttachment(){
        _messageWithAttachment.value= _messageWithAttachment.value.copy(
            message = "",
            isBotMessage = false,
            timestamp = System.currentTimeMillis(),
            isError = false,
        )
    }
}