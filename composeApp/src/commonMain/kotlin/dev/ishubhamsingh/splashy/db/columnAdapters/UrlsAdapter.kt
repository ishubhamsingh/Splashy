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
package dev.ishubhamsingh.splashy.db.columnAdapters

import app.cash.sqldelight.ColumnAdapter
import dev.ishubhamsingh.splashy.models.Urls
import kotlinx.serialization.json.Json

/** Created by Shubham Singh on 11/08/23. */
object UrlsAdapter : ColumnAdapter<Urls, String> {
  override fun decode(databaseValue: String): Urls {
    return Json.decodeFromString(databaseValue)
  }

  override fun encode(value: Urls): String {
    return value.toString()
  }
}
