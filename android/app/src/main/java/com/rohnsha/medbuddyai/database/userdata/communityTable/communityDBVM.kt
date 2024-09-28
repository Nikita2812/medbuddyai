package com.rohnsha.medbuddyai.database.userdata.communityTable

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.rohnsha.medbuddyai.database.userdata.userDataDB

class communityDBVM(application: Application): AndroidViewModel(application) {

    private val communityRepo: communityRepo
    private val communityDAO: communityDAO

    init {
        communityDAO= userDataDB.getUserDBRefence(application).communityDAO()
        communityRepo= communityRepo(communityDAO)
    }

    suspend fun addPosts(posts: List<Post>){
        for (post in posts){
            communityRepo.addPosts(post)
        }
    }

    suspend fun addReplies(replies: List<Reply>){
        for (reply in replies){
            communityRepo.addReplies(reply)
        }
    }

    suspend fun clearPosts(){
        communityRepo.clearPosts()
    }

    suspend fun clearReplies(){
        communityRepo.clearReplies()
    }

    suspend fun filterReplies(postID: String) : List<Reply>{
        return communityRepo.filterReplies(postID)
    }

    suspend fun getPosts(): List<Post> {
        return communityRepo.getAllPosts()
    }

    suspend fun getReplies(): List<Reply> {
        return communityRepo.getAllReplies()
    }

    suspend fun getPostCountByUsername(username: String): Int{
        return  communityRepo.getPostCountByUsername(username)
    }

    suspend fun mergePostReplies(postID: String? = null): MutableList<postWithReply> {

        val postData = mutableListOf<Post>()
        val replyData= mutableListOf<Reply>()

        val postsList= getPosts()
        for (post in postsList){
            postData.add(post)
        }
        val replyList= getReplies()
        for (reply in replyList){
            replyData.add(reply)
        }
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
}