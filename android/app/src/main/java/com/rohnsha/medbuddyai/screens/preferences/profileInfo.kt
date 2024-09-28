package com.rohnsha.medbuddyai.screens.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.database.userdata.currentUser.currentUserDataVM
import com.rohnsha.medbuddyai.database.userdata.currentUser.fieldValueDC
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.domain.viewmodels.userAuthVM
import com.rohnsha.medbuddyai.screens.TextInputThemed
import com.rohnsha.medbuddyai.screens.auth.PasswordTextField
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoScreen(
    currentUserDataVM: currentUserDataVM,
    navController: NavHostController,
    userAuthVM: userAuthVM,
    snackBarToggleVM: snackBarToggleVM
) {

    val userInfo= remember {
        mutableStateOf(fieldValueDC(
            username = "",
            fname = "",
            lname = "",
            isDefaultUser = true
        ))
    }
    LaunchedEffect(key1 = true) {
        userInfo.value= currentUserDataVM.getAllUsers().filter { it.isDefaultUser }[0]
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile Info",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 26.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back Icon"
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.tertiary
    ){ values ->
        val password= remember {
            mutableStateOf("")
        }
        val context= LocalContext.current
        val scope= rememberCoroutineScope()

        Column(
            modifier = Modifier.padding(values)
        ) {
            Text(
                text = "We get your personal information from the verification process. If you want to make changes on your personal information, contact our support.",
                fontFamily = fontFamily,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp),
                textAlign = TextAlign.Center,
                color = lightTextAccent
            )
            Column(
                modifier = Modifier
                    .padding(top = 34.dp)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                    .padding(top = 30.dp, start = 24.dp, end = 24.dp)
            ){
                TextInputThemed(
                    value = userInfo.value.fname,
                    onValueChanged = {  },
                    label = "First Name",
                    onClose = {  },
                    isEnabled = false,
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextInputThemed(
                    value = userInfo.value.lname,
                    onValueChanged = {  },
                    label = "Last Name",
                    onClose = {  },
                    isEnabled = false,
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextInputThemed(
                    value = userInfo.value.username,
                    onValueChanged = {  },
                    label = "Username",
                    onClose = {  },
                    isEnabled = false,
                    readOnly = true
                )
                Text(
                    text = "Delete Your Account & Data",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 26.dp, bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                PasswordTextField(password = password.value, onPasswordChanged = {
                    password.value=it
                })
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = customBlue
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        if (password.value!=""){
                            scope.launch {
                                userAuthVM.deletaAccount(
                                    context = context,
                                    password= password.value,
                                    navController = navController,
                                    snackBarToggleVM = snackBarToggleVM
                                )
                            }
                        } else snackBarToggleVM.SendToast(
                            message = "Password cannot be empty",
                            indicator_color = Color.Red,
                            icon = Icons.Outlined.Warning
                        )
                    }) {
                    Text(text = "Delete Account")
                }
            }
        }
    }
}