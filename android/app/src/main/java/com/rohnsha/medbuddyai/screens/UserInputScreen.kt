package com.rohnsha.medbuddyai.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ShortText
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.database.userdata.currentUser.currentUserDataVM
import com.rohnsha.medbuddyai.database.userdata.currentUser.fieldValueDC
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.ui.theme.customYellow
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAddComp(
    navController: NavHostController,
    currentUserDataVM: currentUserDataVM,
    snackBarToggleVM: snackBarToggleVM
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add Local Profile",
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

        val pfname= remember {
            mutableStateOf("")
        }
        val plname= remember {
            mutableStateOf("")
        }
        val scope= rememberCoroutineScope()

        Column(
            modifier = Modifier.padding(values)
        ) {
            Text(
                text = "Add linked local account. Automatically removed upon main account deletion.",
                fontFamily = fontFamily,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 24.dp),
                textAlign = TextAlign.Center,
                color = lightTextAccent
            )
            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(top = 30.dp, start = 24.dp, end = 24.dp)
            ){
                TextInputThemed(
                    value = pfname.value,
                    onValueChanged = { pfname.value= it },
                    label = "First Name",
                    icon = Icons.Outlined.ShortText,
                    onClose = { pfname.value = "" },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextInputThemed(
                    value = plname.value,
                    onValueChanged = { plname.value= it },
                    label = "Last Name",
                    icon = Icons.Outlined.ShortText,
                    onClose = { plname.value = "" },
                    singleLine = true
                )
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (pfname.value != "" && plname.value != "") {
                            scope.launch {
                                val data = fieldValueDC(
                                    fname = pfname.value,
                                    lname = plname.value,
                                    username = "addedPatient",
                                    isDefaultUser = false
                                )
                                currentUserDataVM.addDataCurrentUser(data)
                                navController.popBackStack()
                            }
                        } else {
                            snackBarToggleVM.SendToast(
                                message = "either of the field is empty",
                                indicator_color = customYellow,
                                padding = PaddingValues(2.dp),
                            )
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Save and Exit", color = Color.White, fontSize = 18.sp, fontFamily = fontFamily)
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}