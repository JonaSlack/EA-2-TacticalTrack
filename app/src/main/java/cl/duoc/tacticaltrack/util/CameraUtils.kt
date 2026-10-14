package cl.duoc.tacticaltrack.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun createVisitImageUri(context: Context): Uri {
    val imageDir = File(context.filesDir, "visit_images").apply { mkdirs() }
    val file = File.createTempFile("visit_${System.currentTimeMillis()}_", ".jpg", imageDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

fun copyImageToInternalStorage(context: Context, source: Uri): Uri? {
    return runCatching {
        val imageDir = File(context.filesDir, "visit_images").apply { mkdirs() }
        val destination = File.createTempFile(
            "gallery_${System.currentTimeMillis()}_",
            ".jpg",
            imageDir
        )

        context.contentResolver.openInputStream(source)?.use { input ->
            FileOutputStream(destination).use { output ->
                input.copyTo(output)
            }
        } ?: return null

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            destination
        )
    }.getOrNull()
}
