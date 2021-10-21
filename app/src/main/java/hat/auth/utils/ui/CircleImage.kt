package hat.auth.utils.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircleImage(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    borderSize: Dp = 2.dp,
    borderColor: Color = Color(0xFFE3E3E3),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Image(
        bitmap = bitmap,
        contentDescription = contentDescription,
        modifier = modifier.then(
            Modifier
                .clip(CircleShape)
                .border(borderSize,borderColor,CircleShape)
        ),
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter
    )
}
