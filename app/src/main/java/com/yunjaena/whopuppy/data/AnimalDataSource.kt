package com.yunjaena.whopuppy.data

import com.yunjaena.whopuppy.data.entity.AbandonedAnimal
import com.yunjaena.whopuppy.data.entity.AbandonedAnimalItem
import com.yunjaena.whopuppy.data.entity.DogBreedAnalysis
import io.reactivex.rxjava3.core.Single

interface AnimalDataSource {
    fun getAnimalList(
        kindCd: String?,
        limit: Int?,
        noticeNo: String?,
        page: Int?,
        sexCd: String?
    ): Single<AbandonedAnimal>

    fun getAnimalDetail(index: Long): Single<AbandonedAnimalItem>

    fun getDogBreedInfo(imageUrl: String): Single<List<DogBreedAnalysis>>
}
