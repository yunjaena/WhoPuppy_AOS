package com.yunjaena.whopuppy.data

import com.yunjaena.whopuppy.data.entity.AbandonedDog
import com.yunjaena.whopuppy.data.entity.AbandonedDogItem
import io.reactivex.rxjava3.core.Single

interface AnimalDataSource {
    fun getAnimalList(
        kindCd: String?,
        limit: Int?,
        noticeNo: String?,
        page: Int?,
        sexCd: String?
    ): Single<AbandonedDog>

    fun getAnimalDetail(index: Long): Single<AbandonedDogItem>
}
