package com.rohnsha.medbuddyai.screens.auth

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.R
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.domain.viewmodels.userAuthVM
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.screens.TextInputThemed
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.customRed
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.formAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAuthScreen(
    mode: Int, // 1 for login, 0 for signup
    userAuthVM: userAuthVM,
    navController: NavHostController,
    snackBarToggleVM: snackBarToggleVM
) {

    val selectedMode= remember {
        mutableIntStateOf(mode)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (selectedMode.value == 0) "Create Account" else "Welcome Back!",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 26.sp
                    )
                },
                actions = {
                    /*Image(
                        imageVector = Icons.Outlined.AdminPanelSettings,
                        contentDescription = "Show accuracy button",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(24.dp)
                            .clickable {
                                //navController.navigate(bottomNavItems.Preferences.route)
                            }
                    )*/
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back Icon"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            )
        },
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.tertiary
    ){
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .scrollable(
                    orientation = Orientation.Vertical,
                    enabled = true,
                    state = scrollState
                )
                .background(MaterialTheme.colorScheme.tertiary)
        ) {

            Spacer(modifier = Modifier.height(48.dp))
            Image(painter = painterResource(id = R.drawable.login), contentDescription = null, modifier= Modifier.padding(horizontal = 25.dp))
            when (selectedMode.value) {
                0 -> { RegisterUI(userAuthVM = userAuthVM, selectedMode = { selectedMode.intValue= it }, onSucess = { navController.navigate(
                    bottomNavItems.Home.route) }, snackBarToggleVM = snackBarToggleVM)  }
                1 -> { LoginUI(userAuthVM, selectedMode = { selectedMode.intValue= it }, onSucess = { navController.navigate(
                    bottomNavItems.Home.route) }, snackBarToggleVM = snackBarToggleVM) }
            }

        }
    }

}

