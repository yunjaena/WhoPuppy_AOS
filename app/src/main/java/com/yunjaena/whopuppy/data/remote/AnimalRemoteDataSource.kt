package com.yunjaena.whopuppy.data.remote

import com.yunjaena.whopuppy.api.NoAuthApi
import com.yunjaena.whopuppy.data.AnimalDataSource
import com.yunjaena.whopuppy.data.entity.AbandonedDog
import com.yunjaena.whopuppy.data.entity.AbandonedDogItem
import io.reactivex.rxjava3.core.Single

class AnimalRemoteDataSource(private val noAuthApi: NoAuthApi) : AnimalDataSource {
    override fun getAnimalList(
        kindCd: String?,
        limit: Int?,
        noticeNo: String?,
        page: Int?,
        sexCd: String?
    ): Single<AbandonedDog> {
        return noAuthApi.getAnimal(kindCd, limit, noticeNo, page, sexCd)
    }

    override fun getAnimalDetail(index: Long): Single<AbandonedDogItem> {
        return noAuthApi.getAnimalDetail(index)
    }
}
