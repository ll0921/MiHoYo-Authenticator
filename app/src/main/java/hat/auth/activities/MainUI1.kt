package hat.auth.activities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hat.auth.data.Account
import hat.auth.utils.accountList
import hat.auth.utils.removeFrom
import hat.auth.utils.ui.CircleImage
import hat.auth.utils.ui.IconButton
import hat.auth.utils.ui.TextButton

@Composable
fun AccountsColumnItem(
    account: Account,
    avatar: ImageBitmap,
    onInfoClick: () -> Unit,
    onItemClick: () -> Unit,
) = Box(
    modifier = Modifier
        .fillMaxWidth()
        .clickable {
            currentAccount = account
            onItemClick()
        }
) {
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
                text = account.name,
                fontSize = 20.sp,
                color = c
            )
            Text(
                text = account.guid,
                color = c
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                tint = c,
                icon = Icons.Outlined.Info,
                onClick = {
                    currentAccount = account
                    onInfoClick()
                }
            )
            IconButton(
                tint = c,
                icon = Icons.Outlined.Delete,
                onClick = {
                    currentAccount = account
                    isDialogShowing = true
                }
            )
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
