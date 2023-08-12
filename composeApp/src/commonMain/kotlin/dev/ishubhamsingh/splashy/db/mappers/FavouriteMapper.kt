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
package dev.ishubhamsingh.splashy.db.mappers

import dev.ishubhamsingh.splashy.db.FavouriteEntity
import dev.ishubhamsingh.splashy.models.Favourite
import dev.ishubhamsingh.splashy.models.Photo

/** Created by Shubham Singh on 12/08/23. */
fun Photo.toFavourite(): Favourite =
  Favourite(
    id = id,
    color = color,
    altDescription = altDescription,
    description = description,
    topicSubmissions = topicSubmissions,
    url = urls?.regular
  )

fun Favourite.toFavouriteEntity(): FavouriteEntity =
  FavouriteEntity(
    id = id,
    color = color,
    altDescription = altDescription,
    description = description,
    topicSubmissions = topicSubmissions,
    url = url
  )

fun FavouriteEntity.toFavourite(): Favourite =
  Favourite(
    id = id,
    color = color,
    altDescription = altDescription,
    description = description,
    topicSubmissions = topicSubmissions,
    url = url
  )

fun List<FavouriteEntity>.toFavouriteList(): List<Favourite> = map { it.toFavourite() }

fun List<FavouriteEntity>.toFavouriteArrayList(): ArrayList<Favourite> =
  ArrayList(toFavouriteList())
