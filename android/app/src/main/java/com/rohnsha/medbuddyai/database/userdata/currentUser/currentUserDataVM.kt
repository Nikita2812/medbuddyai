package com.rohnsha.medbuddyai.database.userdata.currentUser

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rohnsha.medbuddyai.database.userdata.userDataDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class currentUserDataVM(application: Application): AndroidViewModel(application) {

    private val repo: currentUserRepo
    private val dao: currentUserDAO

    private val _defaultUserIndex= MutableStateFlow(1)
    val defaultUserIndex= _defaultUserIndex.asStateFlow()

    private val _userCount= MutableStateFlow(0)
    val userCount= _userCount.asStateFlow()

    private val _userName= MutableStateFlow("")
    val userName= _userName.asStateFlow()

    init {
        dao= userDataDB.getUserDBRefence(application).currentUserDAO()
        repo= currentUserRepo(dao)
        viewModelScope.launch {
            initializeDefaultUser()
        }
    }

    private suspend fun initializeDefaultUser(){
        val users= getAllUsers()
        val defaultUser= users.find { it.isDefaultUser }
        _defaultUserIndex.value= defaultUser?.index ?: users.firstOrNull()?.index ?: 0
    }

    suspend fun addDataCurrentUser(data: fieldValueDC){
        repo.addUserData(data)
        initializeDefaultUser()
        countEntries()
    }

    suspend fun getQueryData(isDefaultUser: Boolean) {
        _userName.value= repo.searchQuery(isDefaultUser)?.username ?: ""
    }

    suspend fun isDefaultUser(userIndex: Int): Boolean{
        val defaultUser = repo.searchQuery(true)
        return defaultUser != null && defaultUser.index == userIndex
    }

    suspend fun getAllUsers(): List<fieldValueDC>{
        return repo.getAllUsers()
    }

    suspend fun getUserInfo(userIndex: Int): fieldValueDC{
        return repo.getUserInfo(userIndex)
    }

    suspend fun deleteUser(userIndex: Int){
        repo.deleteUser(userIndex)
    }

    fun switchDefafultUser(index: Int){
        _defaultUserIndex.value= index
    }
    suspend fun countEntries() {
        _userCount.value= repo.countEntries()
    }
}