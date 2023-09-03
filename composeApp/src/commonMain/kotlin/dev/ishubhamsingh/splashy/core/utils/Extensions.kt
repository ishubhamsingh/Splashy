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

import dev.ishubhamsingh.splashy.models.CollectionItem

inline fun <reified T : Enum<T>> Int.toEnum(): T? {
  return enumValues<T>().firstOrNull { it.ordinal == this }
}

fun ArrayList<CollectionItem>.getNonPremiumCollections(): ArrayList<CollectionItem> {
  val filteredList =
    this.filter { it.coverPhoto?.urls?.regular?.contains("plus.unsplash") == false }
  return ArrayList(filteredList)
}
