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
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.eygraber.uri.toAndroidUri
import com.eygraber.uri.toUri
import dev.ishubhamsingh.splashy.core.presentation.CommonRes
import dev.ishubhamsingh.splashy.features.details.WallpaperScreenType
import java.io.File
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Created by Shubham Singh on 19/08/23. */
actual class FileUtils(private val context: Context) {

  actual suspend fun saveByteArrayToFile(
    fileName: String,
    data: ByteArray,
    shouldOpenFile: Boolean,
    updateMessage: (String) -> Unit
  ) {
    var uri: Uri? = null
    var result = ""

    CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
      runCatching {
          val imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

          val mContentValues =
            ContentValues().apply {
              put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
              put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
              put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
              put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
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
          if (shouldOpenFile) {
            openFile(uri?.toUri(), fileName)
          } else {
            result =
              CommonRes.downloading_file_success_message.replace(
                "%path",
                Environment.DIRECTORY_PICTURES
              )
            updateMessage.invoke(result)
          }
        }
        .onFailure {
          result =
            if (shouldOpenFile) {
              CommonRes.opening_file_failed_message
            } else {
              CommonRes.downloading_file_failed_message
            }
          updateMessage.invoke(result)
          it.printStackTrace()
        }
    }
  }

  actual suspend fun openFile(uri: com.eygraber.uri.Uri?, fileName: String) {
    val intent = Intent(Intent.ACTION_ATTACH_DATA)
    intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.setDataAndType(uri?.toAndroidUri(), "image/jpeg")
    intent.putExtra("mimeType", "image/jpeg")
    val chooserIntent =
      Intent.createChooser(intent, CommonRes.wallpaper_chooser_title.replace("%fileName", fileName))
    chooserIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooserIntent)
  }

  actual suspend fun applyWallpaper(
    data: ByteArray,
    wallpaperScreenType: WallpaperScreenType,
    updateMessage: (String) -> Unit
  ) {
    val wallpaperManager = WallpaperManager.getInstance(context)
    var result = ""
    val flag =
      when (wallpaperScreenType) {
        WallpaperScreenType.HOME_SCREEN -> FLAG_SYSTEM
        WallpaperScreenType.LOCK_SCREEN -> FLAG_LOCK
        WallpaperScreenType.BOTH -> FLAG_SYSTEM or FLAG_LOCK
        else -> FLAG_SYSTEM
      }

    CoroutineScope(coroutineContext).launch(Dispatchers.Default) {
      runCatching {
          val imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
          wallpaperManager.setBitmap(imageBitmap, null, true, flag)
        }
        .onSuccess {
          result = CommonRes.applying_wallpaper_success_message
          updateMessage.invoke(result)
        }
        .onFailure {
          result = CommonRes.applying_wallpaper_failed_message
          showMessage(result)
          updateMessage.invoke(result)
        }
    }
  }

  actual suspend fun shouldAskStorageRuntimePermission(): Boolean {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
  }

  actual suspend fun showMessage(message: String) {
    CoroutineScope(coroutineContext).launch(Dispatchers.Main) {
      Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
  }
}
