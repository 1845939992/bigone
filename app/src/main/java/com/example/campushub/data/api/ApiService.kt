package com.example.campushub.data.api

import com.example.campushub.data.model.Comment
import com.example.campushub.data.model.Post
import com.example.campushub.data.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

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
    suspend fun updatePost(
        @Path("postId") postId: String,
        @Body request: UpdatePostRequest
    ): ApiResponse<Post>

    @POST("posts/{postId}/comments")
    suspend fun addComment(
        @Path("postId") postId: String,
        @Body request: AddCommentRequest
    ): ApiResponse<Comment>

    @POST("comments/{commentId}/reply")
    suspend fun addReply(
        @Path("commentId") commentId: String,
        @Body request: AddCommentRequest
    ): ApiResponse<Comment>

    @DELETE("posts/{postId}")
    suspend fun deletePost(@Path("postId") postId: String): ApiResponse<Unit>

    @POST("posts/{postId}/like")
    suspend fun toggleLike(@Path("postId") postId: String): ApiResponse<Unit>

    @POST("posts/{postId}/favorite")
    suspend fun toggleFavorite(@Path("postId") postId: String): ApiResponse<Unit>
}
