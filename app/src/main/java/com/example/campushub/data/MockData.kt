package com.example.campushub.data

import com.example.campushub.data.model.Comment
import com.example.campushub.data.model.Post
import com.example.campushub.data.model.User
import com.example.campushub.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MockData {

    private val user1 = User(
        id = "1001",
        username = "student_01",
        nickname = "极客学长",
        avatarUrl = "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix",
        signature = "代码改变世界，Hub链接校园",
        school = "清华大学"
    )

    var currentUser: User? = null

    val users = mutableListOf(
        user1,
        User("1002", "user_test", "校园小明", "https://api.dicebear.com/7.x/avataaars/svg?seed=Aria", "热爱生活，发现美", "北京大学"),
        User("1003", "dev_hub", "Hub开发者", "https://api.dicebear.com/7.x/avataaars/svg?seed=Jack", "欢迎来到 CampusHub", "复旦大学")
    )

    val userPasswords = mutableMapOf(
        "student_01" to "123456",
        "user_test" to "123456",
        "dev_hub" to "123456"
    )

    val posts = mutableListOf(
        Post("p1", "今天食堂的红烧肉绝了！", "简直是本学期最佳，就在二食堂二楼，强烈推荐大家去试一试！", "极客学长", System.currentTimeMillis() - 7200000, likes = 128),
        Post("p2", "图书馆有人组队备考考研吗？", "寻找志同道合研友，坐标逸夫楼 302，大家一起监督学习。", "校园小明", System.currentTimeMillis() - 86400000, likes = 45),
        Post("p3", "关于校园运动会的最新通知", "由于天气原因，原定于本周五的运动会将顺延至下周一。", "Hub开发者", System.currentTimeMillis() - 172800000, likes = 312)
    )

    private val _postsFlow = MutableStateFlow(posts.toList())
    val postsFlow: StateFlow<List<Post>> = _postsFlow.asStateFlow()

    fun notifyPostsChanged() {
        _postsFlow.value = posts.toList()
        PreferencesManager.savePosts(posts)
    }

    private val _comments = mutableMapOf<String, MutableList<Comment>>()

    val comments: Map<String, List<Comment>> get() = _comments

    fun getComments(postId: String): List<Comment> = _comments[postId] ?: emptyList()

    fun addComment(postId: String, comment: Comment) {
        _comments.getOrPut(postId) { mutableListOf() }.add(0, comment)
    }

    fun addReply(commentId: String, reply: Comment) {
        for ((_, commentList) in _comments) {
            val index = commentList.indexOfFirst { it.id == commentId }
            if (index >= 0) {
                val updatedReplies = commentList[index].replies.toMutableList()
                updatedReplies.add(0, reply)
                commentList[index] = commentList[index].copy(replies = updatedReplies)
                return
            }
        }
    }

    fun loadFromPreferences() {
        val savedUsers = PreferencesManager.loadRegisteredUsers()
        for ((user, password) in savedUsers) {
            val existingIndex = users.indexOfFirst { it.username == user.username }
            if (existingIndex < 0) {
                users.add(user)
            }
            userPasswords[user.username] = password
        }

        val savedPosts = PreferencesManager.loadPosts()
        if (savedPosts.isNotEmpty()) {
            posts.clear()
            posts.addAll(savedPosts)
        }
        notifyPostsChanged()
    }
}
