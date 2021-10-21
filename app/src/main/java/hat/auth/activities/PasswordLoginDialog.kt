package hat.auth.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.text.trimmedLength
import hat.auth.data.EncryptedPassword
import hat.auth.data.PLData
import hat.auth.utils.*
import hat.auth.utils.GT3.start
import hat.auth.utils.ui.CircularProgressWithText
import hat.auth.utils.ui.SingleTextField

private var isLogging       by mutableStateOf(false)
private var isUsernameError by mutableStateOf(false)
private var isPasswordError by mutableStateOf(false)
private var isDialogShowing by mutableStateOf(false)

private var name     by mutableStateOf("")
private var password by mutableStateOf("")

fun showPasswordLoginDialog() { isDialogShowing = true }

@Composable
fun MainActivity.PasswordLoginDialog() {
    if (isDialogShowing) PLD()
}

private fun checkUsername() = (name.matches(PhoneNumRegex) || name.contains('@')).not().apply {
    isUsernameError = this
}
private fun checkPassword() = (password.trimmedLength() !in 8..15).apply {
    isPasswordError = this
}

private fun close() {
    name = ""
    password = ""
    isDialogShowing = false
}

private fun MainActivity.onGo() {
    if (checkUsername() || checkPassword()) return
    GT3.init(
        ctx = this,
        afterTest = { params ->
            runCatching {
                with(parseCaptchaData(params[0].toString())) {
                    PLData(name,EncryptedPassword(password)).login(this).also {
                        check(!it.exists()) { "已经存在相同UID的账户了" }
                    } addTo accountList
                }
                close()
            }.onFailure {
                processException(it)
            }
            isLogging = false
        },
        onDialogResultL = {
            GT3.dismissGeetestDialog()
            isLogging = true
            after.execute(it)
        }
    ).start()
}

@Composable
fun MainActivity.PLD() = Dialog(
    onDismissRequest = {
        if (!isLogging) close()
    }
) {
    Column(
        modifier = Modifier
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp, top = 10.dp)
    ) {
        if (isLogging) {
            CircularProgressWithText(
                text = "正在登录",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
            )
        } else {
            val ufc = FocusRequester()
            val pfc = FocusRequester()
            SingleTextField(
                label = "用户名/邮箱",
                isError = isUsernameError,
                value = name,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(ufc),
                onValueChange = {
                    name = it
                    if (isUsernameError) isUsernameError = checkUsername()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { pfc.requestFocus() }
                )
            )
            SingleTextField(
                label = "密码",
                isError = isPasswordError,
                value = password,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(pfc),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = { onGo() }
                ),
                onValueChange = ovc@{
                    if (it.length > 15) return@ovc
                    password = it
                    if (isPasswordError) isPasswordError = checkPassword()
                }
            )
            LaunchedEffect(Unit) {
                ufc.requestFocus()
            }
        }
    }
}

