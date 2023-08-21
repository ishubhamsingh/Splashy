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

import com.eygraber.uri.Uri
import dev.ishubhamsingh.splashy.features.details.WallpaperScreenType
import kotlin.coroutines.coroutineContext
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import platform.UIKit.UIImageWriteToSavedPhotosAlbum

/** Created by Shubham Singh on 19/08/23. */
actual class FileUtils {
  @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
  actual suspend fun saveByteArrayToFile(
    fileName: String,
    data: ByteArray,
    shouldOpenFile: Boolean,
    updateMessage: (String) -> Unit
  ) {
    var result = ""
    CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
      runCatching {
          val nsData: NSData = memScoped {
            NSData.create(bytes = allocArrayOf(data), length = data.size.toULong())
          }
          val imageData = UIImage.imageWithData(data = nsData)
          if (imageData != null) {
            UIImageWriteToSavedPhotosAlbum(image = imageData, null, null, null)
          } else {
            throw NullPointerException()
          }
        }
        .onSuccess {
          result = "Saved to photos successfully"
          updateMessage.invoke(result)
        }
        .onFailure {
          result = "Saving to photos failed!"
          updateMessage.invoke(result)
          it.printStackTrace()
        }
    }
  }

  actual suspend fun openFile(uri: Uri?, fileName: String) {
    // Do nothing
  }

  actual suspend fun applyWallpaper(
    data: ByteArray,
    wallpaperScreenType: WallpaperScreenType,
    updateMessage: (String) -> Unit
  ) {
    // Do nothing
  }

  actual suspend fun shouldAskStorageRuntimePermission(): Boolean {
    return true
  }

  actual suspend fun showMessage(message: String) {
    // Do nothing
  }
}
