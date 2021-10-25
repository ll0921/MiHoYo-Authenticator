package hat.auth.activities.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import hat.auth.activities.MainActivity
import hat.auth.utils.*
import hat.auth.utils.GT3.start
import hat.auth.utils.ui.CircularProgressWithText
import hat.auth.utils.ui.SingleTextField
import kotlinx.coroutines.launch

private var isLogging    by mutableStateOf(false)
private var isCodeError  by mutableStateOf(false)
private var isPhoneError by mutableStateOf(false)
private var isDialogShowing by mutableStateOf(false)
private var focusToCodeTextField by mutableStateOf(false)

private var code  by mutableStateOf("")
private var phone by mutableStateOf("")
private var loadingDesc  by mutableStateOf("")

private val CodeRegex = Regex("[0-9]{6}")

fun showCodeLoginDialog() { isDialogShowing = true }

@Composable
fun MainActivity.CodeLoginDialog() {
    if (isDialogShowing) CLD()
}

private fun checkPhone() = (!phone.matches(PhoneNumRegex)).apply {
    isPhoneError = this
}
private fun checkCode() = (!code.matches(CodeRegex)).apply {
    isCodeError = this
}

private fun close() {
    code = ""
    phone = ""
    isDialogShowing = false
}

private fun MainActivity.onNext() {
    if (checkPhone()) return
    GT3.init(
        ctx = this,
        afterTest = { params ->
            runCatching {
                check(!MiHoYoAPI.checkMobileRegistered(phone)) { "该手机号尚未注册" }
                with(parseCaptchaData(params[0].toString())) {
                    MiHoYoAPI.createMobileCaptcha(phone,this)
                }
                focusToCodeTextField = true
                toast("发送成功.")
            }.onFailure {
                processException(it)
            }
            isLogging = false
        },
        onDialogResultL = {
            GT3.dismissGeetestDialog()
            isLogging = true
            loadingDesc = "正在处理请求"
            after.execute(it)
        }
    ).start()
}

private fun MainActivity.onGo() {
    if (checkPhone() || checkCode()) return
    isLogging = true
    loadingDesc = "正在登录"
    ioScope.launch {
        runCatching {
            MiHoYoAuth.login(phone, code).also {
                check(!it.exists()) { "已经存在相同UID的账户了" }
            } addTo accountList
            close()
        }.onFailure {
            processException(it)
        }
        isLogging = false
    }
}

@Composable
private fun MainActivity.CLD() = Dialog(
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
                text = loadingDesc,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
            )
        } else {
            val codeFocusRequester  = FocusRequester()
            val phoneFocusRequester = FocusRequester()
            SingleTextField(
                label = "手机号码",
                value = phone,
                isError = isPhoneError,
                onValueChange = ovc@{
                    if (it.length > 11) return@ovc
                    phone = it
                    if (isPhoneError) isPhoneError = checkPhone()
                    if (focusToCodeTextField) focusToCodeTextField = false
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { onNext() }
                ),
                modifier = Modifier.focusRequester(phoneFocusRequester)
            )
            SingleTextField(
                label = "验证码",
                value = code,
                isError = isCodeError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = { onGo() }
                ),
                onValueChange = ovc@{
                    if (it.length > 6) return@ovc
                    code = it
                    if (isCodeError) isCodeError = checkCode()
                },
                modifier = Modifier.focusRequester(codeFocusRequester)
            )
            LaunchedEffect(Unit) {
                if (focusToCodeTextField) {
                    codeFocusRequester.requestFocus()
                } else {
                    phoneFocusRequester.requestFocus()
                }
            }
        }
    }
}
