package com.yunjaena.whopuppy.data

import io.reactivex.rxjava3.core.Single
import java.io.File

class CommunityRepository(
    private val communityRemoteDataSource: CommunityDataSource
) : CommunityDataSource {
    override fun uploadImage(imageFile: File): Single<String> {
        return communityRemoteDataSource.uploadImage(imageFile)
    }
}
