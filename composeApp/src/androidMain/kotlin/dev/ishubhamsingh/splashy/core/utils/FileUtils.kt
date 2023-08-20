/*
 * Copyright 2023 Shubham Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.ishubhamsingh.splashy.core.utils

import android.app.WallpaperManager
import android.app.WallpaperManager.FLAG_LOCK
import android.app.WallpaperManager.FLAG_SYSTEM
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import dev.ishubhamsingh.splashy.features.details.WallpaperScreenType
import java.io.File
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Created by Shubham Singh on 19/08/23. */
actual class FileUtils(private val context: Context) {

  actual suspend fun saveByteArrayToFile(fileName: String, data: ByteArray) {
    var uri: Uri? = null
    var newFileName = ""
    var result = ""

    CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
      runCatching {
          val imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

          val pictureFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
          val splashyFolder = File(pictureFolder, "Splashy")
          if (!splashyFolder.exists()) {
            splashyFolder.mkdir()
          }

          val imageFile = File(splashyFolder, "$fileName.jpg")
          newFileName = imageFile.name

          val mContentValues =
            ContentValues().apply {
              put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
              put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
              put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
              put(MediaStore.Images.Media.DISPLAY_NAME, newFileName)
            }

          context.contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mContentValues)
            .apply {
              uri = this
              uri?.let {
                context.contentResolver.openOutputStream(it)?.let { outStream ->
                  imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                }
              }
            }
        }
        .onSuccess {
          result = "File saved successfully in ${Environment.DIRECTORY_PICTURES}"
          showMessage(result)
        }
        .onFailure {
          result = "Saving file failed!"
          showMessage(result)
          it.printStackTrace()
        }
    }
  }

  actual suspend fun applyWallpaper(data: ByteArray, wallpaperScreenType: WallpaperScreenType) {
    val wallpaperManager = WallpaperManager.getInstance(context)
    var result = ""
    val flag =
      when (wallpaperScreenType) {
        WallpaperScreenType.HOME_SCREEN -> FLAG_SYSTEM
        WallpaperScreenType.LOCK_SCREEN -> FLAG_LOCK
        WallpaperScreenType.BOTH -> FLAG_SYSTEM or FLAG_LOCK
        else -> FLAG_SYSTEM
      }

    CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
      runCatching {
          val imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
          wallpaperManager.setBitmap(imageBitmap, null, true, flag)
        }
        .onSuccess {
          result = "Applying wallpaper succeeded"
          showMessage(result)
        }
        .onFailure {
          result = "Applying wallpaper failed!"
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
