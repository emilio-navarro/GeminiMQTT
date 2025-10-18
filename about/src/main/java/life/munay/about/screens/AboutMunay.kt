package life.munay.about.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import life.munay.about.R
import life.munay.about.viewmodels.AboutViewModel
import life.munay.core.resusables.composables.fonts.FontConfiguration
import life.munay.core.resusables.composables.themes.AppTheme

@Composable
fun AboutMunay(
    aboutViewModel: AboutViewModel? = null
) {
    val versionName = aboutViewModel?.getApplicationVersionInfo() ?: { "1.0.0" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Top section with logo and main content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Logo at the top, centered
                Image(
                    painter = painterResource(id = R.drawable.munay_logo),
                    contentDescription = "Munay Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 24.dp)
                )

                // MQTT title
                Text(
                    text = "GeminiMQTT",
                    style = FontConfiguration.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // By Munay subtitle
                Text(
                    text = "by Munay",
                    style = FontConfiguration.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // MIT License indication
                Text(
                    text = "MIT License",
                    style = FontConfiguration.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }

            // App version at the bottom (dynamically retrieved)
            Text(
                text = "Version $versionName",
                style = FontConfiguration.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutMunayPreview() {
    AppTheme {
        AboutMunay()
    }
}
