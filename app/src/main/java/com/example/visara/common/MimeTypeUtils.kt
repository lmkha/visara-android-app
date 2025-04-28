package com.example.visara.common

import android.webkit.MimeTypeMap
import java.io.File

fun File.getMimeTypeOrNull(): String? {
    val extension = MimeTypeMap.getFileExtensionFromUrl(toURI().toString())
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
}
