package com.example.visara.utils

import android.webkit.MimeTypeMap
import java.io.File

fun File.getMimeTypeOrNull(): String? {
    val extension = MimeTypeMap.getFileExtensionFromUrl(toURI().toString())
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
}
