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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.Outline
import compose.icons.evaicons.fill.Heart
import compose.icons.evaicons.fill.Info
import compose.icons.evaicons.outline.Heart
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.ishubhamsingh.splashy.CommonRes
import dev.ishubhamsingh.splashy.core.utils.Platform
import dev.ishubhamsingh.splashy.core.utils.getFormattedDateTime
import dev.ishubhamsingh.splashy.core.utils.getPlatform
import dev.ishubhamsingh.splashy.features.details.DetailsViewModel
import dev.ishubhamsingh.splashy.features.details.WallpaperScreenType
import dev.ishubhamsingh.splashy.models.Photo
import dev.ishubhamsingh.splashy.ui.components.BackButton
import dev.ishubhamsingh.splashy.ui.components.getKamelConfig
import dev.ishubhamsingh.splashy.ui.components.parseColor
import dev.ishubhamsingh.splashy.ui.theme.getLatoBold
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
  factory: PermissionsControllerFactory = rememberPermissionsControllerFactory(),
  viewModel: DetailsViewModel =
    getViewModel(
      "details-screen",
      factory = viewModelFactory { DetailsViewModel(factory.createPermissionsController()) }
    ),
  onShowSnackBar: (String) -> Unit
) {
  val state by viewModel.state.collectAsState()

  val bottomSheetScaffoldState =
    rememberBottomSheetScaffoldState(
      bottomSheetState =
        rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded)
    )

  BindEffect(viewModel.permissionsController)

  LaunchedEffect(state.snackBarMessage) {
    state.snackBarMessage?.let {
      onShowSnackBar.invoke(it)
      viewModel.resetSnackBarMessage()
    }
  }

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
      sheetContent = {
        PhotoDetailsContainer(
          photo = photo,
          isFavourite = state.isFavourite,
          isDownloading = state.isDownloading,
          isApplying = state.isApplying,
          onSetFavourite = { viewModel.onEvent(DetailsEvent.AddFavourite) },
          onRemoveFavourite = { viewModel.onEvent(DetailsEvent.RemoveFavourite) },
          onDownloadClicked = { viewModel.onEvent(DetailsEvent.DownloadPhoto) },
          onApplyClicked = { viewModel.onEvent(DetailsEvent.ShowApplyWallpaperDialog) }
        )
      },
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
      BackButton(onBackPressed = { navigator.goBack() })

      if (state.shouldShowApplyWallpaperDialog) {
        ApplyWallpaperDialog(
          onActionSelected = { viewModel.onEvent(DetailsEvent.ApplyAsWallpaper(it)) },
          onDialogDismiss = { viewModel.onEvent(DetailsEvent.DismissApplyWallpaperDialog) }
        )
      }
    }
  }
}

@Composable
fun PhotoContainer(modifier: Modifier = Modifier, photo: Photo) {
  Column(
    modifier = modifier.fillMaxSize().background(color = Color(parseColor(photo.color))),
    verticalArrangement = Arrangement.Center
  ) {
    photo.urls?.regular?.let {
      CompositionLocalProvider(LocalKamelConfig provides getKamelConfig(it)) {
        KamelImage(
          resource = asyncPainterResource(data = it),
          contentDescription = photo.altDescription,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop,
          animationSpec = tween()
        )
      }
    }
  }
}

@Composable
fun PhotoDetailsContainer(
  photo: Photo,
  isFavourite: Boolean,
  isDownloading: Boolean,
  isApplying: Boolean,
  onSetFavourite: () -> Unit,
  onRemoveFavourite: () -> Unit,
  onDownloadClicked: () -> Unit,
  onApplyClicked: () -> Unit
) {
  Column(
    modifier = Modifier.padding(16.dp).fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.Start
  ) {
    SheetProfileRow(
      photo = photo,
      isFavourite = isFavourite,
      onSetFavourite = onSetFavourite,
      onRemoveFavourite = onRemoveFavourite
    )
    SheetActionRow(
      isDownloading = isDownloading,
      isApplying = isApplying,
      onDownloadClicked = onDownloadClicked,
      onApplyClicked = onApplyClicked
    )
    SheetPhotoDetails(photo)
  }
}

@Composable
fun SheetProfileRow(
  photo: Photo,
  isFavourite: Boolean,
  onSetFavourite: () -> Unit,
  onRemoveFavourite: () -> Unit
) {
  photo.user.let { user ->
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        user?.profileImage?.let { profilePic ->
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
            text = user?.name ?: "${user?.firstName} ${user?.lastName}",
            style = MaterialTheme.typography.headlineMedium
          )
          Text(
            text = "@${user?.username ?: ""}",
            style =
              MaterialTheme.typography.bodySmall.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
              )
          )
        }
      }

      FilledTonalIconButton(
        onClick = {
          if (isFavourite) {
            onRemoveFavourite.invoke()
          } else {
            onSetFavourite.invoke()
          }
        }
      ) {
        Crossfade(targetState = isFavourite) { isFavourite ->
          if (isFavourite) {
            Icon(imageVector = EvaIcons.Fill.Heart, contentDescription = "Fav")
          } else {
            Icon(imageVector = EvaIcons.Outline.Heart, contentDescription = "Fav")
          }
        }
      }
    }
  }
}

