package com.yunjaena.whopuppy.data

import io.reactivex.rxjava3.core.Single
import java.io.File

interface CommunityDataSource {
    fun uploadImage(imageFile: File): Single<String>
}
