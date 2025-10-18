package life.munay.core.resusables.composables.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import life.munay.core.resusables.composables.fonts.LocalAppFonts
import life.munay.core.resusables.composables.themes.AppDimensions.PaddingStandard
import life.munay.core.resusables.composables.themes.AppTheme
import life.munay.core.resusables.composables.themes.extendedColorScheme

@Composable
fun ScrollablePage(
    backgroundColor: Color = MaterialTheme.extendedColorScheme.backgroundScreen,
    headerContent: @Composable () -> Unit = {},
    bodyContent: @Composable () -> Unit = {},
    footerContent: @Composable () -> Unit = {},
    scrollToTopTrigger: Any? = null
) {
    val listState = rememberLazyListState()

    LaunchedEffect(scrollToTopTrigger) {
        listState.scrollToItem(0)
    }

    Column(
        modifier =
        Modifier
            .background(color = backgroundColor)
            .fillMaxSize()
    ) {
        HeaderContent(headerContent = headerContent, backgroundColor = backgroundColor)
        LazyColumn(
            state = listState,
            modifier =
            Modifier
                .background(backgroundColor)
                .weight(1f)
        ) {
            item {
                bodyContent()
            }
        }
        FooterContent(footerContent = footerContent, backgroundColor = backgroundColor)
    }
}

@Composable
private fun HeaderContent(
    headerContent: @Composable () -> Unit?,
    backgroundColor: Color = MaterialTheme.extendedColorScheme.backgroundScreen
) {
    Row(
        modifier =
        Modifier
            .background(color = backgroundColor)
            .fillMaxWidth()
    ) {
        headerContent()
    }
}

@Composable
private fun FooterContent(
    footerContent: @Composable () -> Unit?,
    backgroundColor: Color = MaterialTheme.extendedColorScheme.backgroundScreen
) {
    Row(
        modifier =
        Modifier
            .background(color = backgroundColor)
            .fillMaxWidth()
    ) {
        footerContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HeaderContent() {
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

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BodyContent() {
    AppTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.extendedColorScheme.backgroundScreen)
                .fillMaxSize()
                .padding(PaddingStandard)
        ) {
            Text(
                "Scrollable Page Content",
                style = LocalAppFonts.current.titleLarge,
                modifier = Modifier.padding(bottom = PaddingStandard)
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewScrollablePage() {
    AppTheme {
        ScrollablePage(
            headerContent = { HeaderContent() },
            bodyContent = { BodyContent() }
        )
    }
}
