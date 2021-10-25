package hat.auth.activities.tap

/*
private var isLogging    by mutableStateOf(false)
private var isCodeError  by mutableStateOf(false)
private var isEmailError by mutableStateOf(false)
private var isDialogShowing by mutableStateOf(false)
private var focusToCodeTextField by mutableStateOf(false)

private var code  by mutableStateOf("")
private var email by mutableStateOf("")
private var loadingDesc  by mutableStateOf("")

private val CodeRegex = Regex("[0-9]{6}")

fun showTapEmailLoginDialog() { isDialogShowing = true }

@Composable
fun MainActivity.TapEmailLoginDialog() {
    if (isDialogShowing) CLD()
}

private fun checkEmail() = (!email.contains('@')).apply {
    isEmailError = this
}

private fun checkCode() = (!code.matches(CodeRegex)).apply {
    isCodeError = this
}

private fun close() {
    isDialogShowing = false
    cWebView.destroy()
}

private var currentTapAccount = TapAccount()
private var currentGtJson = JsonObject()

private fun MainActivity.onNext() {
    if (checkEmail()) return
    GT3.init(
        ctx = this,
        beforeTest = {
            runCatching {
                GT3.getConfig().api1Json = currentGtJson.toOrgJson()
                GT3.getGeetest()
            }.onFailure {
                GT3.dismissGeetestDialog()
                processException(it)
            }
        },
        afterTest = { params ->
            runCatching {
                with(parseCaptchaData(params[0].toString())) {
                    Log.d("Captcha",this.toString())
                    TapAPI.sendCode(email, currentTapAccount, this)
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
    if (checkEmail() || checkCode()) return
    isLogging = true
    loadingDesc = "正在登录"
    ioScope.launch {
        runCatching {
            TapAuth.login(email, code, currentTapAccount).also {
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
            .padding(20.dp)
    ) {
        WebView(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
        )
        /*if (isLogging) {
            CircularProgressWithText(
                text = loadingDesc,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
            )
        } else {
            val codeFocusRequester  = FocusRequester()
            val mailFocusRequester = FocusRequester()
            SingleTextField(
                label = "邮箱账号",
                value = email,
                isError = isEmailError,
                onValueChange = ovc@{
                    email = it
                    if (isEmailError) isEmailError = checkEmail()
                    if (focusToCodeTextField) focusToCodeTextField = false
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { onNext() }
                ),
                modifier = Modifier.focusRequester(mailFocusRequester)
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
                    mailFocusRequester.requestFocus()
                }
            }
        }*/
    }
    LaunchedEffect(Unit) {
        CookieManager.getInstance().removeAllCookies(null)
        cWebView.loadUrl("https://www.taptap.com/auth/login")
        //currentGtJson = TapAPI.initCaptchaAndCookie()
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun WebView.enableJavaScript() = apply { settings.javaScriptEnabled = true }
*/
