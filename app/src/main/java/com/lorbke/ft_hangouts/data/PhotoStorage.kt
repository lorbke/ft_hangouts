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

// Copies a picture the user picked into the app's own private storage, and
// loads it back for display. We never keep the picker's original content://
// URI around - those can stop working later (permission revoked, the app
// that owned the file uninstalled), so we make our own permanent copy.
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

    // Loads a bitmap downsized to roughly reqSize x reqSize pixels, so a
    // handful of full-resolution photos don't blow up the app's memory.
    private fun loadThumbnail(path: String, reqSize: Int): Bitmap? {
        // First pass: read only the image's dimensions, not its pixels.
        val bounds = BitmapFactory.Options()
        bounds.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, bounds)

        var sampleSize = 1
        while (bounds.outWidth / sampleSize > reqSize || bounds.outHeight / sampleSize > reqSize) {
            sampleSize *= 2
        }

        // Second pass: actually decode, at 1/sampleSize resolution.
        val options = BitmapFactory.Options()
        options.inSampleSize = sampleSize
        return BitmapFactory.decodeFile(path, options)
    }

    // Shows a contact's photo in an ImageView, falling back to the default
    // avatar if there is none (or the file is somehow gone).
    fun showInto(imageView: ImageView, photoPath: String?, reqSize: Int) {
        val bitmap = if (photoPath != null) loadThumbnail(photoPath, reqSize) else null
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageResource(R.drawable.ic_default_avatar)
        }
    }
}
