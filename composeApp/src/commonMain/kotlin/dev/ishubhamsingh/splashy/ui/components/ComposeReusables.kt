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
package dev.ishubhamsingh.splashy.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.ArrowIosBack
import compose.icons.evaicons.outline.ArrowheadUp
import compose.icons.evaicons.outline.CloseCircle
import compose.icons.evaicons.outline.Search
import dev.ishubhamsingh.splashy.features.categoriesPhotos.ui.CategoryType
import dev.ishubhamsingh.splashy.models.Favourite
import dev.ishubhamsingh.splashy.models.Photo
import dev.ishubhamsingh.splashy.resources.Res
import dev.ishubhamsingh.splashy.resources.lbl_search
import io.github.aakira.napier.Napier
import kotlin.random.Random
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PhotoGridLayout(
  isRefreshing: Boolean,
  onRefresh: () -> Unit,
  onSurfaceTouch: () -> Unit,
  searchQuery: String? = null,
  isSearching: Boolean = false,
  onSearchQueryChange: (String?) -> Unit = {},
  isPaginating: Boolean,
  photos: ArrayList<Photo> = arrayListOf(),
  favourites: ArrayList<Favourite> = arrayListOf(),
  onLoadMore: () -> Unit,
  onItemSelected: (Photo?, String?, String?, String?, String?) -> Unit,
  error: String?,
  shouldShowSearch: Boolean = false,
  modifier: Modifier = Modifier
) {
  val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)
  val lazyGridState = rememberLazyGridState()
  val threshold = 5
  val isFabVisible by
    remember(threshold) { derivedStateOf { lazyGridState.firstVisibleItemIndex > threshold } }

  Surface(
    color = MaterialTheme.colorScheme.surface,
    modifier = modifier.fillMaxSize().clickable { onSurfaceTouch.invoke() }
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      Box(Modifier.pullRefresh(pullRefreshState)) {
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(4.dp),
          state = lazyGridState
        ) {
          if (shouldShowSearch) {
            item(span = { GridItemSpan(this.maxLineSpan) }) {
              SearchBar(searchQuery, isSearching, onSearchQueryChange)
            }
          }

          if (favourites.isNotEmpty()) {
            items(items = favourites) {
              PhotoCardItem(
                onItemSelected = onItemSelected,
                id = it.id,
                color = it.color,
                url = it.url,
                altDescription = it.altDescription,
                modifier = Modifier.fillMaxWidth()
              )
            }
          } else if (photos.isNotEmpty()) {
            items(items = photos) {
              PhotoCardItem(
                onItemSelected = onItemSelected,
                id = it.id,
                color = it.color,
                url = it.urls?.regular,
                altDescription = it.altDescription,
                modifier = Modifier.fillMaxWidth(),
                photo = it
              )
            }
          } else if (error.isNullOrEmpty().not()) {
            // TODO: show error
          }

          if (isPaginating) {
            item(span = { GridItemSpan(this.maxLineSpan) }) {
              Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                CircularProgressIndicator(
                  modifier = Modifier.align(Alignment.CenterHorizontally).size(24.dp),
                  color = MaterialTheme.colorScheme.secondary
                )
              }
            }
          }
        }
        lazyGridState.OnBottomReached {
          // do on load more
          onLoadMore.invoke()
        }
        PullRefreshIndicator(
          isRefreshing,
          pullRefreshState,
          Modifier.align(Alignment.TopCenter),
          backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.secondary
        )
        ScrollToTopFab(
          isFabVisible,
          lazyGridState,
          modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp),
        )
      }
    }
  }
}

@Composable
fun PhotoCardItem(
  onItemSelected: (Photo?, String?, String?, String?, String?) -> Unit,
  id: String,
  color: String?,
  url: String?,
  altDescription: String?,
  photo: Photo? = null,
  heightDp: Dp = 320.dp,
  widthDp: Dp = 200.dp,
  padding: Dp = 4.dp,
  shouldShowOverlay: Boolean = false,
  overlayTitle: String = "",
  overlaySubtitle: String = "",
  modifier: Modifier = Modifier
) {

  Card(
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    colors =
      CardDefaults.cardColors(
        containerColor =
          if (color.isNullOrEmpty().not()) Color(parseColor(color!!))
          else MaterialTheme.colorScheme.surface
      ),
    modifier =
      modifier
        .padding(vertical = padding, horizontal = padding)
        .width(widthDp)
        .height(heightDp)
        .background(
          color =
            if (color.isNullOrEmpty().not()) Color(parseColor(color!!))
            else MaterialTheme.colorScheme.surface,
          shape = RoundedCornerShape(16.dp)
        )
        .clickable { onItemSelected.invoke(photo, id, color, url, altDescription) }
  ) {
    if (shouldShowOverlay) {
      val gradient =
        Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
        url?.let { ImageViewComponent(it, altDescription) }
        Box(modifier.matchParentSize().background(gradient), contentAlignment = Alignment.Center) {
          Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = overlayTitle,
              style =
                MaterialTheme.typography.titleMedium.copy(
                  fontSize = if (widthDp <= 180.dp) 18.sp else 22.sp
                ),
              color = Color.White,
              maxLines = 1,
              modifier = Modifier.padding(horizontal = 8.dp),
              overflow = TextOverflow.Visible,
              letterSpacing = if (widthDp <= 180.dp) 2.sp else 4.sp
            )

            Text(
              text = overlaySubtitle,
              style =
                MaterialTheme.typography.bodySmall.copy(
                  fontSize = if (widthDp <= 180.dp) 14.sp else 16.sp
                ),
              color = Color.White
            )
          }
        }
      }
    } else {
      url?.let { ImageViewComponent(it, altDescription) }
    }
  }
}

