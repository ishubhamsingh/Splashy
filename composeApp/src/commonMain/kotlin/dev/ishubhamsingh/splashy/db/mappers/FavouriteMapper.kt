package dev.ishubhamsingh.splashy.db.mappers

import dev.ishubhamsingh.splashy.db.FavouriteEntity
import dev.ishubhamsingh.splashy.db.PhotoEntity
import dev.ishubhamsingh.splashy.models.Favourite
import dev.ishubhamsingh.splashy.models.Photo

/**
 * Created by Shubham Singh on 12/08/23.
 */

fun Photo.toFavourite(): Favourite = Favourite(
    id = id,
    color = color,
    altDescription = altDescription,
    description = description,
    topicSubmissions = topicSubmissions,
    url = urls?.regular
)

fun Favourite.toFavouriteEntity(): FavouriteEntity = FavouriteEntity(
    id = id,
    color = color,
    altDescription = altDescription,
    description = description,
    topicSubmissions = topicSubmissions,
    url = url
)

fun FavouriteEntity.toFavourite(): Favourite = Favourite(
    id = id,
    color = color,
    altDescription = altDescription,
    description = description,
    topicSubmissions = topicSubmissions,
    url = url
)

fun List<FavouriteEntity>.toFavouriteList(): List<Favourite> = map { it.toFavourite()}
fun List<FavouriteEntity>.toFavouriteArrayList(): ArrayList<Favourite> = ArrayList(toFavouriteList())