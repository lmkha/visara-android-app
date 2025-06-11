package com.example.visara.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun createVideoThumbFromLocalUri(context: Context, uri: Uri): Bitmap? {
    return withContext(Dispatchers.IO) {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        try {
            mediaMetadataRetriever.setDataSource(context, uri)
            mediaMetadataRetriever.frameAtTime
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            mediaMetadataRetriever.release()
        }
    }
}

suspend fun createBitmapFromLocalUri(context: Context, imageUri: Uri): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
