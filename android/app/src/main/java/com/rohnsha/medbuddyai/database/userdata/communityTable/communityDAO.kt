package com.rohnsha.medbuddyai.database.userdata.communityTable

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface communityDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPost(post: Post)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReply(reply: Reply)

    @Query("SELECT * FROM communityPosts")
    suspend fun getAllPosts(): List<Post>

    @Query("SELECT * FROM communityReply")
    suspend fun getAllReplies(): List<Reply>

    @Query("select count(*) from communityPosts where author like :username")
    suspend fun getPostCountByUsername(username: String): Int

    @Query("DELETE FROM communityPosts")
    suspend fun clearAllPosts()

    @Query("DELETE FROM communityReply")
    suspend fun clearAllReplies()

    @Query("select * from communityReply where id like :postID || ':%'")
    suspend fun filterReplies(postID: String): List<Reply>
}