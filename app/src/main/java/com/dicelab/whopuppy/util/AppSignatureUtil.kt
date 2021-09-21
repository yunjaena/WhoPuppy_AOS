package com.dicelab.whopuppy.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.orhanobut.logger.Logger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays

fun Context.getAppSignatures(): ArrayList<String> {
    val appCodes: ArrayList<String> = ArrayList()

    try {
        val packageName = packageName
        val packageManager = packageManager
        val signatures = with(packageManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                    .signingInfo
                    .apkContentsSigners
            } else {
                getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                    .signatures
            }
        }

        for (signature in signatures) {
            val hash = hash(packageName, signature.toCharsString())
            if (hash != null) {
                appCodes.add(String.format("%s", hash))
            }
        }
    } catch (e: PackageManager.NameNotFoundException) {
        Logger.e("Hash", "Unable to find package to obtain hash.", e)
    }

    return appCodes
}

private fun hash(packageName: String, signature: String): String? {
    val HASH_TYPE = "SHA-256"
    val NUM_HASHED_BYTES = 9
    val NUM_BASE64_CHAR = 11

    val appInfo = "$packageName $signature"
    try {
        val messageDigest = MessageDigest.getInstance(HASH_TYPE)
        messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
        var hashSignature = messageDigest.digest()

        // truncated into NUM_HASHED_BYTES
        hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
        // encode into Base64
        var base64Hash =
            android.util.Base64.encodeToString(hashSignature, android.util.Base64.NO_PADDING)
        base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)
        Logger.d("HASH", String.format("pkg: %s -- hash: %s", packageName, base64Hash))
        return base64Hash
    } catch (e: NoSuchAlgorithmException) {
        Logger.e("Hash", "hash:NoSuchAlgorithm", e)
    }
    return null
}
