package com.dicelab.whopuppy.api

import com.dicelab.whopuppy.data.entity.ArticleItem
import com.dicelab.whopuppy.data.entity.Articles
import com.dicelab.whopuppy.data.entity.Boards
import com.dicelab.whopuppy.data.entity.ChatMessage
import com.dicelab.whopuppy.data.entity.ChatRoom
import com.dicelab.whopuppy.data.entity.ChatRoomCreateItem
import com.dicelab.whopuppy.data.entity.ChatRoomCreateResponse
import com.dicelab.whopuppy.data.entity.ChatRoomItem
import com.dicelab.whopuppy.data.entity.CommentItem
import com.dicelab.whopuppy.data.entity.Comments
import com.dicelab.whopuppy.data.entity.User
import com.dicelab.whopuppy.data.response.CommonResponse
import com.dicelab.whopuppy.data.response.TokenResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApi {
    @POST("/user/refresh")
    fun refreshToken(): Single<TokenResponse>

    @GET("/user/me")
    fun getUserInfo(): Single<User>

    @GET("/user/me")
    suspend fun getUserInfoCoroutine(): User

    @PUT("/user/nickname")
    fun updateNickName(@Body user: User): Single<CommonResponse>

    @Multipart
    @POST("/community/article/image")
    fun uploadCommunityImage(@Part multipartFile: MultipartBody.Part): Single<String>

    @GET(".")
    fun getDogBreedInfo(@Query("url") imageUrl: String): Single<ResponseBody>

    @GET("/community/article")
    fun getArticles(
        @Query("boardId") boardId: Long,
        @Query("limit") limit: Int?,
        @Query("page") page: Int?,
        @Query("region") region: String?,
        @Query("title") title: String?
    ): Single<Articles>

    @POST("/community/article")
    fun postArticle(@Body articleItem: ArticleItem): Single<Long>

    @GET("/community/article/{articleId}")
    fun getArticle(@Path("articleId") articleId: Long): Single<ArticleItem>

    @PUT("/community/article/{id}")
    fun putArticle(@Path("id") id: Long, @Body articleItem: ArticleItem): Completable

    @DELETE("/community/article/{id}")
    fun deleteArticle(@Path("id") id: Long): Completable

    @GET("/community/article/{id}/comment")
    fun getComments(@Path("id") id: Long): Single<Comments>

    @POST("/community/article/{id}/comment")
    fun postComment(@Path("id") id: Long, @Body commentItem: CommentItem): Completable

    @DELETE("/community/article/{id}/comment/{commentId}")
    fun deleteComment(@Path("id") id: Long, @Path("commentId") commentId: Long): Completable

    @GET("/community/article/user/{boardId}")
    fun getMyArticle(@Path("boardId") boardId: Long): Single<Articles>

    @GET("/community/board")
    fun getBoards(): Single<Boards>

    @GET("/community/board/{id}")
    fun getBoard(@Path("id") id: Long): Single<Boards>

    @Multipart
    @POST("/user/profile")
    fun updateProfile(@Part multipartFile: MultipartBody.Part): Completable

    @GET("/chat/rooms")
    suspend fun getChatRooms(): ChatRoom

    @GET("/chat/room/{roomId}")
    suspend fun getChatRoomDetail(@Path("roomId") roomId: Long): ChatRoomItem

    @GET("/chat/room/{roomId}/message")
    suspend fun getRecentChatMessage(
        @Path("roomId") roomId: Long,
        @Query("count") count: Int,
        @Query("id") id: Long,
    ): ArrayList<ChatMessage>

    @POST("/chat/room")
    suspend fun postChatRoom(@Body chatRoomCreateItem: ChatRoomCreateItem): ChatRoomCreateResponse
}
