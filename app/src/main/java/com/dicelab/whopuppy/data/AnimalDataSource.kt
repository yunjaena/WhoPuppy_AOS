package com.dicelab.whopuppy.data

import com.dicelab.whopuppy.data.entity.AbandonedAnimal
import com.dicelab.whopuppy.data.entity.AbandonedAnimalItem
import com.dicelab.whopuppy.data.entity.DogBreedAnalysis
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
