package com.rohnsha.medbuddyai.database.userdata.communityTable

class communityRepo(private val communityDAO: communityDAO) {

    suspend fun addPosts(post: Post){
        communityDAO.insertPost(post)
    }

    suspend fun addReplies(reply: Reply){
        communityDAO.insertReply(reply)
    }

    suspend fun getAllPosts(): List<Post>{
        return communityDAO.getAllPosts()
    }

    suspend fun getAllReplies(): List<Reply>{
        return communityDAO.getAllReplies()
    }

    suspend fun getPostCountByUsername(username: String): Int{
        return  communityDAO.getPostCountByUsername(username = username)
    }

    suspend fun clearPosts(){
        communityDAO.clearAllPosts()
    }

    suspend fun clearReplies(){
        communityDAO.clearAllReplies()
    }

    suspend fun filterReplies(postID: String): List<Reply>{
        return communityDAO.filterReplies(postID)
    }
}