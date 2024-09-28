package com.rohnsha.medbuddyai.database.userdata.currentUser

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currentUserTable")
data class fieldValueDC(
    @PrimaryKey(autoGenerate = true)
    val index: Int=0,
    val username: String,
    val fname: String,
    val lname: String,
    val isDefaultUser: Boolean
)
