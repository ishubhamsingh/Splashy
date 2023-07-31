package dev.ishubhamsingh.splashy.core.navigation

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.AllIcons
import compose.icons.EvaIcons
import compose.icons.FeatherIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.Outline
import compose.icons.evaicons.fill.Facebook
import compose.icons.evaicons.fill.Folder
import compose.icons.evaicons.fill.Heart
import compose.icons.evaicons.fill.Home
import compose.icons.evaicons.outline.Folder
import compose.icons.evaicons.outline.Heart
import compose.icons.evaicons.outline.Home
import compose.icons.feathericons.Folder
import compose.icons.feathericons.Heart
import compose.icons.feathericons.Home

sealed class Screen(
    val route: String,
    val title: String,
    val selectedNavIcon: ImageVector,
    val unselectedNavIcon: ImageVector
) {
    object Home: Screen(route = "home", title = "Home", selectedNavIcon = EvaIcons.Fill.Home,
        unselectedNavIcon = EvaIcons.Outline.Home
    )

    object Collections: Screen(route = "collections", title = "Collections", selectedNavIcon = EvaIcons.Fill.Folder,
        unselectedNavIcon = EvaIcons.Outline.Folder
    )

    object Favourites: Screen(route = "favourites", title = "Favourites", selectedNavIcon = EvaIcons.Fill.Heart,
        unselectedNavIcon = EvaIcons.Outline.Heart
    )
}
