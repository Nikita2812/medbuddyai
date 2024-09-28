package com.rohnsha.medbuddyai.screens.preferences

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Check
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.database.userdata.keys.keyDC
import com.rohnsha.medbuddyai.database.userdata.keys.keyVM
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.screens.TextInputThemed
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.customGreen
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun APIScreen(
    keyVM: keyVM,
    navController: NavHostController,
    snackBarToggleVM: snackBarToggleVM
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "API Secrets",
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

        val keySecrets= remember {
            mutableStateListOf<keyDC>()
        }

        LaunchedEffect(Unit) {
            keyVM.getKeySecretPairs().forEach { keySecrets.add(it) }
        }

        Log.d("keys", keySecrets.find { it.serviceName == "google" }?.secretKey ?: "")

        val swasthai= remember {
            mutableStateOf("")
        }
        val google = remember {
            mutableStateOf(keySecrets.find { it.serviceName == "google" }?.secretKey ?: "")
        }
        val openai = remember {
            mutableStateOf("")
        }
        val anthropic = remember {
            mutableStateOf("")
        }

        LaunchedEffect(key1 = true) {
            keyVM.getKeySecretPairs().let { pairs->
                keySecrets.addAll(pairs)
                swasthai.value= pairs.find { it.serviceName == "swasthai" }?.secretKey ?: ""
                google.value= pairs.find { it.serviceName == "google" }?.secretKey ?: ""
                openai.value= pairs.find { it.serviceName == "openai" }?.secretKey ?: ""
                anthropic.value= pairs.find { it.serviceName == "anthropic" }?.secretKey ?: ""
            }
        }
        val scope= rememberCoroutineScope()

        Column(
            modifier = Modifier.padding(values)
        ) {
            Text(
                text = "We do not sync these keys to the cloud and are stored privately and locally.",
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
                    value = swasthai.value,
                    onValueChanged = { swasthai.value = it },
                    label = "SwasthAI API Key",
                    onClose = { swasthai.value = "" },
                    isEnabled = false,
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextInputThemed(
                    value = google.value,
                    onValueChanged = { google.value = it },
                    label = "Google API Key",
                    onClose = { google.value = "" }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextInputThemed(
                    value = openai.value,
                    onValueChanged = { openai.value = it },
                    label = "OpenAI API Key",
                    onClose = { openai.value = "" }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextInputThemed(
                    value = anthropic.value,
                    onValueChanged = { anthropic.value = it },
                    label = "Anthropic API Key",
                    onClose = { anthropic.value = "" }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = customBlue
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        scope.launch {
                            keyVM.updateKeySecretPair(
                                keyDC = listOf(
                                    keyDC(
                                        serviceName = "swasthai",
                                        secretKey = swasthai.value
                                    ),
                                    keyDC(
                                        serviceName = "google",
                                        secretKey = google.value
                                    ),
                                    keyDC(
                                        serviceName = "openai",
                                        secretKey = openai.value
                                    ),
                                    keyDC(
                                        serviceName = "anthropic",
                                        secretKey = anthropic.value
                                    )
                                ),
                                onCompleteLambda = {
                                    snackBarToggleVM.SendToast(
                                        message = "API Keys Updated successfully",
                                        indicator_color = customGreen,
                                        icon = Icons.Outlined.Check
                                    )
                                }
                            )
                        }
                }) {
                    Text(text = "Save Changes")
                }
            }
        }
    }

}