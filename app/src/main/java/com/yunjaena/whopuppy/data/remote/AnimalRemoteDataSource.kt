package com.yunjaena.whopuppy.data.remote

import com.yunjaena.whopuppy.api.NoAuthApi
import com.yunjaena.whopuppy.data.AnimalDataSource
import com.yunjaena.whopuppy.data.entity.AbandonedAnimal
import com.yunjaena.whopuppy.data.entity.AbandonedAnimalItem
import io.reactivex.rxjava3.core.Single

class AnimalRemoteDataSource(private val noAuthApi: NoAuthApi) : AnimalDataSource {
    override fun getAnimalList(
        kindCd: String?,
        limit: Int?,
        noticeNo: String?,
        page: Int?,
        sexCd: String?
    ): Single<AbandonedAnimal> {
        return noAuthApi.getAnimal(kindCd, limit, noticeNo, page, sexCd)
    }

    override fun getAnimalDetail(index: Long): Single<AbandonedAnimalItem> {
        return noAuthApi.getAnimalDetail(index)
    }
}
