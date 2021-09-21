package com.dicelab.whopuppy.viewmodel

import com.dicelab.whopuppy.data.Area

data class ArticleSearchQuery(
    var boardId: Long? = null,
    var title: String? = null,
    var area: String? = null,
    var page: Int = 1,
    var limit: Int = 10,
) {
    fun initPage() {
        page = 1
    }

    fun isSameQuery(articleSearchQuery: ArticleSearchQuery?): Boolean {
        if (articleSearchQuery == null) return false
        return this.boardId == articleSearchQuery.boardId && this.title == articleSearchQuery.title && this.area == articleSearchQuery.area
    }

    fun setArticleBoardId(boardId: Long?) {
        initPage()
        this.boardId = boardId
    }

    fun setArea(area: Area) {
        initPage()
        if (area == Area.ALL){
            this.area = null
            return
        }
        this.area = area.areaName
    }

    fun setArticleTitle(title: String?) {
        initPage()
        this.title = title
    }

    fun setLimitPage(limitPage: Int) {
        initPage()
        this.limit = limitPage
    }

    @Synchronized
    fun nextPage() {
        page++
    }
}
