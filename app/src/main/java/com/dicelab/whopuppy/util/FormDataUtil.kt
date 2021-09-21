package com.dicelab.whopuppy.util

import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URLEncoder


object FormDataUtil {
    fun getTextBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun getImageBody(name: String, file: File): MultipartBody.Part {
        val type = getMimeType(file.path) ?: "*"
        val requestBody = file.asRequestBody(type.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, file.name, requestBody)
    }

    fun getVideoBody(name: String, file: File): MultipartBody.Part {
        val type = getMimeType(file.path) ?: "*"
        val requestBody = file.asRequestBody(type.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, file.name, requestBody)
    }

    private fun getMimeType(url: String?): String? {
        var type: String? = null
        val urlEncoding = URLEncoder.encode(url, "UTF-8")
        val extension = MimeTypeMap.getFileExtensionFromUrl(urlEncoding)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
}
