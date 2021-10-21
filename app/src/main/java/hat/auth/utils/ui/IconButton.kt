package hat.auth.utils.ui

import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun IconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    onClick: () -> Unit
) = androidx.compose.material.IconButton(
    onClick = onClick,
    modifier = modifier
) {
    Icon(
        tint = tint,
        imageVector = icon,
        contentDescription = null
    )
}
