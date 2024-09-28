package com.rohnsha.medbuddyai.domain.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.Color
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rohnsha.medbuddyai.api.authUsername.usernameOBJ.usernameCheckService
import com.rohnsha.medbuddyai.database.userdata.currentUser.currentUserDataVM
import com.rohnsha.medbuddyai.database.userdata.currentUser.fieldValueDC
import com.rohnsha.medbuddyai.database.userdata.keys.keyDC
import com.rohnsha.medbuddyai.database.userdata.keys.keyVM
import com.rohnsha.medbuddyai.database.userdata.userDataDB
import com.rohnsha.medbuddyai.domain.dataclass.userInfoDC
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class userAuthVM: ViewModel() {

    private lateinit var _auth: FirebaseAuth
    private lateinit var _firestoreRef: DatabaseReference
    private lateinit var _currentUserVM: currentUserDataVM
    private lateinit var _communityVM: communityVM
    private lateinit var _keyVM: keyVM
    private lateinit var _username: String
    private lateinit var credentialManager: CredentialManager

    fun initialize(
        instance: FirebaseAuth,
        dbReference: DatabaseReference,
        currentUserVM: currentUserDataVM,
        keyVM: keyVM,
        username: String,
        communityVM: communityVM,
        credMan: CredentialManager
    ){
        _auth = instance
        _firestoreRef = dbReference
        _currentUserVM= currentUserVM
        _keyVM= keyVM
        _username= username
        _communityVM= communityVM
        credentialManager= credMan
    }

    suspend fun isUsernameValid(username: String): Boolean {
        if (username==""){
            return false
        }

        val dynamicURL= "https://api-jjtysweprq-el.a.run.app/getUsername/$username"
        return withContext(viewModelScope.coroutineContext){
            usernameCheckService.getUsernameDetails(dynamicURL).username!=null
        }
    }

    fun isUserUnAuthenticated(): Boolean {
        if(_auth.currentUser == null){
            return true
        }
        return false
    }

    fun loginUser(
        password: String,
        email: String,
        onSuccess: () -> Unit,
        snackBarToggleVM: snackBarToggleVM
        ) {
        _auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _firestoreRef.child("users").child(it.user?.uid.toString()).get()
                    .addOnSuccessListener {
                        Log.d("loginSuccess", it.toString())
                        viewModelScope.launch {
                            val userInfo= fieldValueDC(
                                username = it.child("username").value.toString(),
                                fname = it.child("firstName").value.toString(),
                                lname = it.child("lastName").value.toString(),
                                isDefaultUser = true
                            )
                            Log.d("loginSuccess", userInfo.toString())
                            _currentUserVM.addDataCurrentUser(
                                userInfo
                            )
                            _keyVM.updateKeySecretPair(
                                listOf(keyDC(serviceName = "swasthai", secretKey = userInfo.username))
                            ) { }
                            onSuccess()
                            _communityVM.getFeed()
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        Log.d("loginError", it.printStackTrace().toString())
                        Log.d("loginError", it.message.toString())
                    }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Log.d("loginError", exception.message.toString())
                snackBarToggleVM.SendToast(
                    message = exception.message.toString(),
                    indicator_color = Color.Red,
                    icon = Icons.Outlined.Warning,
                )
            }
    }

    suspend fun registerUser(
        password: String,
        email: String,
        onSucess: () -> Unit,
        fname: String,
        lname: String,
        username: String,
        snackBarToggleVM: snackBarToggleVM,
        context: Context
    ) {
        _auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userInfo= userInfoDC(
                    firstName = fname,
                    lastName = lname,
                    username = username
                )
                _firestoreRef.child("users").child(it.user?.uid.toString()).setValue(userInfo)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            _currentUserVM.addDataCurrentUser(
                                fieldValueDC(
                                    username = username,
                                    fname = fname,
                                    lname = lname,
                                    isDefaultUser = true
                                )
                            )
                            _keyVM.updateKeySecretPair(
                                listOf(keyDC(serviceName = "swasthai", secretKey = username))
                            ){  }
                            saveCredential(
                                activity = context,
                                email = email,
                                password = password
                            )
                        }
                        onSucess()
                        _communityVM.getFeed()
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        Log.d("loginError", it.printStackTrace().toString())
                        Log.d("loginError", it.message.toString())
                    }
                _firestoreRef.child("usernames").push().setValue(username)
            }
            .addOnFailureListener {
                it.printStackTrace()
                Log.d("loginError", it.printStackTrace().toString())
                Log.d("loginError", it.message.toString())
                snackBarToggleVM.SendToast(
                    message = it.message.toString(),
                    indicator_color = Color.Red,
                    icon = Icons.Outlined.Warning,
                )
            }
    }

    private suspend fun saveCredential(activity: Context, email: String, password: String) {
        try {
            //Ask the user for permission to add the credentials to their store
            credentialManager.createCredential(
                request = CreatePasswordRequest(email, password),
                context = activity,
            )
            Log.v("CredentialTest", "Credentials successfully added")
        }
        catch (e: CreateCredentialCancellationException) {
            //do nothing, the user chose not to save the credential
            Log.v("CredentialTest", "User cancelled the save")
        }
        catch (e: CreateCredentialException) {
            Log.v("CredentialTest", "Credential save error", e)
        }
    }

    suspend fun retrievePassword(
        context: Context,
        snackBarToggleVM: snackBarToggleVM,
        onSucess: () -> Unit
    ) {
        try {
            val getCredRequest = GetCredentialRequest(
                listOf(GetPasswordOption())
            )

//Show the user a dialog allowing them to pick a saved credential
            val credentialResponse = credentialManager.getCredential(
                request = getCredRequest,
                context = context,
            )

            val creds= credentialResponse.credential as? PasswordCredential
            creds.let {
                if (it != null) {
                    loginUser(
                        password = it.password,
                        email = it.id,
                        onSuccess = onSucess,
                        snackBarToggleVM = snackBarToggleVM
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("CredentialTest", "Error getting credential", e)
            snackBarToggleVM.SendToast(
                message = "Error getting credential",
                indicator_color = Color.Red,
                icon = Icons.Outlined.Warning,
            )
        }

    }

    suspend fun deletaAccount(
        context: Context,
        navController: NavHostController,
        password: String,
        snackBarToggleVM: snackBarToggleVM
    ){
        if (!isUserUnAuthenticated()){
            _auth.currentUser?.let { currentUser ->
                try {
                    withContext(Dispatchers.IO) {
                        val credential = EmailAuthProvider.getCredential(currentUser.email!!, password)
                        currentUser.reauthenticate(credential).await()
                        deleteUserData()
                        currentUser.delete().await()
                        println("User account deleted successfully")
                        deletePostReply("posts")
                        deletePostReply("replies")
                        val db= userDataDB.getUserDBRefence(context = context)
                        db.runInTransaction{
                            db.clearAllTables()
                        }
                    }
                    withContext(Dispatchers.Main){
                        navController.navigate(route = bottomNavItems.LogoWelcome.isLocalAccEnbld()){
                            popUpTo(navController.graph.startDestinationId){
                                inclusive = true
                            }
                        }
                    }
                } catch (e: Exception) {
                    snackBarToggleVM.SendToast(
                        message = e.message.toString(),
                        indicator_color = Color.Red,
                        icon = Icons.Outlined.Warning,
                    )
                    Log.d("accMgmt", "Error deleting user account: ${e.message}")
                }
            }
        }
    }

    private suspend fun deleteUserData(){
        _firestoreRef.child("users").child(_auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        snapshot.ref.removeValue().addOnCompleteListener {
                            if (it.isSuccessful){
                                Log.d("accMgmt", "Child successfully deleted")
                            } else {
                                Log.d("accMgmt", "Failed to delete child: ${it.exception?.message}")
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("accMgmt", "Error checking for child: ${error.message}")
                }

            })
    }

    private suspend fun deletePostReply(
        postOrReply: String
    ){
        _firestoreRef.child(postOrReply).child(_username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // The child exists, so let's delete it
                        snapshot.ref.removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("accMgmt", "Child successfully deleted")
                            } else {
                                Log.d("accMgmt", "Failed to delete child: ${task.exception?.message}")
                            }
                        }
                    } else {
                        Log.d("accMgmt", "Child does not exist")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("accMgmt", "Error checking for child: ${error.message}")
                }
            })
    }

    suspend fun signOut(context: Context, navController: NavHostController){
        if (!isUserUnAuthenticated()){
            _auth.signOut()
            withContext(Dispatchers.IO){
                val db= userDataDB.getUserDBRefence(context = context)
                db.runInTransaction{
                    db.clearAllTables()
                }
            }
            withContext(Dispatchers.Main){
                navController.navigate(route = bottomNavItems.LogoWelcome.isLocalAccEnbld()){
                    popUpTo(navController.graph.startDestinationId){
                        inclusive = true
                    }
                }
            }
        }
    }
}