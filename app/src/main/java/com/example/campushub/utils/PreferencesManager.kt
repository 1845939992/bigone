package com.example.campushub.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.example.campushub.data.model.Post
import com.example.campushub.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

object PreferencesManager {

    private const val PREFS_NAME = "campus_hub_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_REMEMBER_PASSWORD = "remember_password"
    private const val KEY_AUTO_LOGIN = "auto_login"
    private const val KEY_REGISTERED_USERS = "registered_users"
    private const val KEY_POSTS = "posts"
    private const val KEY_AUTH_TOKEN = "auth_token"

    private var prefs: SharedPreferences? = null
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isLoggedIn.value = prefs?.getBoolean(KEY_AUTO_LOGIN, false) == true &&
                prefs?.getString(KEY_USERNAME, "")?.isNotBlank() == true
    }

    fun saveCredentials(username: String, password: String, rememberPassword: Boolean, autoLogin: Boolean = false) {
        prefs?.edit()?.apply {
            putString(KEY_USERNAME, username)
            if (rememberPassword) {
                putString(KEY_PASSWORD, encode(password))
            } else {
                remove(KEY_PASSWORD)
                putBoolean(KEY_AUTO_LOGIN, false)
            }
            putBoolean(KEY_REMEMBER_PASSWORD, rememberPassword)
            putBoolean(KEY_AUTO_LOGIN, autoLogin && rememberPassword)
            apply()
        }
        _isLoggedIn.value = autoLogin
    }

    fun saveRegisteredUser(user: User, password: String) {
        val usersJson = prefs?.getString(KEY_REGISTERED_USERS, "[]") ?: "[]"
        val usersArray = JSONArray(usersJson)
        var userExists = false
        for (i in 0 until usersArray.length()) {
            val obj = usersArray.getJSONObject(i)
            if (obj.getString("username") == user.username) {
                obj.put("password", encode(password))
                obj.put("nickname", user.nickname)
                obj.put("avatarUrl", user.avatarUrl)
                obj.put("signature", user.signature)
                obj.put("school", user.school)
                userExists = true
                break
            }
        }
        if (!userExists) {
            val obj = JSONObject().apply {
                put("id", user.id)
                put("username", user.username)
                put("password", encode(password))
                put("nickname", user.nickname)
                put("avatarUrl", user.avatarUrl)
                put("signature", user.signature)
                put("school", user.school)
            }
            usersArray.put(obj)
        }
        prefs?.edit()?.putString(KEY_REGISTERED_USERS, usersArray.toString())?.apply()
    }

    fun loadRegisteredUsers(): List<Pair<User, String>> {
        val usersJson = prefs?.getString(KEY_REGISTERED_USERS, "[]") ?: "[]"
        val result = mutableListOf<Pair<User, String>>()
        try {
            val usersArray = JSONArray(usersJson)
            for (i in 0 until usersArray.length()) {
                val obj = usersArray.getJSONObject(i)
                val user = User(
                    id = obj.getString("id"),
                    username = obj.getString("username"),
                    nickname = obj.optString("nickname", ""),
                    avatarUrl = obj.optString("avatarUrl", ""),
                    signature = obj.optString("signature", ""),
                    school = obj.optString("school", "")
                )
                val password = decode(obj.optString("password", ""))
                result.add(user to password)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun getSavedUsername(): String {
        return prefs?.getString(KEY_USERNAME, "") ?: ""
    }

    fun getSavedPassword(): String {
        val encoded = prefs?.getString(KEY_PASSWORD, "") ?: ""
        return if (encoded.isNotBlank()) decode(encoded) else ""
    }

    fun isRememberPassword(): Boolean {
        return prefs?.getBoolean(KEY_REMEMBER_PASSWORD, false) ?: false
    }

    fun isAutoLogin(): Boolean {
        return prefs?.getBoolean(KEY_AUTO_LOGIN, false) ?: false
    }

    fun clearLoginState() {
        prefs?.edit()?.apply {
            putBoolean(KEY_AUTO_LOGIN, false)
            remove(KEY_AUTH_TOKEN)
            apply()
        }
        _isLoggedIn.value = false
    }

    fun clearAll() {
        prefs?.edit()?.clear()?.apply()
        _isLoggedIn.value = false
    }

    fun saveToken(token: String) {
        prefs?.edit()?.putString(KEY_AUTH_TOKEN, token)?.apply()
    }

    fun getToken(): String? {
        return prefs?.getString(KEY_AUTH_TOKEN, null)
    }

    fun clearToken() {
        prefs?.edit()?.remove(KEY_AUTH_TOKEN)?.apply()
    }

    fun savePosts(posts: List<Post>) {
        val postsArray = JSONArray()
        for (post in posts) {
            val obj = JSONObject().apply {
                put("id", post.id)
                put("title", post.title)
                put("content", post.content)
                put("author", post.author)
                put("timestamp", post.timestamp)
                put("likes", post.likes)
                put("isLiked", post.isLiked)
                put("isFavorite", post.isFavorite)
                val imagesArray = JSONArray()
                for (url in post.imageUrls) {
                    imagesArray.put(url)
                }
                put("imageUrls", imagesArray)
            }
            postsArray.put(obj)
        }
        prefs?.edit()?.putString(KEY_POSTS, postsArray.toString())?.apply()
    }

    fun loadPosts(): List<Post> {
        val postsJson = prefs?.getString(KEY_POSTS, null)
        if (postsJson == null) return emptyList()
        val result = mutableListOf<Post>()
        try {
            val postsArray = JSONArray(postsJson)
            for (i in 0 until postsArray.length()) {
                val obj = postsArray.getJSONObject(i)
                val imagesArray = obj.optJSONArray("imageUrls")
                val images = mutableListOf<String>()
                if (imagesArray != null) {
                    for (j in 0 until imagesArray.length()) {
                        images.add(imagesArray.getString(j))
                    }
                }
                val post = Post(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    content = obj.getString("content"),
                    author = obj.getString("author"),
                    timestamp = obj.getLong("timestamp"),
                    imageUrls = images,
                    likes = obj.optInt("likes", 0),
                    isLiked = obj.optBoolean("isLiked", false),
                    isFavorite = obj.optBoolean("isFavorite", false)
                )
                result.add(post)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun encode(input: String): String {
        return Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    private fun decode(input: String): String {
        return try {
            String(Base64.decode(input, Base64.NO_WRAP), Charsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }
}
