package com.lorbke.ft_hangouts.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import com.lorbke.ft_hangouts.R
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

// need to copy the picture into the apps private storage to make sure it remains accessible
object PhotoStorage {

    fun copyToAppStorage(context: Context, sourceUri: Uri): String {
        val photosDir = File(context.filesDir, "photos")
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }
        val destFile = File(photosDir, UUID.randomUUID().toString() + ".jpg")

        val input = context.contentResolver.openInputStream(sourceUri)
        input.use {
            FileOutputStream(destFile).use { output ->
                it?.copyTo(output)
            }
        }
        return destFile.absolutePath
    }

    private fun resize(path: String, reqSize: Int): Bitmap? {
        val bounds = BitmapFactory.Options()
        bounds.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, bounds)

        var sampleSize = 1
        while (bounds.outWidth / sampleSize > reqSize || bounds.outHeight / sampleSize > reqSize) {
            sampleSize *= 2
        }

        val options = BitmapFactory.Options()
        options.inSampleSize = sampleSize
        return BitmapFactory.decodeFile(path, options)
    }

    fun showPhoto(imageView: ImageView, photoPath: String?, reqSize: Int) {
        val bitmap = if (photoPath != null) resize(photoPath, reqSize) else null
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageResource(R.drawable.ic_default_avatar)
        }
    }
}