@Composable
fun SheetActionRow(
  isDownloading: Boolean,
  isApplying: Boolean,
  onDownloadClicked: () -> Unit,
  onApplyClicked: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    if (getPlatform() == Platform.iOS) {
      Button(
        onClick = { onDownloadClicked.invoke() },
        modifier = Modifier.fillMaxWidth().weight(1f),
        shape = RoundedCornerShape(8.dp),
        enabled = !isDownloading
      ) {
        AnimatedVisibility(visible = isDownloading) {
          CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = MaterialTheme.colorScheme.onSurface,
            strokeWidth = 2.dp
          )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
          text = CommonRes.string.save_to_photos,
          style =
            MaterialTheme.typography.titleSmall.copy(
              fontFamily = getLatoRegular(),
              fontSize = 14.sp
            )
        )
      }
    } else {
      OutlinedButton(
        onClick = { onDownloadClicked.invoke() },
        modifier = Modifier.fillMaxWidth().weight(1f),
        shape = RoundedCornerShape(8.dp),
        enabled = !isDownloading
      ) {
        AnimatedVisibility(visible = isDownloading) {
          CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = MaterialTheme.colorScheme.onSurface,
            strokeWidth = 2.dp
          )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
          text = CommonRes.string.lbl_download,
          style =
            MaterialTheme.typography.titleSmall.copy(
              fontFamily = getLatoRegular(),
              fontSize = 14.sp
            )
        )
      }
      Spacer(modifier = Modifier.size(16.dp))
      Button(
        onClick = { onApplyClicked.invoke() },
        modifier = Modifier.fillMaxWidth().weight(1f),
        shape = RoundedCornerShape(8.dp),
        enabled = !isApplying
      ) {
        AnimatedVisibility(visible = isApplying) {
          CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = MaterialTheme.colorScheme.onSurface,
            strokeWidth = 2.dp
          )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
          text = CommonRes.string.lbl_apply_wallpaper,
          style =
            MaterialTheme.typography.titleSmall.copy(
              fontFamily = getLatoRegular(),
              fontSize = 14.sp
            ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
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
        text = CommonRes.string.lbl_details_caps,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodyMedium
      )
    }

    Column(
      modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      PhotoDetailItem(CommonRes.string.lbl_desc) {
        Text(
          if (photo.description.isNullOrEmpty()) photo.altDescription ?: "" else photo.description,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
        )
      }

      photo.createdAt?.let {
        PhotoDetailItem(CommonRes.string.lbl_posted_on) {
          Text(
            text = getFormattedDateTime(it),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
          )
        }
      }

      PhotoDetailItem(CommonRes.string.lbl_size) {
        Text(
          "${photo.width} x ${photo.height}",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
        )
      }

      PhotoDetailItem(CommonRes.string.lbl_likes) {
        Text(
          text = photo.likes.toString(),
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
        )
      }

      PhotoDetailItem(CommonRes.string.lbl_color) {
        Box(
          modifier =
            Modifier.size(18.dp)
              .background(color = Color(parseColor(photo.color)), shape = CircleShape)
        )
      }

      photo.topicSubmissions?.let {
        if (it.getApprovedTopics().isNotEmpty()) {
          PhotoDetailItem(CommonRes.string.lbl_topics, alignment = Alignment.CenterVertically) {
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
  FlowRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
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

@Composable
fun ApplyWallpaperDialog(
  onActionSelected: (WallpaperScreenType) -> Unit,
  onDialogDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDialogDismiss,
    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
  ) {
    Card(
      shape = RoundedCornerShape(12.dp),
      colors =
        CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
      Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = CommonRes.string.title_apply_wallpaper_dialog,
          fontSize = 24.sp,
          fontFamily = getLatoBold(),
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
          text = CommonRes.string.desc_apply_wallpaper_dialog,
          fontSize = 14.sp,
          fontFamily = getLatoRegular(),
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(16.dp))

        OutlinedButton(
          onClick = {
            onActionSelected.invoke(WallpaperScreenType.OTHER_APPLICATION)
            onDialogDismiss.invoke()
          },
          modifier = Modifier.fillMaxWidth(),
          shape =
            RoundedCornerShape(
              topStart = 12.dp,
              topEnd = 12.dp,
              bottomStart = 4.dp,
              bottomEnd = 4.dp
            )
        ) {
          Text(
            text = CommonRes.string.lbl_apply_wall_other_app,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = getLatoRegular(),
            modifier = Modifier.padding(8.dp)
          )
        }
        Spacer(modifier = Modifier.size(8.dp))

        OutlinedButton(
          onClick = {
            onActionSelected.invoke(WallpaperScreenType.HOME_SCREEN)
            onDialogDismiss.invoke()
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = CommonRes.string.lbl_apply_wall_homescreen,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = getLatoRegular(),
            modifier = Modifier.padding(8.dp)
          )
        }
        Spacer(modifier = Modifier.size(8.dp))

        OutlinedButton(
          onClick = {
            onActionSelected.invoke(WallpaperScreenType.LOCK_SCREEN)
            onDialogDismiss.invoke()
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = CommonRes.string.lbl_apply_wall_lockscreen,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = getLatoRegular(),
            modifier = Modifier.padding(8.dp)
          )
        }
        Spacer(modifier = Modifier.size(8.dp))

        Button(
          onClick = {
            onActionSelected.invoke(WallpaperScreenType.BOTH)
            onDialogDismiss.invoke()
          },
          modifier = Modifier.fillMaxWidth(),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.secondaryContainer,
              contentColor = MaterialTheme.colorScheme.secondary
            ),
          shape =
            RoundedCornerShape(
              topStart = 4.dp,
              topEnd = 4.dp,
              bottomStart = 12.dp,
              bottomEnd = 12.dp
            )
        ) {
          Text(
            text = CommonRes.string.lbl_apply_wall_both,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = getLatoRegular(),
            modifier = Modifier.padding(8.dp)
          )
        }
        Spacer(modifier = Modifier.size(8.dp))
      }
    }
  }
}
