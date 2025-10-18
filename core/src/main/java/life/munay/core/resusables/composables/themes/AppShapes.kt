package life.munay.core.resusables.composables.themes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val shapes =
    Shapes(
        small = RoundedCornerShape(percent = 50),
        medium = RoundedCornerShape(size = 6.dp),
        large =
        RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 20.dp,
            bottomEnd = 0.dp,
            bottomStart = 20.dp
        )
    )
