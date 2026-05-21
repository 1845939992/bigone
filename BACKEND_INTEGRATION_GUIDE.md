# CampusHub 后端接口接入方案

> 本文档记录了接入后端 API 所需的全部决策和步骤。  
> 核心原则：**只需替换 Repository 实现层，ViewModel 和 UI 层零改动，功能完全不变。**

---

## 一、需要新增的依赖

在 `app/build.gradle.kts` 的 `dependencies {}` 中添加：

```kotlin
// Retrofit 网络请求
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")

// OkHttp 底层 HTTP 客户端
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```

---

## 二、需要新增的文件（5 个）

### 2.1 `data/api/ApiService.kt` — Retrofit 接口定义

```kotlin
package com.example.campushub.data.api

import com.example.campushub.data.model.*
import retrofit2.http.*

interface ApiService {

    // ========== 用户模块 ==========

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<User>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<User>

    @GET("user/me")
    suspend fun getCurrentUser(): ApiResponse<User>

    @PUT("user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponse<User>

    @PUT("user/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): ApiResponse<Unit>

    // ========== 帖子模块 ==========

    @GET("posts")
    suspend fun getPosts(): ApiResponse<List<Post>>

    @GET("posts/{postId}")
    suspend fun getPostById(@Path("postId") postId: String): ApiResponse<Post>

    @GET("posts/{postId}/comments")
    suspend fun getComments(@Path("postId") postId: String): ApiResponse<List<Comment>>

    @GET("posts/author/{author}")
    suspend fun getPostsByAuthor(@Path("author") author: String): ApiResponse<List<Post>>

    @GET("posts/favorites")
    suspend fun getFavoritePosts(): ApiResponse<List<Post>>

    @GET("posts/search")
    suspend fun searchPosts(@Query("q") query: String): ApiResponse<List<Post>>

    @POST("posts")
    suspend fun createPost(@Body request: CreatePostRequest): ApiResponse<Post>

    @PUT("posts/{postId}")
    suspend fun updatePost(@Path("postId") postId: String, @Body request: UpdatePostRequest): ApiResponse<Post>

    @POST("posts/{postId}/comments")
    suspend fun addComment(@Path("postId") postId: String, @Body request: AddCommentRequest): ApiResponse<Comment>

    @POST("comments/{commentId}/reply")
    suspend fun addReply(@Path("commentId") commentId: String, @Body request: AddCommentRequest): ApiResponse<Comment>

    @DELETE("posts/{postId}")
    suspend fun deletePost(@Path("postId") postId: String): ApiResponse<Unit>

    @POST("posts/{postId}/like")
    suspend fun toggleLike(@Path("postId") postId: String): ApiResponse<Unit>

    @POST("posts/{postId}/favorite")
    suspend fun toggleFavorite(@Path("postId") postId: String): ApiResponse<Unit>
}
```

> **注意**：以上路径为参考模板。根据实际后端接口调整 URL 路径。

### 2.2 `data/api/ApiModels.kt` — 请求体 / 响应体模型

```kotlin
package com.example.campushub.data.api

// 通用响应包装
data class ApiResponse<T>(
    val code: Int,       // 0 = 成功
    val message: String,
    val data: T?
)

// ========== 请求体 ==========

data class LoginRequest(val username: String, val password: String)
data class RegisterRequest(val username: String, val password: String)
data class UpdateProfileRequest(val nickname: String, val signature: String, val school: String)
data class ChangePasswordRequest(val oldPassword: String, val newPassword: String)
data class CreatePostRequest(val title: String, val content: String, val author: String, val imageUrls: List<String> = emptyList())
data class UpdatePostRequest(val title: String, val content: String, val imageUrls: List<String> = emptyList())
data class AddCommentRequest(val content: String, val author: String)
```

> 如果后端 JSON 字段名与 Kotlin 属姓名不一致，在 `data/model/*.kt` 的数据类字段上添加 `@SerializedName("xxx")` 注解。

### 2.3 `data/repository/impl/ApiUserRepository.kt` — UserRepository 的 API 实现

```kotlin
package com.example.campushub.data.repository.impl

import com.example.campushub.data.api.ApiResponse
import com.example.campushub.data.api.ApiService
import com.example.campushub.data.model.User
import com.example.campushub.data.repository.UserRepository
import com.example.campushub.utils.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ApiUserRepository(private val api: ApiService) : UserRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    override suspend fun login(username: String, password: String): Result<User> {
        val response = api.login(LoginRequest(username, password))
        return if (response.code == 0 && response.data != null) {
            _currentUser.value = response.data
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message))
        }
    }

    override suspend fun register(username: String, password: String): Result<User> {
        val response = api.register(RegisterRequest(username, password))
        return if (response.code == 0 && response.data != null) {
            _currentUser.value = response.data
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message))
        }
    }

    override fun getCurrentUser(): Flow<User?> = _currentUser.asStateFlow()

    override suspend fun updateProfile(nickname: String, signature: String, school: String): Result<User> {
        val response = api.updateProfile(UpdateProfileRequest(nickname, signature, school))
        return if (response.code == 0 && response.data != null) {
            _currentUser.value = response.data
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message))
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        val response = api.changePassword(ChangePasswordRequest(oldPassword, newPassword))
        return if (response.code == 0) {
            Result.success(Unit)
        } else {
            Result.failure(Exception(response.message))
        }
    }

    override fun logout() {
        _currentUser.value = null
        PreferencesManager.clearLoginState()
    }
}
```

