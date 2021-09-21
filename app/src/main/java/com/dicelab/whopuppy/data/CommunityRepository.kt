package com.dicelab.whopuppy.data

import com.dicelab.whopuppy.data.entity.ArticleItem
import com.dicelab.whopuppy.data.entity.Articles
import com.dicelab.whopuppy.data.entity.Boards
import com.dicelab.whopuppy.data.entity.CommentItem
import com.dicelab.whopuppy.data.entity.Comments
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.File

class CommunityRepository(
    private val communityRemoteDataSource: CommunityDataSource
) : CommunityDataSource {
    override fun getBoards(): Single<Boards> {
        return communityRemoteDataSource.getBoards()
    }

    override fun getArticles(
        boardId: Long,
        limit: Int,
        page: Int,
        region: String?,
        title: String?
    ): Single<Articles> {
        return communityRemoteDataSource.getArticles(boardId, limit, page, region, title)
    }

    override fun getArticle(articleId: Long): Single<ArticleItem> {
        return communityRemoteDataSource.getArticle(articleId)
    }

    override fun writeArticle(articleItem: ArticleItem): Single<Long> {
        return communityRemoteDataSource.writeArticle(articleItem)
    }

    override fun updateArticle(articleItem: ArticleItem): Completable {
        return communityRemoteDataSource.updateArticle(articleItem)
    }

    override fun deleteArticle(id: Long): Completable {
        return communityRemoteDataSource.deleteArticle(id)
    }

    override fun getComments(id: Long): Single<Comments> {
        return communityRemoteDataSource.getComments(id)
    }

    override fun writeComment(id: Long, commentItem: CommentItem): Completable {
        return communityRemoteDataSource.writeComment(id, commentItem)
    }

    override fun deleteComment(id: Long, commentId: Long): Completable {
        return communityRemoteDataSource.deleteComment(id, commentId)
    }

    override fun uploadImage(imageFile: File): Single<String> {
        return communityRemoteDataSource.uploadImage(imageFile)
    }
}
