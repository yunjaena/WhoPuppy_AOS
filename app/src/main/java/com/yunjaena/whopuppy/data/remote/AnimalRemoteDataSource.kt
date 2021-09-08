package com.yunjaena.whopuppy.data.remote

import com.orhanobut.logger.Logger
import com.yunjaena.whopuppy.api.AuthApi
import com.yunjaena.whopuppy.api.NoAuthApi
import com.yunjaena.whopuppy.data.AnimalDataSource
import com.yunjaena.whopuppy.data.entity.AbandonedAnimal
import com.yunjaena.whopuppy.data.entity.AbandonedAnimalItem
import com.yunjaena.whopuppy.data.entity.DogBreedAnalysis
import io.reactivex.rxjava3.core.Single

class AnimalRemoteDataSource(private val noAuthApi: NoAuthApi, private val authFlaskApi: AuthApi) :
    AnimalDataSource {
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

    override fun getDogBreedInfo(imageUrl: String): Single<List<DogBreedAnalysis>> {
        return authFlaskApi.getDogBreedInfo(imageUrl)
            .map { it.string().toDogBreedAnalysisList() }
    }

    private fun String.toDogBreedAnalysisList(): List<DogBreedAnalysis> {
        val dogBreedAnalysisList = arrayListOf<DogBreedAnalysis>()
        this.replace("{", "")
            .replace("}", "")
            .split(",")
            .forEach {
                val breedItem = it.split(":")
                val breedAnalysisItem =
                    DogBreedAnalysis(breedItem[0].replace("\"", ""), breedItem[1].toFloat())
                dogBreedAnalysisList.add(breedAnalysisItem)
                Logger.d("NYJ $breedAnalysisItem")
            }
        return dogBreedAnalysisList
    }
}