### 2.4 `data/repository/impl/ApiPostRepository.kt` — PostRepository 的 API 实现

```kotlin
package com.example.campushub.data.repository.impl

import com.example.campushub.data.api.ApiService
import com.example.campushub.data.model.Comment
import com.example.campushub.data.model.Post
import com.example.campushub.data.repository.PostRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ApiPostRepository(private val api: ApiService) : PostRepository {

    private val _postsFlow = MutableStateFlow<List<Post>>(emptyList())
    val postsFlow: Flow<List<Post>> = _postsFlow.asStateFlow()

    // 轮询首页帖子（每 10 秒刷新一次）
    // 可选方案：如果后端支持 WebSocket / SSE，改用事件驱动
    override fun getPosts(): Flow<List<Post>> = flow {
        while (true) {
            val response = api.getPosts()
            if (response.code == 0 && response.data != null) {
                _postsFlow.value = response.data
                emit(response.data)
            }
            delay(10_000)  // 轮询间隔
        }
    }

    override fun getPostById(postId: String): Flow<Post?> = flow {
        val response = api.getPostById(postId)
        if (response.code == 0) emit(response.data)
        else emit(null)
    }

    override fun getComments(postId: String): Flow<List<Comment>> = flow {
        val response = api.getComments(postId)
        if (response.code == 0 && response.data != null) emit(response.data)
        else emit(emptyList())
    }

    override fun getPostsByAuthor(author: String): Flow<List<Post>> =
        postsFlow.map { it.filter { post -> post.author == author } }

    override fun getFavoritePosts(): Flow<List<Post>> =
        postsFlow.map { it.filter { post -> post.isFavorite } }

    override fun searchPosts(query: String): Flow<List<Post>> = flow {
        val response = api.searchPosts(query)
        if (response.code == 0 && response.data != null) emit(response.data)
        else emit(emptyList())
    }

    override suspend fun createPost(title: String, content: String, author: String, imageUrls: List<String>): Result<Post> {
        val response = api.createPost(CreatePostRequest(title, content, author, imageUrls))
        return if (response.code == 0 && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message))
        }
    }

    override suspend fun updatePost(postId: String, title: String, content: String, imageUrls: List<String>): Result<Post> {
        val response = api.updatePost(postId, UpdatePostRequest(title, content, imageUrls))
        return if (response.code == 0 && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message))
        }
    }

    override suspend fun addComment(postId: String, content: String, author: String): Result<Comment> {
        val response = api.addComment(postId, AddCommentRequest(content, author))
        return if (response.code == 0 && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message))
        }
    }

    override suspend fun addReply(commentId: String, content: String, author: String): Result<Comment> {
        val response = api.addReply(commentId, AddCommentRequest(content, author))
        return if (response.code == 0 && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message))
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        val response = api.deletePost(postId)
        return if (response.code == 0) Result.success(Unit)
        else Result.failure(Exception(response.message))
    }

    override suspend fun toggleLike(postId: String): Result<Unit> {
        val response = api.toggleLike(postId)
        return if (response.code == 0) Result.success(Unit)
        else Result.failure(Exception(response.message))
    }

    override suspend fun toggleFavorite(postId: String): Result<Unit> {
        val response = api.toggleFavorite(postId)
        return if (response.code == 0) Result.success(Unit)
        else Result.failure(Exception(response.message))
    }
}
```

> **轮询优化**：如果首页不需要实时刷新，可以将 `getPosts()` 改为一次性 `flow{}` + 手动调用 `refreshPosts()`。

### 2.5 `data/api/RetrofitClient.kt` — Retrofit 单例工厂

```kotlin
package com.example.campushub.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.campushub.utils.PreferencesManager

object RetrofitClient {

    // ======== 部署时修改为实际后端 URL ========
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    // 可注入 Token 拦截器
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
        // 如果有 Token，在请求头携带
        // request.addHeader("Authorization", "Bearer ${PreferencesManager.getToken()}")
        chain.proceed(request.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val apiService: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}
```

---

## 三、需要修改的现有文件（6 个 ViewModel）

每个 ViewModel 的默认构造函数值从 `MockXxxRepository()` 改为 `RetrofitClient.apiService` 构造的 API 实现：

