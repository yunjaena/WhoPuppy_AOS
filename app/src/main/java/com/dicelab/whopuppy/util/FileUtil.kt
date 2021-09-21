package com.dicelab.whopuppy.util

import android.net.Uri
import com.dicelab.whopuppy.WhoPuppyApplication
import java.io.File

fun Uri.toFile(): File {
    return when ("content" == scheme) {
        true -> getFileFromContentResolver(this)
        false -> File(path)
    }
}

private fun getFileFromContentResolver(uri: Uri): File {
    WhoPuppyApplication.instance.applicationContext.contentResolver.query(
        uri,
        null,
        null,
        null,
        null
    ).use {
        if (it == null) throw IllegalArgumentException("Cursor is null")
        it.moveToNext()
        val path = it.getString(it.getColumnIndex("_data"))
        return File(path)
    }
}
