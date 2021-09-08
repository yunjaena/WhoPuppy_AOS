package com.yunjaena.whopuppy.util

import android.webkit.MimeTypeMap
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


object FormDataUtil {
    fun getTextBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), value)
    }

    fun getImageBody(name: String, file: File): MultipartBody.Part {
        val type = getMimeType(file.path) ?: "*"
        val requestBody = RequestBody.create(MediaType.parse(type), file)
        return MultipartBody.Part.createFormData(name, file.name, requestBody)
    }

    fun getVideoBody(name: String, file: File): MultipartBody.Part {
        val type = getMimeType(file.path) ?: "*"
        val requestBody = RequestBody.create(MediaType.parse(type), file)
        return MultipartBody.Part.createFormData(name, file.name, requestBody)
    }

    private fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
}
