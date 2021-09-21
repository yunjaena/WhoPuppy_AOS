package com.dicelab.whopuppy.data

import com.dicelab.whopuppy.data.entity.AbandonedAnimal
import com.dicelab.whopuppy.data.entity.AbandonedAnimalItem
import com.dicelab.whopuppy.data.entity.DogBreedAnalysis
import io.reactivex.rxjava3.core.Single

class AnimalRepository(private val animalRemoteDataSource: AnimalDataSource) : AnimalDataSource {
    override fun getAnimalList(
        kindCd: String?,
        limit: Int?,
        noticeNo: String?,
        page: Int?,
        sexCd: String?
    ): Single<AbandonedAnimal> {
        return animalRemoteDataSource.getAnimalList(kindCd, limit, noticeNo, page, sexCd)
    }

    override fun getAnimalDetail(index: Long): Single<AbandonedAnimalItem> {
        return animalRemoteDataSource.getAnimalDetail(index)
    }

    override fun getDogBreedInfo(imageUrl: String): Single<List<DogBreedAnalysis>> {
        return animalRemoteDataSource.getDogBreedInfo(imageUrl)
    }
}
