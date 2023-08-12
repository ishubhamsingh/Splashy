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
package dev.ishubhamsingh.splashy.db

import dev.ishubhamsingh.splashy.core.utils.DatabaseDriverFactory
import dev.ishubhamsingh.splashy.db.columnAdapters.IntAdapter
import dev.ishubhamsingh.splashy.db.columnAdapters.LinksAdapter
import dev.ishubhamsingh.splashy.db.columnAdapters.TopicSubmissionsAdapter
import dev.ishubhamsingh.splashy.db.columnAdapters.UrlsAdapter
import dev.ishubhamsingh.splashy.db.columnAdapters.UserAdapter

/** Created by Shubham Singh on 11/08/23. */
fun createDatabase(databaseDriverFactory: DatabaseDriverFactory): SplashyDatabase {
  val driver = databaseDriverFactory.createDriver()

  return SplashyDatabase(
    driver = driver,
    photoEntityAdapter =
      PhotoEntity.Adapter(
        linksAdapter = LinksAdapter,
        topicSubmissionsAdapter = TopicSubmissionsAdapter,
        urlsAdapter = UrlsAdapter,
        userAdapter = UserAdapter,
        heightAdapter = IntAdapter,
        widthAdapter = IntAdapter,
        likesAdapter = IntAdapter
      ),
    favouriteEntityAdapter =
      FavouriteEntity.Adapter(topicSubmissionsAdapter = TopicSubmissionsAdapter)
  )
}
