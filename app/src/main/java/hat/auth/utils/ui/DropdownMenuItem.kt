package hat.auth.utils.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun DropdownMenuItem(
    text: String,
    onClick: () -> Unit
) = androidx.compose.material.DropdownMenuItem(onClick = onClick) {
    Text(text)
}