@Composable
fun CategoriesCardItem(
  onItemSelected: (String, String, CategoryType) -> Unit,
  id: String,
  name: String,
  categoryType: CategoryType,
  color: String?,
  url: String?,
  altDescription: String?,
  heightDp: Dp = 320.dp,
  widthDp: Dp = 200.dp,
  padding: Dp = 4.dp,
  shouldShowOverlay: Boolean = false,
  overlayTitle: String = "",
  overlaySubtitle: String = "",
  modifier: Modifier = Modifier
) {

  Card(
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    colors =
      CardDefaults.cardColors(
        containerColor =
          if (color.isNullOrEmpty().not()) Color(parseColor(color!!))
          else MaterialTheme.colorScheme.surface
      ),
    modifier =
      modifier
        .padding(vertical = padding, horizontal = padding)
        .width(widthDp)
        .height(heightDp)
        .background(
          color =
            if (color.isNullOrEmpty().not()) Color(parseColor(color!!))
            else MaterialTheme.colorScheme.surface,
          shape = RoundedCornerShape(16.dp)
        )
        .clickable { onItemSelected.invoke(id, name, categoryType) }
  ) {
    if (shouldShowOverlay) {
      val gradient =
        Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
        url?.let { ImageViewComponent(it, altDescription) }
        Box(modifier.matchParentSize().background(gradient), contentAlignment = Alignment.Center) {
          Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = overlayTitle,
              style =
                MaterialTheme.typography.titleMedium.copy(
                  fontSize = if (widthDp <= 180.dp) 18.sp else 22.sp
                ),
              color = Color.White,
              maxLines = 1,
              modifier = Modifier.padding(horizontal = 8.dp),
              overflow = TextOverflow.Visible,
              letterSpacing = if (widthDp <= 180.dp) 2.sp else 4.sp
            )

            Text(
              text = overlaySubtitle,
              style =
                MaterialTheme.typography.bodySmall.copy(
                  fontSize = if (widthDp <= 180.dp) 14.sp else 16.sp
                ),
              color = Color.White
            )
          }
        }
      }
    } else {
      url?.let { ImageViewComponent(it, altDescription) }
    }
  }
}

@Composable
fun ImageViewComponent(url: String, altDescription: String?) {
  AsyncImage(
    model =
      ImageRequest.Builder(LocalPlatformContext.current)
        .data(url)
        .diskCacheKey(url)
        .memoryCacheKey(url)
        .build(),
    contentDescription = altDescription,
    modifier = Modifier.fillMaxSize(),
    contentScale = ContentScale.Crop,
    alignment = Alignment.Center
  )
}

@Composable
fun LazyGridState.OnBottomReached(loadMore: () -> Unit) {
  val shouldLoadMore = remember {
    derivedStateOf {
      val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf true

      lastVisibleItem.index == layoutInfo.totalItemsCount - 1
    }
  }

  // Convert the state into a cold flow and collect
  LaunchedEffect(shouldLoadMore) {
    snapshotFlow { shouldLoadMore.value }
      .collect {
        // if should load more, then invoke loadMore
        if (it) loadMore()
      }
  }
}

@Composable
fun BackButton(modifier: Modifier = Modifier, onBackPressed: () -> Unit) {
  Box(
    modifier =
      modifier
        .padding(16.dp)
        .size(48.dp)
        .background(color = MaterialTheme.colorScheme.surface, shape = CircleShape),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = EvaIcons.Outline.ArrowIosBack,
      contentDescription = "back",
      modifier = Modifier.padding(8.dp).size(24.dp).clickable { onBackPressed.invoke() },
      tint = MaterialTheme.colorScheme.surfaceTint
    )
  }
}

