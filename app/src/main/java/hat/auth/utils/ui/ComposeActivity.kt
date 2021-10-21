package hat.auth.utils.ui

import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import hat.auth.R

open class ComposeActivity : AppCompatActivity() {

    fun init(content: @Composable ColumnScope.() -> Unit) {
        setContent {
            MaterialTheme(
                colors = MaterialTheme.colors.copy(
                    primary = Color(0xFF2196F3)
                )
            ) {
                Column(content = content)
            }
        }
    }

    fun setMainTheme() = setTheme(R.style.Theme_MiHoYoAuthenticator)
}