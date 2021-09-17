package com.yunjaena.whopuppy.data.remote

import com.yunjaena.whopuppy.api.AuthApi
import com.yunjaena.whopuppy.data.CommunityDataSource
import com.yunjaena.whopuppy.data.entity.ArticleItem
import com.yunjaena.whopuppy.data.entity.Articles
import com.yunjaena.whopuppy.data.entity.Boards
import com.yunjaena.whopuppy.data.entity.CommentItem
import com.yunjaena.whopuppy.data.entity.Comments
import com.yunjaena.whopuppy.util.FormDataUtil
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.File

class CommunityRemoteDataSource(
    private val api: AuthApi
) : CommunityDataSource {
    override fun getBoards(): Single<Boards> {
        return api.getBoards()
    }

    override fun getArticles(
        boardId: Long,
        limit: Int,
        page: Int,
        region: String?,
        title: String?
    ): Single<Articles> {
        return api.getArticles(boardId, limit, page, region, title)
    }

    override fun getArticle(articleId: Long): Single<ArticleItem> {
        return api.getArticle(articleId)
    }

    override fun writeArticle(articleItem: ArticleItem): Single<Int> {
        return api.postArticle(articleItem)
    }

    override fun updateArticle(articleItem: ArticleItem): Completable {
        if (articleItem.id == null)
            return Completable.error(IllegalArgumentException("article id is null"))
        return api.putArticle(articleItem.id, articleItem)
    }

    override fun deleteArticle(id: Long): Completable {
        return api.deleteArticle(id)
    }

    override fun getComments(id: Long): Single<Comments> {
        return api.getComments(id)
    }

    override fun writeComment(id: Long, commentItem: CommentItem): Completable {
        return api.postComment(id, commentItem)
    }

    override fun deleteComment(id: Long, commentId: Long): Completable {
        return api.deleteComment(id, commentId)
    }

    override fun uploadImage(imageFile: File): Single<String> {
        return api.uploadCommunityImage(FormDataUtil.getImageBody("multipartFile", imageFile))
    }
}
