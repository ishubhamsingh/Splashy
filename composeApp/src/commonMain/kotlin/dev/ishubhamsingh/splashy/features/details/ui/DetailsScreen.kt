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
package dev.ishubhamsingh.splashy.features.details.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.Outline
import compose.icons.evaicons.fill.Info
import compose.icons.evaicons.outline.Heart
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.ishubhamsingh.splashy.core.utils.getFormattedDateTime
import dev.ishubhamsingh.splashy.features.details.DetailsViewModel
import dev.ishubhamsingh.splashy.models.Photo
import dev.ishubhamsingh.splashy.ui.components.BackButton
import dev.ishubhamsingh.splashy.ui.components.getKamelConfig
import dev.ishubhamsingh.splashy.ui.components.parseColor
import dev.ishubhamsingh.splashy.ui.theme.getLatoRegular
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.kamel.image.config.LocalKamelConfig
import moe.tlaster.precompose.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
  navigator: Navigator,
  id: String,
  viewModel: DetailsViewModel =
    getViewModel("details-screen", factory = viewModelFactory { DetailsViewModel() })
) {
  val state by viewModel.state.collectAsState()

  val bottomSheetScaffoldState =
    rememberBottomSheetScaffoldState(
      bottomSheetState =
        rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded)
    )

  LaunchedEffect(Unit) { viewModel.onEvent(DetailsEvent.LoadDetails(id)) }
  if (state.isLoading) {
    Column(
      modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      CircularProgressIndicator(
        modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally),
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
  state.photo?.let { photo ->
    BottomSheetScaffold(
      scaffoldState = bottomSheetScaffoldState,
      sheetShape =
        MaterialTheme.shapes.medium.copy(
          topStart = CornerSize(4),
          topEnd = CornerSize(4),
          bottomEnd = CornerSize(0),
          bottomStart = CornerSize(0)
        ),
      sheetSwipeEnabled = true,
      sheetContainerColor = MaterialTheme.colorScheme.surface,
      sheetContentColor = MaterialTheme.colorScheme.onSurface,
      sheetContent = { PhotoDetailsContainer(photo) },
      sheetPeekHeight = 180.dp,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
      sheetDragHandle = {
        Surface(
          modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
          color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
          shape = CircleShape
        ) {
          Box(modifier = Modifier.size(height = 4.dp, width = 32.dp))
        }
      },
      sheetShadowElevation = 16.dp,
    ) {
      PhotoContainer(photo = photo)
      BackButton(navigator = navigator)
    }
  }
}

@Composable
fun PhotoContainer(modifier: Modifier = Modifier, photo: Photo) {
  Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
    photo.let {
      CompositionLocalProvider(LocalKamelConfig provides getKamelConfig(photo.urls.regular)) {
        KamelImage(
          resource = asyncPainterResource(data = it.urls.regular),
          contentDescription = it.altDescription,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop,
          animationSpec = tween()
        )
      }
    }
  }
}

@Composable
fun PhotoDetailsContainer(photo: Photo) {
  Column(
    modifier = Modifier.padding(16.dp).fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.Start
  ) {
    SheetProfileRow(photo)
    SheetActionRow(photo)
    SheetPhotoDetails(photo)
  }
}

@Composable
fun SheetProfileRow(photo: Photo) {
  photo.user.let { user ->
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        user.profileImage?.let { profilePic ->
          CompositionLocalProvider(LocalKamelConfig provides getKamelConfig(profilePic.medium)) {
            KamelImage(
              modifier = Modifier.size(36.dp).clip(CircleShape),
              resource = asyncPainterResource(data = profilePic.medium),
              contentDescription = user.name,
              contentScale = ContentScale.Crop,
              animationSpec = tween()
            )
          }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = user.name ?: "${user.firstName} ${user.lastName}",
            style = MaterialTheme.typography.headlineMedium
          )
          Text(
            text = "@${user.username ?: ""}",
            style =
              MaterialTheme.typography.bodySmall.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
              )
          )
        }
      }

      FilledTonalIconButton(onClick = {}) {
        Icon(imageVector = EvaIcons.Outline.Heart, contentDescription = "Fav")
      }
    }
  }
}

@Composable
fun SheetActionRow(photo: Photo) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    OutlinedButton(
      onClick = {},
      modifier = Modifier.fillMaxWidth().weight(1f),
      shape = RoundedCornerShape(8.dp)
    ) {
      Text(
        text = "Download",
        style =
          MaterialTheme.typography.titleSmall.copy(fontFamily = getLatoRegular(), fontSize = 14.sp)
      )
    }
    Spacer(modifier = Modifier.size(16.dp))
    Button(
      onClick = {},
      modifier = Modifier.fillMaxWidth().weight(1f),
      shape = RoundedCornerShape(8.dp)
    ) {
      Text(
        text = "Apply",
        style =
          MaterialTheme.typography.titleSmall.copy(fontFamily = getLatoRegular(), fontSize = 14.sp)
      )
    }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SheetPhotoDetails(photo: Photo) {
  Column(modifier = Modifier.padding(top = 16.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = EvaIcons.Fill.Info,
        contentDescription = "details",
        tint = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.size(8.dp))
      Text(
        text = "DETAILS",
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodyMedium
      )
    }

    Column(
      modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      PhotoDetailItem("Description :") {
        Text(
          photo.altDescription,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
        )
      }

      PhotoDetailItem("Posted on :") {
        Text(
          text = getFormattedDateTime(photo.createdAt),
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
        )
      }

      PhotoDetailItem("Size :") {
        Text(
          "${photo.width} x ${photo.height}",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
        )
      }

      PhotoDetailItem("Likes :") {
        Text(
          text = photo.likes.toString(),
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
        )
      }

      PhotoDetailItem("Color :") {
        Box(
          modifier =
            Modifier.size(18.dp)
              .background(color = Color(parseColor(photo.color)), shape = CircleShape)
        )
      }

      photo.topicSubmissions?.let {
        if (it.getApprovedTopics().isNotEmpty()) {
          PhotoDetailItem("Topics :", alignment = Alignment.CenterVertically) {
            TopicDetailItem(it.getApprovedTopics())
          }
        }
      }
    }
  }
}

@Composable
fun PhotoDetailItem(
  title: String,
  alignment: Alignment.Vertical = Alignment.Top,
  suffix: @Composable () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = alignment
  ) {
    Text(
      title,
      style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
      modifier = Modifier.fillMaxWidth().weight(3f)
    )
    Box(modifier = Modifier.fillMaxWidth().weight(7f)) { suffix() }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopicDetailItem(approvedTopics: ArrayList<String>) {
  FlowRow {
    approvedTopics.forEach {
      SuggestionChip(
        label = {
          Text(
            text = it,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            maxLines = 1,
            softWrap = true,
            overflow = TextOverflow.Ellipsis
          )
        },
        onClick = {},
        shape = CircleShape,
        colors =
          SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.secondary
          ),
        border =
          SuggestionChipDefaults.suggestionChipBorder(
            borderColor = MaterialTheme.colorScheme.secondary
          ),
      )
    }
  }
}
