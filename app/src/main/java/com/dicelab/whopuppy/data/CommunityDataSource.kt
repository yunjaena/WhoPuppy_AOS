package com.dicelab.whopuppy.data

import com.dicelab.whopuppy.data.entity.ArticleItem
import com.dicelab.whopuppy.data.entity.Articles
import com.dicelab.whopuppy.data.entity.Boards
import com.dicelab.whopuppy.data.entity.CommentItem
import com.dicelab.whopuppy.data.entity.Comments
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.File

interface CommunityDataSource {
    fun getBoards(): Single<Boards>

    fun getArticles(
        boardId: Long,
        limit: Int,
        page: Int,
        region: String?,
        title: String?
    ): Single<Articles>

    fun getArticle(articleId: Long): Single<ArticleItem>

    fun writeArticle(articleItem: ArticleItem): Single<Long>

    fun updateArticle(articleItem: ArticleItem): Completable

    fun deleteArticle(id: Long): Completable

    fun getComments(id: Long): Single<Comments>

    fun writeComment(id: Long, commentItem: CommentItem): Completable

    fun deleteComment(id: Long, commentId: Long): Completable

    fun uploadImage(imageFile: File): Single<String>
}
