package com.rohnsha.medbuddyai.database.userdata.currentUser

class currentUserRepo(private val currentUser: currentUserDAO) {

    suspend fun addUserData(userData: fieldValueDC){
        currentUser.insert(userData)
    }

    suspend fun searchQuery(isDefaultUser: Boolean): fieldValueDC {
        return currentUser.getFieldData(isDefaultUser)
    }

    suspend fun getAllUsers(): List<fieldValueDC>{
        return currentUser.getAllUsers()
    }

    suspend fun getUserInfo(userIndex: Int): fieldValueDC{
        return  currentUser.getuserInformaton(userIndex)
    }

    suspend fun deleteUser(userIndex: Int){
        currentUser.deleteUser(userIndex)
    }

    suspend fun countEntries(): Int{
        return currentUser.countEntries()
    }

}