package com.yunjaena.whopuppy.data

import com.yunjaena.whopuppy.data.entity.AbandonedDog
import com.yunjaena.whopuppy.data.entity.AbandonedDogItem
import io.reactivex.rxjava3.core.Single

class AnimalRepository(private val animalRemoteDataSource: AnimalDataSource) : AnimalDataSource {
    override fun getAnimalList(
        kindCd: String?,
        limit: Int?,
        noticeNo: String?,
        page: Int?,
        sexCd: String?
    ): Single<AbandonedDog> {
        return animalRemoteDataSource.getAnimalList(kindCd, limit, noticeNo, page, sexCd)
    }

    override fun getAnimalDetail(index: Long): Single<AbandonedDogItem> {
        return animalRemoteDataSource.getAnimalDetail(index)
    }
}
