package hat.auth.utils.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CircularProgressWithText(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = text,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}
