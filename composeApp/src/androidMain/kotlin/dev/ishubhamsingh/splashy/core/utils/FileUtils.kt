package dev.ishubhamsingh.splashy.core.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.coroutineContext

/**
 * Created by Shubham Singh on 19/08/23.
 */
actual class FileUtils(private val context: Context) {


    actual suspend fun saveByteArrayToFile(fileName: String, data: ByteArray) {
        var uri: Uri? = null
        var newFileName = ""
        var result = ""

        val imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

        CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
            runCatching {

                val pictureFolder = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                )
                val splashyFolder = File(pictureFolder, "Splashy")
                if(!splashyFolder.exists()) {
                    splashyFolder.mkdir()
                }

                val imageFile = File(splashyFolder, "$fileName.jpg")
                newFileName = imageFile.name

                val mContentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                    put(MediaStore.Images.Media.DISPLAY_NAME, newFileName)
                }

                context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    mContentValues
                ).apply {
                    uri = this
                    uri?.let {
                        context.contentResolver.openOutputStream(it)?.let { outStream ->
                            imageBitmap.compress(
                                Bitmap.CompressFormat.JPEG,
                                100,
                                outStream
                            )
                        }
                    }
                }

            }.onSuccess {
                result = "File saved successfully in ${Environment.DIRECTORY_PICTURES}"
                showMessage(result)
            }.onFailure {
                result = "Saving file failed!"
                showMessage(result)
                it.printStackTrace()
            }
        }
    }

    actual suspend fun showMessage(message: String) {
        CoroutineScope(coroutineContext).launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}