package com.dicelab.whopuppy.viewmodel

data class AnimalSearchQuery(
    var kindCd: String? = null,
    var limit: Int = 10,
    var noticeNo: String? = null,
    var page: Int = 1,
    var sexCd: Sex = Sex.ALL
) {
    fun initPage() {
        page = 1
    }

    fun setKind(kind: String?) {
        initPage()
        kindCd = kind
    }

    fun setArea(area: String?) {
        initPage()
        noticeNo = area
    }

    fun setSex(sex: Sex) {
        initPage()
        sexCd = sex
    }

    @Synchronized
    fun nextPage() {
        page++
    }
}

enum class Sex(
    val type: String?
) {
    ALL(null), MALE("M"), FEMALE("F"), NEUTRAL("Q");
}
