package com.yunjaena.whopuppy.data.remote

import com.yunjaena.whopuppy.api.AuthApi
import com.yunjaena.whopuppy.data.CommunityDataSource
import com.yunjaena.whopuppy.util.FormDataUtil
import io.reactivex.rxjava3.core.Single
import java.io.File

class CommunityRemoteDataSource(
    private val api: AuthApi
) : CommunityDataSource {
    override fun uploadImage(imageFile: File): Single<String> {
        return api.uploadCommunityImage(FormDataUtil.getImageBody("multipartFile", imageFile))
    }
}
