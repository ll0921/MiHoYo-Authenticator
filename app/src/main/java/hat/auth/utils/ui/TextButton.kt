package hat.auth.utils.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun TextButton(
    text: String,
    textColor: Color = Color.Unspecified,
    onClick: () -> Unit
) = androidx.compose.material.TextButton(onClick = onClick) {
    Text(
        text = text,
        color = textColor
    )
}