| ViewModel | 修改点 | 原默认值 | 新默认值 |
|-----------|--------|---------|---------|
| `LoginViewModel.kt` | `userRepository` 参数 | `MockUserRepository.getInstance()` | `ApiUserRepository(RetrofitClient.apiService)` |
| `RegisterViewModel.kt` | `userRepository` 参数 | `MockUserRepository.getInstance()` | `ApiUserRepository(RetrofitClient.apiService)` |
| `HomeViewModel.kt` | `postRepository` 参数 | `MockPostRepository()` | `ApiPostRepository(RetrofitClient.apiService)` |
| `PostViewModel.kt` | `postRepository` + `userRepository` | `MockPostRepository()` + `MockUserRepository.getInstance()` | `ApiPostRepository(api)` + `ApiUserRepository(api)` |
| `PostDetailViewModel.kt` | `postRepository` + `userRepository` | `MockPostRepository()` + `MockUserRepository.getInstance()` | `ApiPostRepository(api)` + `ApiUserRepository(api)` |
| `SearchViewModel.kt` | `postRepository` 参数 | `MockPostRepository()` | `ApiPostRepository(RetrofitClient.apiService)` |
| `ProfileViewModel.kt` | `postRepository` + `userRepository` | `MockPostRepository()` + `MockUserRepository.getInstance()` | `ApiPostRepository(api)` + `ApiUserRepository(api)` |

---

## 四、UI 层：零改动文件（不需要修改）

以下所有文件在接入后端后 **完全不需要任何改动**：

```
ui/screen/login/LoginScreen.kt
ui/screen/register/RegisterScreen.kt
ui/screen/home/HomeScreen.kt
ui/screen/post/PostScreen.kt
ui/screen/post/PostDetailScreen.kt
ui/screen/search/SearchScreen.kt
ui/screen/profile/ProfileScreen.kt
ui/component/PostCard.kt
navigation/NavGraph.kt
navigation/Screen.kt
MainActivity.kt
utils/PreferencesManager.kt          （仅保留本地 Token 存储）
data/model/User.kt / Post.kt / Comment.kt  （按需加 @SerializedName）
```

---

## 五、数据持久化策略变更

| 数据类型 | Mock 模式 | API 模式 |
|----------|----------|---------|
| 用户信息 | SharedPreferences JSON | 后端 DB → API 请求 |
| 帖子列表 | SharedPreferences JSON + StateFlow | 后端 DB → API 请求 + StateFlow |
| 评论 | MockData 内存 | 后端 DB → API 请求 |
| 登录凭证 | SharedPreferences(Base64 编码密码) | SharedPreferences(Token) |
| 自动登录标志 | SharedPreferences | SharedPreferences（保留不变） |
| 收藏/点赞态 | 随帖子持久化 | 后端维护，API 返回时自带 |

**接入 API 后，`PreferencesManager.savePosts()/loadPosts()` 可以移除或保留不调用**，因为数据源变为远程 API。

---

## 六、渐进式接入检查清单

| # | 步骤 | 验证方法 |
|---|------|---------|
| 1 | `build.gradle.kts` 添加 Retrofit/OkHttp 依赖 | Sync Gradle 无报错 |
| 2 | 根据后端实际接口调整 `ApiService.kt` 中的 URL 路径 | 对照 Swagger / 接口文档 |
| 3 | 根据需要调整 `data/model/*.kt` 中的字段注解 | 对照后端 JSON 响应结构 |
| 4 | 创建 `RetrofitClient.kt` 并配置 `BASE_URL` | 编译通过 |
| 5 | 创建 `ApiUserRepository.kt` 实现 `UserRepository` | 编译通过 |
| 6 | 创建 `ApiPostRepository.kt` 实现 `PostRepository` | 编译通过 |
| 7 | 逐个 ViewModel 切换默认实现 | 逐功能回归测试 |
| 8 | 全功能回归：登录→首页浏览→发帖→详情→评论→搜索→个人中心→退出 | App 无崩溃、功能一致 |
| 9 | 清理不再需要的 Mock 文件和 MockData 持久化代码 | 编译通过、无冗余引用 |

---

## 七、MockData 去留在接入后的处理

接入 API 后，`MockData.kt` 可整体删除（或保留用于离线开发调试）：

- **删除 `MockData.kt`** → 同时删除 `PreferencesManager.savePosts()/loadPosts()` 调用
- **删除 `MockUserRepository.kt`** → 确认所有 ViewModel 已切换到 Api 实现
- **删除 `MockPostRepository.kt`** → 确认所有 ViewModel 已切换到 Api 实现
- **保留 `PreferencesManager.kt`** → 缩减为仅存储 Token / 自动登录标志 / 记住密码
- **保留 `CampusRepository.kt`**（如果存在）→ 评估是否需要
