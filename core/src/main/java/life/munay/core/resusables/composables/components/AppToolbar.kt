package life.munay.core.resusables.composables.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import life.munay.core.resusables.composables.fonts.LocalAppFonts
import life.munay.core.resusables.composables.themes.AppTheme
import life.munay.core.resusables.composables.themes.extendedColorScheme

data class AppToolbarColors(
    val containerColor: Color,
    val actionIconColor: Color,
    val navigationIconColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(
    modifier: Modifier = Modifier,
    title: String = "",
    titleStyle: TextStyle = LocalAppFonts.current.titleMedium.copy(color = MaterialTheme.extendedColorScheme.white),
    subTitle: String? = null,
    subTitleStyle: TextStyle = LocalAppFonts.current.titleSmall.copy(color = MaterialTheme.extendedColorScheme.white),
    showSubtitle: Boolean = true,
    navigationVector: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
    colors: AppToolbarColors = defaultAppToolbarColors(),
    elevation: Dp = 4.dp,
    navigationShow: Boolean = true,
    navigationAction: () -> Unit = {},
    action: @Composable () -> Unit
) {
    AppTheme {
        TopAppBar(
            modifier =
            modifier
                .background(colors.containerColor)
                .shadow(elevation),
            navigationIcon = {
                if (navigationShow) {
                    IconButton(onClick = navigationAction) {
                        Icon(
                            imageVector = navigationVector,
                            contentDescription = "Navigate back",
                            tint = colors.navigationIconColor
                        )
                    }
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = title,
                            style = titleStyle,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )

                        if (showSubtitle && !subTitle.isNullOrEmpty()) {
                            Text(
                                text = subTitle,
                                style = subTitleStyle,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                }
            },
            colors =
            TopAppBarColors(
                containerColor = colors.containerColor,
                titleContentColor = titleStyle.color,
                actionIconContentColor = colors.actionIconColor,
                navigationIconContentColor = colors.navigationIconColor,
                scrolledContainerColor = colors.containerColor
            ),
            actions = { action() }
        )
    }
}

@Composable
fun defaultAppToolbarColors() =
    AppToolbarColors(
        containerColor = MaterialTheme.extendedColorScheme.primary,
        actionIconColor = MaterialTheme.extendedColorScheme.white,
        navigationIconColor = MaterialTheme.extendedColorScheme.white
    )

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewActions() {
    AppTheme {
        val colors = defaultAppToolbarColors()
        Row(
            modifier =
            Modifier
                .background(colors.containerColor)
        ) {
            IconButton(onClick = {}) {
                Icon(
                    tint = colors.actionIconColor,
                    imageVector = Icons.Filled.AccountBox,
                    contentDescription = "video call"
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    tint = colors.actionIconColor,
                    imageVector = Icons.Filled.Call,
                    contentDescription = "phone call"
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    tint = colors.actionIconColor,
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "more options"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewAppToolbar() {
    AppTheme {
        val colors = defaultAppToolbarColors()
        AppToolbar(
            title = "Title",
            action = {
                Row(
                    modifier =
                    Modifier
                        .background(colors.containerColor)
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            tint = colors.actionIconColor,
                            imageVector = Icons.Filled.AccountBox,
                            contentDescription = "video call"
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            tint = colors.actionIconColor,
                            imageVector = Icons.Filled.Call,
                            contentDescription = "phone call"
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            tint = colors.actionIconColor,
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "more options"
                        )
                    }
                }
            },
            colors = colors
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewAppToolbarSubtitle() {
    AppTheme {
        val colors = defaultAppToolbarColors()
        AppToolbar(
            title = "Title",
            subTitle = "My SubTitle",
            action = {
                Row(
                    modifier =
                    Modifier
                        .background(colors.containerColor)
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            tint = colors.actionIconColor,
                            imageVector = Icons.Filled.AccountBox,
                            contentDescription = "video call"
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            tint = colors.actionIconColor,
                            imageVector = Icons.Filled.Call,
                            contentDescription = "phone call"
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            tint = colors.actionIconColor,
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "more options"
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewAppToolbarNoNavigation() {
    AppTheme {
        val colors = defaultAppToolbarColors()
        AppToolbar(
            title = "Title",
            navigationShow = false,
            action = {
                Row(
                    modifier =
                    Modifier
                        .background(colors.containerColor)
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            tint = colors.actionIconColor,
                            imageVector = Icons.Filled.AccountBox,
                            contentDescription = "video call"
                        )
                    }
                }
            }
        )
    }
}
