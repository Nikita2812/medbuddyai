package com.rohnsha.medbuddyai.domain.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import com.rohnsha.medbuddyai.api.community.commVerifyObj.commVerifyService
import com.rohnsha.medbuddyai.database.userdata.communityTable.Post
import com.rohnsha.medbuddyai.database.userdata.communityTable.Reply
import com.rohnsha.medbuddyai.database.userdata.communityTable.communityDBVM
import com.rohnsha.medbuddyai.database.userdata.communityTable.postWithReply
import com.rohnsha.medbuddyai.database.userdata.communityTable.postWithState
import com.rohnsha.medbuddyai.ui.theme.customRed
import com.rohnsha.medbuddyai.ui.theme.customYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class communityVM: ViewModel() {

    private lateinit var _auth: FirebaseAuth
    private lateinit var _firestoreRef: DatabaseReference
    private lateinit var _username: String
    private lateinit var _communityDBVM: communityDBVM
    private lateinit var _snackBarToggleVM: snackBarToggleVM
    private val _postFeed= MutableStateFlow<List<Post>>(emptyList())
    private val _replyFeed= MutableStateFlow<List<Reply>>(emptyList())
    private val _postCount= MutableStateFlow(0L)
    private val _isCurrentlyPosting= MutableStateFlow(false)
    private val _isCurrentlyReplying= MutableStateFlow(false)
    private var retryCount = 0
    private val _postedList= MutableStateFlow<List<postWithState>>(emptyList())
    private val _replyList= MutableStateFlow<List<Reply>>(emptyList())

    val isCurrentlyPosting= _isCurrentlyPosting.asStateFlow()
    val isCurrentlyReplying= _isCurrentlyReplying.asStateFlow()
    val postedList= _postedList.asStateFlow()
    val replyList= _replyList.asStateFlow()

    fun initialize(
        instance: FirebaseAuth,
        dbReference: DatabaseReference,
        username: String,
        communityDBVModel: communityDBVM,
        snackBarToggleVM: snackBarToggleVM
    ){
        _auth= instance
        _firestoreRef= dbReference
        Log.d("loginStatus", _auth.currentUser?.uid ?: "nullified")
        _username= username
        _communityDBVM= communityDBVModel
        _snackBarToggleVM= snackBarToggleVM
    }

    suspend fun addReply(
        replyContent: String,
        postID: String,
        onCompleteLambda: (Reply) -> Unit,
        resetValues: () -> Unit
    ){
        resetValues()
        _isCurrentlyReplying.value= true
        val _tempRID= "${postID}: ${_replyList.value.size+1}"
        _replyList.value += Reply(
            id = _tempRID,
            content = replyContent,
            timestamp = System.currentTimeMillis().toString(),
            author = _username
        )
        try {
            val response= commVerifyService.isAllowedToBePosted(replyContent)
            if (response.isMedicalText){
                viewModelScope.launch {
                    if (_auth.currentUser!=null){
                        _firestoreRef.child("replies").child(_username).child(postID).get()
                            .addOnSuccessListener {
                                val replyCount= it.childrenCount
                                val replyID= "${postID}: ${replyCount+1}"
                                Log.d("authUserActionInner", replyCount.toString())
                                Log.d("authUserAction", replyCount.toString())
                                val reply= Reply(
                                    id = replyID,
                                    content = replyContent,
                                    timestamp = System.currentTimeMillis().toString(),
                                    author = _username
                                )
                                _firestoreRef
                                    .child("replies")
                                    .child(_username)
                                    .child(postID)
                                    .child(replyID)
                                    .setValue(reply)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            viewModelScope.launch {
                                                _communityDBVM.addReplies(listOf(reply))
                                            }
                                            onCompleteLambda(reply)
                                            _isCurrentlyReplying.value= false
                                            _replyList.value = _replyList.value.filter { it.id != _tempRID }
                                            Log.d("authUserAction", "Reply successful")
                                        } else {
                                            Log.e("authUserAction", "Reply unsuccessful", task.exception)
                                        }
                                    }
                            }
                    }
                }
            } else{
                _snackBarToggleVM.SendToast(
                    message = response.message,
                    indicator_color = customYellow
                )
                _isCurrentlyReplying.value= false
                _replyList.value = _replyList.value.filter { it.id != _tempRID }
            }
        } catch (e: Exception){
            Log.d("errorChat", e.stackTrace.toString())
            Log.d("errorChat", e.message ?: "An unknown error occurred")
            Log.d("errorChat", e.toString())
            _isCurrentlyReplying.value= false
            _replyList.value = _replyList.value.filter { it.id != _tempRID }
            _snackBarToggleVM.SendToast(
                message = "An unknown error occurred",
                indicator_color = customRed
            )
        }
    }

    fun post(
        content: String,
        onCompleteLambda: (Post) -> Unit,
        resetValues: () -> Unit = {},
        postCount: Long?= null,
        isRetry: Boolean= false
    ){
        resetValues()
        viewModelScope.launch {
            _isCurrentlyPosting.value= true
            _postCount.value= postCount ?: (_communityDBVM.getPostCountByUsername(_username) + _postedList.value.size + 1L)
            val localPostCount= if (isRetry) postCount else _postCount.value
            Log.d("postCount", _postCount.value.toString())
            val postID= "${_username}: ${_postCount.value}"
            val newPost= Post(
                id = postID,
                author = _username,
                content = content,
                timestamp = System.currentTimeMillis().toString(),
            )
            if (_postedList.value.none { it.post.id == newPost.id }) {
                val updatedList = (_postedList.value + postWithState(newPost, false))
                    .sortedByDescending { it.post.timestamp }
                _postedList.value = updatedList
            }
            Log.d("authUserAction", "post invoked")
            if (_auth.currentUser!=null){
                Log.d("authUsername", _username)
                try {
                    val response= commVerifyService.isAllowedToBePosted(content)
                    if (response.isMedicalText){
                        Log.d("authUserAction", newPost.toString())
                        _firestoreRef.child("posts").child(_username).child(postID).setValue(newPost)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("authUserAction", "Post successful")
                                    viewModelScope.launch {
                                        _communityDBVM.addPosts(listOf(newPost))
                                    }
                                    onCompleteLambda(newPost)
                                    _isCurrentlyPosting.value= false
                                    _postedList.value = _postedList.value.filter { it.post.id != newPost.id }
                                } else {
                                    Log.e("authUserAction", "Post unsuccessful", task.exception)
                                }
                                task.addOnFailureListener {
                                    Log.e("authUserAction", "unsuccessful")
                                }
                            }
                            .addOnFailureListener {
                                Log.e("authUserAction", "unsuccessful")
                            }
                            .addOnCanceledListener {
                                Log.e("authUserAction", "canceled")
                            }

                    } else{
                        _snackBarToggleVM.SendToast(
                            message = response.message,
                            indicator_color = customYellow
                        )
                        _isCurrentlyPosting.value= false
                        _postedList.value = _postedList.value.filter { it.post.id != newPost.id }
                    }
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
                                        jsonObject.optString("message", "Unknown error caused!")
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
                            if (e.code()!=404 || e.code()!=401){
                                val delayMillis = calculateExponentialBackoff(retryCount)
                                delay(delayMillis)
                                post(content, onCompleteLambda, postCount = localPostCount, isRetry = true)
                            }
                        }
                        is IOException -> {
                            Log.d("errorChat", e.stackTrace.toString())
                            Log.d("errorChat", "Network error: ${e.message}")
                            Log.d("errorChat", e.toString())
                            val delayMillis = calculateExponentialBackoff(retryCount)
                            delay(delayMillis)
                            post(content, onCompleteLambda, postCount = localPostCount, isRetry = true)
                        } else -> {
                            Log.d("errorChat", e.stackTrace.toString())
                            Log.d("errorChat", e.message ?: "An unknown error occurred")
                            Log.d("errorChat", e.toString())
                            val delayMillis = calculateExponentialBackoff(retryCount)
                            delay(delayMillis)
                            post(content, onCompleteLambda, postCount = localPostCount, isRetry = true)
                        }
                    }
                }

            } else {
                _snackBarToggleVM.SendToast(
                    message = "Please login to continue",
                    indicator_color = customRed,
                    icon = Icons.AutoMirrored.Outlined.Login
                )
                _isCurrentlyPosting.value= false
                _postedList.value = _postedList.value.filter { it.post.id != newPost.id }
            }
        }
    }

    fun getFeed(){
        viewModelScope.launch {
            val posts = mutableListOf<Post>()
            val replies= mutableListOf<Reply>()
            _firestoreRef.get()
                .addOnSuccessListener {
                    Log.d("dataSnap", it.toString())
                    for (childSnapshot in it.child("posts").children) {
                        for (postSnapshot in childSnapshot.children) {
                            val postData = postSnapshot.getValue<Map<String, String>>()

                            val author = postData?.get("author") ?: "Unknown"
                            val content = postData?.get("content") ?: "No content"
                            val timestamp= postData?.get("timestamp") ?: "Unnoticed"
                            val id= postData?.get("id") ?: "null"

                            val post = Post(
                                id = id,
                                author = author,
                                content = content,
                                timestamp = timestamp,
                            )
                            posts.add(post)
                        }
                    }
                    for (childSnapshot in it.child("replies").children) {
                        for (postSnapshot in childSnapshot.children) {
                            for (itemm in postSnapshot.children){
                                val postData = itemm.getValue<Map<String, String>>()
                                val id = postData?.get("id") ?: "Unknown"
                                val author = postData?.get("author") ?: "Unknown"
                                val content = postData?.get("content") ?: "Unknown"
                                val timestamp = postData?.get("timestamp") ?: "Unknown"

                                val reply= Reply(
                                    id, author, content, timestamp
                                )
                                replies.add(reply)
                                Log.d("dataSnap", "data: $reply")
                            }
                        }
                    }
                    _postFeed.value=posts
                    _replyFeed.value= replies
                    viewModelScope.launch {
                        _communityDBVM.clearPosts()
                        _communityDBVM.clearReplies()
                        _communityDBVM.addPosts(_postFeed.value)
                        _communityDBVM.addReplies(_replyFeed.value)
                    }
                    Log.d("dataSnapReplyPosts", "posts: ${_postFeed.value}\nreplies: ${_replyFeed.value}")
                }
        }
    }

    fun returnDomain(
        index: Int
    ): String{
        return when(index){
            1 -> "Lungs"
            2 -> "Brain"
            else -> ""
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun calculateTimeDifference(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val differenceInMillis = currentTime - timestamp

        val seconds = TimeUnit.MILLISECONDS.toSeconds(differenceInMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(differenceInMillis)

        return when {
            seconds < 60 -> "$seconds seconds ago"
            minutes < 60 -> "$minutes minutes ago"
            hours < 24 -> "$hours hours ago"
            else -> {
                val instant = Instant.ofEpochMilli(timestamp)
                val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
                formatter.format(dateTime)
            }
        }
    }

    fun mergePostReplies(postData: List<Post> = _postFeed.value, replyData: List<Reply> = _replyFeed.value, postID: String? = null): MutableList<postWithReply> {
        val postsWithReplies = mutableListOf<postWithReply>()
        for (post in postData) {
            val postReplies = if (postID == null) {
                replyData.filter { it.id?.contains(post.id ?: "") ?: false }
            } else {
                replyData.filter { it.id == post.id }
            }
            postsWithReplies.add(postWithReply(post, postReplies))
        }
        return postsWithReplies
    }

    private fun calculateExponentialBackoff(retryCount: Int): Long {
        // Exponential backoff formula: base * 2^retryCount
        val base = 1000L // Base delay in milliseconds (1 second)
        val delayMillis = base * (2.0).pow(retryCount.toDouble()).toLong()
        return delayMillis.coerceAtMost(30000L) // Cap the maximum delay to 30 seconds
    }

}