@Composable
fun BoxScope.ScrollToTopFab(
  isFabVisible: Boolean,
  lazyGridState: LazyGridState,
  modifier: Modifier
) {
  val coroutineScope = rememberCoroutineScope()
  val density = LocalDensity.current

  AnimatedVisibility(
    modifier = modifier,
    visible = isFabVisible,
    enter = slideInVertically { with(density) { 40.dp.roundToPx() } } + fadeIn(),
    exit = fadeOut(animationSpec = keyframes { this.durationMillis = 120 })
  ) {
    FloatingActionButton(
      onClick = { coroutineScope.launch { lazyGridState.animateScrollToItem(0) } },
      backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.secondary,
      elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 15.dp)
    ) {
      androidx.compose.material.Icon(
        imageVector = EvaIcons.Outline.ArrowheadUp,
        contentDescription = "scroll up"
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(searchQuery: String?, isSearching: Boolean, onQueryChanged: (String?) -> Unit) {
  val interactionSource = remember { MutableInteractionSource() }
  val enabled = true
  val singleLine = true

  BasicTextField(
    value = searchQuery ?: "",
    onValueChange = onQueryChanged,
    modifier =
      Modifier.fillMaxWidth()
        .padding(16.dp)
        .height(48.dp)
        .border(
          2.dp,
          color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
          shape = CircleShape
        ),
    interactionSource = interactionSource,
    singleLine = singleLine,
    enabled = enabled,
    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp),
    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
  ) {
    OutlinedTextFieldDefaults.DecorationBox(
      value = searchQuery ?: "",
      innerTextField = it,
      enabled = enabled,
      singleLine = singleLine,
      visualTransformation = VisualTransformation.None,
      interactionSource = interactionSource,
      placeholder = {
        Text(
          stringResource(Res.string.lbl_search),
          style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
        )
      },
      leadingIcon = {
        androidx.compose.material.Icon(
          imageVector = EvaIcons.Outline.Search,
          contentDescription = "search",
          modifier = Modifier.size(20.dp),
          tint = MaterialTheme.colorScheme.primary
        )
      },
      trailingIcon = {
        AnimatedVisibility(
          visible = searchQuery.isNullOrEmpty().not() && isSearching.not(),
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          IconButton(
            onClick = { onQueryChanged.invoke(null) },
          ) {
            androidx.compose.material.Icon(
              imageVector = EvaIcons.Outline.CloseCircle,
              contentDescription = "clear",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }

        AnimatedVisibility(visible = isSearching, enter = fadeIn(), exit = fadeOut()) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 2.dp
          )
        }
      },
      contentPadding = PaddingValues(0.dp),
      container = {
        OutlinedTextFieldDefaults.ContainerBox(
          enabled,
          false,
          interactionSource,
          colors =
            TextFieldDefaults.colors(
              unfocusedContainerColor = MaterialTheme.colorScheme.surface,
              focusedContainerColor = MaterialTheme.colorScheme.surface,
              cursorColor = MaterialTheme.colorScheme.primary,
              unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
              focusedTextColor = MaterialTheme.colorScheme.onSurface,
              unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
              focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
              unfocusedIndicatorColor = Color.Transparent,
              focusedIndicatorColor = Color.Transparent
            ),
          unfocusedBorderThickness = 0.dp,
          focusedBorderThickness = 0.dp
        )
      },
    )
  }
}

fun parseColor(colorString: String): Int {
  if (colorString[0] == '#') { // Use a long to avoid rollovers on #ffXXXXXX
    var color = colorString.substring(1).toLong(16)
    if (colorString.length == 7) { // Set the alpha value
      color = color or -0x1000000
    } else require(colorString.length == 9) { "Unknown color" }
    return color.toInt()
  }
  throw IllegalArgumentException("Unknown color")
}

@Composable
fun GoBack(navigator: Navigator?) {
  if (navigator?.canPop == true) {
    navigator.pop()
  } else {
    Napier.d("Can't go back")
  }
}

@Composable
fun SnowFallComponent(
  height: Dp = 100.dp,
) {
  val snowflakes = remember { List(150) { getRandomSnowFlakes() } }
  val infiniteTransition = rememberInfiniteTransition()

  val offsetY by
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = 1000f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(15000, easing = LinearEasing),
          repeatMode = RepeatMode.Restart
        )
    )

  val color = MaterialTheme.colorScheme.onSurface

  Canvas(
    modifier =
      Modifier.background(
          color = Color.Transparent,
        )
        .fillMaxWidth()
        .height(height)
  ) {
    snowflakes.forEach { drawSnowFlake(it, offsetY % size.height, color) }
  }
}

private fun getRandomSnowFlakes(): Snowflake {
  return Snowflake(
    x = Random.nextFloat(),
    y = Random.nextFloat() * 1000f,
    radius = Random.nextFloat() * 3f + 2f,
    speed = Random.nextFloat() * 1.2f + 1f,
    alpha = Random.nextFloat()
  )
}

fun DrawScope.drawSnowFlake(snowflake: Snowflake, offsetY: Float, color: Color) {
  val newY = (snowflake.y + offsetY * snowflake.speed) % size.height
  drawCircle(
    color = color,
    radius = snowflake.radius,
    center = Offset(snowflake.x * size.width, newY),
    alpha = snowflake.alpha
  )
}

data class Snowflake(
  var x: Float,
  var y: Float,
  var radius: Float,
  var speed: Float,
  var alpha: Float
)
