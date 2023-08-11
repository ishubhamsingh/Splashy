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
package dev.ishubhamsingh.splashy.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Urls(
  @SerialName("full") val full: String = "",
  @SerialName("raw") val raw: String = "",
  @SerialName("regular") val regular: String = "",
  @SerialName("small") val small: String = "",
  @SerialName("small_s3") val smallS3: String = "",
  @SerialName("thumb") val thumb: String = ""
) {
  override fun toString(): String {
    return Json.encodeToString(this)
  }
}