@Composable
fun RegisterUI(
    userAuthVM: userAuthVM,
    onSucess: () -> Unit,
    selectedMode: (Int) -> Unit,
    snackBarToggleVM: snackBarToggleVM
) {

    val firstName= remember {
        mutableStateOf("")
    }
    val lastName= remember {
        mutableStateOf("")
    }
    val mail= remember {
        mutableStateOf("")
    }
    val password= remember {
        mutableStateOf("")
    }
    val username= remember {
        mutableStateOf("")
    }

    val mailError= remember {
        mutableStateOf(false)
    }
    val passwordError= remember {
        mutableStateOf(false)}
    val usernameError= remember {
        mutableStateOf(false)
    }
    val fnameError= remember {
        mutableStateOf(false)
    }
    val lnameError= remember {
        mutableStateOf(false)
    }

    val scope= rememberCoroutineScope()
    val isUsernameValid= remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = username.value != "" && !isUsernameValid.value) {
        Log.d("Username", isUsernameValid.value.toString())
        try {
            isUsernameValid.value = userAuthVM.isUsernameValid(username.value)
            Log.d("Username", "isUsernameValid: ${isUsernameValid.value}")
        } catch (e: Exception) {
            Log.d("Username", "error: ${e.message}")
            snackBarToggleVM.SendToast(
                message = e.message.toString(),
                indicator_color = Color.Red,
                icon = Icons.Outlined.Warning,
            )
        }
    }

    Log.d("Username", "usernmae; ${isUsernameValid.value}")

    val buttonPressedCount= remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(key1 = mail.value, key2 = password.value, key3 = username.value) {
        if (buttonPressedCount.intValue > 0){
            mailError.value= isEntryEmpty(mail.value)
            passwordError.value= isEntryEmpty(password.value)
            usernameError.value= isEntryEmpty(username.value)
        }
    }

    LaunchedEffect(key1 = lastName.value, key2 = firstName.value) {
        if (buttonPressedCount.intValue > 0){
            fnameError.value= isEntryEmpty(firstName.value)
            lnameError.value= isEntryEmpty(lastName.value)
        }
    }

    LazyColumn {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp))
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                TextInputThemed(
                    value = firstName.value,
                    onValueChanged = { firstName.value = it },
                    label = "First name",
                    onClose = { firstName.value= "" },
                    errorBool = fnameError.value
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextInputThemed(
                    value = lastName.value,
                    onValueChanged = { lastName.value = it },
                    label = "Last name",
                    onClose = { lastName.value="" },
                    errorBool = lnameError.value
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextInputThemed(
                    value = mail.value,
                    onValueChanged = { mail.value = it },
                    label = "Email address",
                    onClose = { mail.value= "" },
                    errorBool = mailError.value,
                    regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$",
                    regexUnMatchErrorText = "Must be in `__@_._` format"
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextInputThemed(
                    modifier2 = Modifier
                        .onFocusChanged { focusState ->
                            when {
                                focusState.isFocused -> {
                                    Log.d("Focus", "Focused")
                                    scope.launch {
                                        isUsernameValid.value = userAuthVM.isUsernameValid(username.value)
                                    }
                                }

                                focusState.hasFocus ->
                                    Log.d("Focus", "Has focus")


                                else ->
                                    Log.d("Focus", "Not focused")
                            }
                        },
                    value = username.value,
                    onValueChanged = { username.value = it },
                    label = "Username",
                    onClose = { /*TODO*/ },
                    regex = "^[a-zA-Z0-9_-]{3,16}\$",
                    regexUnMatchErrorText = "Must be 3-16 chars with lower, uppercase, hyphens, underscores",
                    errorBool = usernameError.value
                )
                Spacer(modifier = Modifier.height(15.dp))
                PasswordTextField(
                    password = password.value,
                    onPasswordChanged = { password.value= it },
                    errorBool = passwordError.value,
                    regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}\$",
                    regexUnMatchErrorText = "Must be 8+ chars, 1 letter, 1 #"
                )
                Spacer(modifier = Modifier.height(24.dp))
                val checkStatus= remember {
                    mutableStateOf(false)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = checkStatus.value,
                        onCheckedChange = { checkStatus.value = it },
                        modifier = Modifier.size(8.dp)
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable { checkStatus.value = !checkStatus.value },
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        text = "I understand `SwasthAI` is not a replacement for Doctors.",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                val context= LocalContext.current
                val isLoading= remember {
                    mutableStateOf(false)
                }
                AuthCTA(
                    isLoading = isLoading.value,
                    isBtnEnabled = checkStatus.value,
                    btnAction = {
                        scope.launch {
                            isLoading.value= true
                            mailError.value= isEntryEmpty(mail.value)
                            passwordError.value= isEntryEmpty(password.value)
                            usernameError.value= isEntryEmpty(username.value)
                            fnameError.value= isEntryEmpty(firstName.value)
                            lnameError.value= isEntryEmpty(lastName.value)
                            buttonPressedCount.intValue++
                            if (!(mailError.value || passwordError.value ||  fnameError.value || lnameError.value)){
                                if (isUsernameValid.value){
                                    userAuthVM.registerUser(
                                        password.value,
                                        mail.value, onSucess,
                                        fname = firstName.value,
                                        lname = lastName.value,
                                        username = username.value,
                                        snackBarToggleVM = snackBarToggleVM,
                                        context = context
                                    )
                                } else {
                                    if (username.value==""){
                                        snackBarToggleVM.SendToast(
                                            message = "Username cant be empty",
                                            indicator_color = Color.Red,
                                            icon = Icons.Outlined.Warning,
                                        )
                                    } else {
                                        val isUsernameTaken = userAuthVM.isUsernameValid(username.value)
                                        if (!isUsernameTaken){
                                            userAuthVM.registerUser(
                                                password.value,
                                                mail.value,
                                                onSucess,
                                                fname = firstName.value,
                                                lname = lastName.value,
                                                username = username.value,
                                                snackBarToggleVM = snackBarToggleVM,
                                                context = context
                                            )
                                        } else {
                                            snackBarToggleVM.SendToast(
                                                message = "Username already taken",
                                                indicator_color = Color.Red,
                                                icon = Icons.Outlined.Warning,
                                            )
                                        }
                                    }
                                }
                            }
                            isLoading.value= false
                        }
                    }, txtAction =  {
                    selectedMode(1)
                }, title = "Let's Get Started", subtitle = "Already have an account?", subtitleCTA = "Login")
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun LoginUI(
    userAuthVM: userAuthVM,
    onSucess: () -> Unit,
    selectedMode: (Int) -> Unit,
    snackBarToggleVM: snackBarToggleVM
) {

    val context= LocalContext.current
    val isLoading= remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        userAuthVM.retrievePassword(
            context = context, snackBarToggleVM = snackBarToggleVM, onSucess = onSucess
        )
    }
    val mail= remember {
        mutableStateOf("")
    }
    val mailError= remember {
        mutableStateOf(false)
    }

    val password= remember {
        mutableStateOf("")
    }
    val passwordError= remember {
        mutableStateOf(false)
    }
    val buttonPressedCount= remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(key1 = mail.value, key2 = password.value) {
        if (buttonPressedCount.intValue > 0){
            mailError.value= isEntryEmpty(mail.value)
            passwordError.value= isEntryEmpty(password.value)
        }
    }
    Log.d("Login", "buttonPressedCount: ${buttonPressedCount.intValue}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        TextInputThemed(
            value = mail.value,
            onValueChanged = { mail.value = it },
            label = "Enter your mail",
            onClose = { mail.value= "" },
            errorBool = mailError.value,
            regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$",
            regexUnMatchErrorText = "Must be in `__@_._` format"
        )
        Spacer(modifier = Modifier.height(15.dp))
        PasswordTextField(
            password = password.value,
            onPasswordChanged = { password.value= it }
        )
        Spacer(modifier = Modifier.height(24.dp))
        AuthCTA(
            btnAction = {
                isLoading.value= true
                mailError.value= isEntryEmpty(mail.value)
                passwordError.value= isEntryEmpty(password.value)
                buttonPressedCount.intValue++
                Log.d("Login", "mailError: ${!(mailError.value || passwordError.value)}")
                if (!(mailError.value || passwordError.value)){
                    Log.d("Login", "insideMailError: ${!(mailError.value || passwordError.value)}")
                    /*snackBarToggleVM.SendToast(
                        message = "One or more fields are empty",
                        indicator_color = Color.Red,
                        icon = Icons.Outlined.Warning,
                    )*/
                    userAuthVM.loginUser(
                        password = password.value,
                        email = mail.value,
                        onSuccess = onSucess,
                        snackBarToggleVM = snackBarToggleVM
                    )
                }
                isLoading.value= false
                },
            txtAction = {
                selectedMode(0)
            }, title = "Login", subtitle = "Don't have an account?", subtitleCTA = "Sign up")
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun AuthCTA(
    btnAction: (Boolean) -> Unit,
    txtAction: () -> Unit,
    title: String,
    subtitle: String,
    subtitleCTA: String,
    isBtnEnabled: Boolean= true,
    isLoading: Boolean? = null
) {

    Button(
        onClick = { btnAction(false) },
        colors = ButtonDefaults.buttonColors(
            containerColor = customBlue
        ),
        enabled = isBtnEnabled,
        modifier = Modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
    ) {
        Text(text = title, color = Color.White, fontSize = 18.sp, fontFamily = fontFamily)
        if (isLoading == true){
            Spacer(modifier = Modifier.width(8.dp))
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(12.dp),
                strokeWidth = 2.dp
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row {
        Text(text = subtitle, fontSize = 14.sp, fontFamily = fontFamily, color = MaterialTheme.colorScheme.onPrimary)
        Text(text = " ", fontSize = 14.sp, fontFamily = fontFamily, color = MaterialTheme.colorScheme.onPrimary)
        Text(text = subtitleCTA, fontSize = 14.sp, fontFamily = fontFamily, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight(600),
            modifier = Modifier.clickable { txtAction() })
    }
    Spacer(modifier = Modifier.height(25.dp))
}

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChanged: (String) -> Unit,
    errorBool: Boolean= false,
    errorText: String= "This is a required filed",
    regexUnMatchErrorText: String= "Invalid input",
    regex: String?= null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val errorState= remember {
        mutableStateOf(false)
    }
    if (regex!=null){
        errorState.value= !password.matches(Regex(regex)) && password!= ""
    }

    Column {
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = password,
            onValueChange = { onPasswordChanged(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }
                IconButton(onClick = { passwordVisible= !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle Password")
                }
            },
            textStyle = LocalTextStyle.current.copy(fontFamily = fontFamily, fontSize = 18.sp),
            label = {
                Text(
                    text = "Password",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    color = formAccent
                ) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = customBlue,
                unfocusedIndicatorColor = formAccent,
                errorContainerColor = MaterialTheme.colorScheme.surface,
                errorIndicatorColor = customRed,
                errorSupportingTextColor = customRed,
                disabledContainerColor = MaterialTheme.colorScheme.surface
            ),
            isError = errorState.value || errorBool,
            singleLine = true
        )
        if (errorBool || errorState.value && errorText.isNotEmpty()) {
            Text(
                text = if (errorBool) errorText else regexUnMatchErrorText,
                color = customRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

private fun isEntryEmpty(entry: String): Boolean {
    return entry==""
}