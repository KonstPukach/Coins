package com.oukachkosnt.coins.utils

import okhttp3.ResponseBody
import java.util.zip.ZipInputStream

fun ResponseBody.unzipResponse(): String {
    return ZipInputStream(byteStream())
        .also { it.nextEntry }
        .bufferedReader()
        .use { it.readText() }
}
