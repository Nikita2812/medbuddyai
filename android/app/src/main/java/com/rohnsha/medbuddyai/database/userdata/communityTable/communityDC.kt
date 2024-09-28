package com.rohnsha.medbuddyai.database.userdata.communityTable

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "communityPosts")
data class Post(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val author: String,
    val content: String,
    val timestamp: String,
)

@Entity(tableName = "communityReply")
data class Reply(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val author: String,
    val content: String,
    val timestamp: String
)

data class postWithReply(
    val post: Post,
    val replies: List<Reply>
)

data class postWithState(
    val post: Post,
    val isPosted: Boolean
)
