package com.theblindmole.gamevault.data.storage

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID
import kotlin.math.max

//gestao das capas guardadas no armazenamento interno
class CoverStorage(context: Context) {

    private val contentResolver: ContentResolver = context.contentResolver
    private val directory = File(context.filesDir, DIRECTORY)

    suspend fun import(sourceUri: String): String? = withContext(Dispatchers.IO) {
        try {
            val bitmap = decodeScaled(Uri.parse(sourceUri)) ?: return@withContext null
            directory.mkdirs()
            val target = File(directory, "$FILE_PREFIX${UUID.randomUUID()}$FILE_EXTENSION")
            target.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, it) }
            bitmap.recycle()
            Uri.fromFile(target).toString()
        } catch (e: IOException) {
            null
        } catch (e: SecurityException) {
            null
        }
    }

    suspend fun delete(coverUrl: String?) = withContext(Dispatchers.IO) {
        coverUrl?.toLocalFile()?.delete()
        Unit
    }

    suspend fun deleteAllExcept(keep: Collection<String>) = withContext(Dispatchers.IO) {
        val keepFiles = keep.mapNotNull { it.toLocalFile() }.toSet()
        directory.listFiles()?.filterNot { it in keepFiles }?.forEach { it.delete() }
        Unit
    }

    //reduz a imagem para poupar espaco
    private fun decodeScaled(uri: Uri): Bitmap? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                val largest = max(info.size.width, info.size.height)
                if (largest > MAX_SIZE) {
                    val scale = MAX_SIZE.toFloat() / largest
                    decoder.setTargetSize(
                        (info.size.width * scale).toInt(),
                        (info.size.height * scale).toInt(),
                    )
                }
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            decodeSampled(uri)
        }

    private fun decodeSampled(uri: Uri): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sampleSize = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sampleSize * 2) >= MAX_SIZE) {
            sampleSize *= 2
        }
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    }

    private fun String.toLocalFile(): File? {
        val uri = Uri.parse(this)
        if (uri.scheme != ContentResolver.SCHEME_FILE) return null
        val file = File(uri.path ?: return null)
        return file.takeIf { it.parentFile == directory }
    }

    private companion object {
        const val DIRECTORY = "covers"
        const val FILE_PREFIX = "cover_"
        const val FILE_EXTENSION = ".jpg"
        const val MAX_SIZE = 1280
        const val JPEG_QUALITY = 85
    }
}
