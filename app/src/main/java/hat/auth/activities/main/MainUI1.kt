package hat.auth.activities.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hat.auth.BuildConfig
import hat.auth.activities.MainActivity
import hat.auth.data.IAccount
import hat.auth.data.MiAccount
import hat.auth.utils.*
import hat.auth.utils.ui.CircleImage
import hat.auth.utils.ui.IconButton
import hat.auth.utils.ui.TextButton
import kotlinx.coroutines.launch

@Composable
fun AccountsColumnItem(
    ia: IAccount,
    avatar: ImageBitmap,
    onInfoClick: () -> Unit,
    onTestClick: () -> Unit = {},
    onItemClick: () -> Unit,
) = Box(
    modifier = Modifier
        .fillMaxWidth()
        .clickable {
            currentAccount = ia
            onItemClick()
        }
) {
    var showInfoButton = true
    val uid = if (ia !is MiAccount) {
        showInfoButton = false
        ia.uid
    } else {
        ia.guid
    }
    Row(
        modifier = Modifier.padding(
            vertical = 12.5.dp,
            horizontal = 20.dp
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleImage(
            bitmap = avatar,
            modifier = Modifier.size(56.dp)
        )
        val c = Color(0xBF000000)
        Column(
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Text(
                text = ia.name,
                fontSize = 20.sp,
                color = c
            )
            Text(
                text = uid,
                color = c
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (showInfoButton) {
                IconButton(
                    tint = c,
                    icon = Icons.Outlined.Info,
                ) {
                    currentAccount = ia
                    onInfoClick()
                }
            }
            if (!showInfoButton && BuildConfig.DEBUG) {
                IconButton(
                    tint = c,
                    icon = Icons.Outlined.Build
                ) {
                    currentAccount = ia
                    onTestClick()
                }
            }
            IconButton(
                tint = c,
                icon = Icons.Outlined.Delete,
            ) {
                currentAccount = ia
                isDialogShowing = true
            }
        }
    }
}

private var isDialogShowing by mutableStateOf(false)

@Composable
fun MainActivity.DeleteAccountDialog() = run {
    if (isDialogShowing) AlertDialog(
        onDismissRequest = { isDialogShowing = false },
        dismissButton = {
            TextButton("确认",Color.Red) {
                isDialogShowing = false
                currentAccount removeFrom accountList
            }
        },
        confirmButton = {
            TextButton("取消") {
                isDialogShowing = false
            }
        },
        title = {
            Text("要永久删除 ${currentAccount.name} 吗?")
        }
    )
}
