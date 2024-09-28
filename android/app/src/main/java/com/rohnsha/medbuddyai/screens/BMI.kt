package com.rohnsha.medbuddyai.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.Transgender
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.database.userdata.keys.keyDC
import com.rohnsha.medbuddyai.database.userdata.keys.keyVM
import com.rohnsha.medbuddyai.ui.theme.BGMain
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.customRed
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.formAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BMIScreen(
    padding: PaddingValues,
    navController: NavHostController,
    keyVM: keyVM
) {

    val scope= rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "BMI Calculator",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 26.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BGMain
                )
            )
        },
        modifier = Modifier
            .fillMaxSize(),
        containerColor = BGMain
    ){ values ->
        Column(
            modifier = Modifier
                .padding(values)
                .padding(padding)
                .padding(top = 20.dp)
                .fillMaxSize()
                .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                .padding(top = 30.dp, start = 24.dp, end = 24.dp)
        ){
            val height= remember {
                mutableStateOf("")
            }
            val weight= remember {
                mutableStateOf("")
            }
            val age= remember {
                mutableStateOf("")
            }
            val genderColExpanded= remember {
                mutableStateOf(false)
            }
            val gender= remember {
                mutableIntStateOf(0)
            } // 1-> Male, 2-> Female

            Text(
                text = "Key Data",
                fontFamily = fontFamily,
                fontWeight = FontWeight(600),
                fontSize = 15.sp,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextInputThemed(value = height.value, onValueChanged = { height.value= it },
                icon = Icons.Outlined.Height, label = "Enter your height",
                onClose = { height.value= "" }, regex = "^(?:5[6-9]|[6-9]\\d|1\\d\\d|22[0-5])\$\n", suffix = "cm",
                //isNumKey = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextInputThemed(value = weight.value, onValueChanged = { weight.value= it },
                icon = Icons.Outlined.Scale, label = "Enter your weight",
                onClose = { weight.value= "" }, suffix = "kg", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), regex = "\\b(?:[2-9]|[1-9][0-9]|110)\\b\n"
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Get more insights (optional)",
                fontFamily = fontFamily,
                fontWeight = FontWeight(600),
                fontSize = 15.sp,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextInputThemed(
                value = age.value,
                onValueChanged = { age.value= it },
                icon = Icons.Outlined.Add,
                label = "Enter your age",
                onClose = { /*TODO*/ },
                //isNumKey = true,
                suffix = "years"
            )
            Spacer(modifier = Modifier.height(12.dp))
            ExposedDropdownMenuBox(
                expanded = genderColExpanded.value,
                onExpandedChange = { genderColExpanded.value = it },
            ) {
                TextInputThemed(
                    value = when(gender.intValue){
                        1 -> "Male"
                        2 -> "Female"
                        else -> ""
                    },
                    onValueChanged = {  },
                    icon = when(gender.intValue){
                        1 -> Icons.Outlined.Male
                        2 -> Icons.Outlined.Female
                        else -> Icons.Outlined.Transgender
                        },
                    label = "Select Gender",
                    onClose = {  },
                    //isNumKey = true,
                    readOnly = true,
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = genderColExpanded.value,
                    onDismissRequest = { genderColExpanded.value = false },
                    ) {
                    DropdownMenuItem(text = { Text(text = "Male") }, onClick = {
                        gender.intValue= 1
                        genderColExpanded.value = false
                    })
                    DropdownMenuItem(text = { Text(text = "Female") }, onClick = {
                        gender.intValue= 2
                        genderColExpanded.value = false
                    })
                }
            }
            Button(
                modifier = Modifier
                    .padding(top = 26.dp)
                    .align(Alignment.CenterHorizontally)
                ,
                onClick = {
                    scope.launch {

                        keyVM.updateKeySecretPair(
                            keyDC = listOf(
                                keyDC(
                                    serviceName = "swasthai",
                                    secretKey = ""
                                ),
                                keyDC(
                                    serviceName = "google",
                                    secretKey = ""
                                ),
                                keyDC(
                                    serviceName = "openai",
                                    secretKey = ""
                                ),
                                keyDC(
                                    serviceName = "anthropic",
                                    secretKey = ""
                                ),
                            )
                        ){}
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Analyze",
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600),
                    fontFamily = fontFamily
                )
            }
        }
    }
}

@Composable
fun TextInputThemed(
    value: String,
    onValueChanged: (String) -> Unit,
    icon: ImageVector? = null,
    label: String,
    onClose: () -> Unit,
    regex: String?=null,
    suffix: String? = null,
    readOnly: Boolean= false,
    modifier: Modifier= Modifier,
    modifier2: Modifier= Modifier,
    singleLine: Boolean= true,
    keyboardOptions: KeyboardOptions= KeyboardOptions(keyboardType = KeyboardType.Text),
    errorBool: Boolean= false,
    errorText: String= "This is a required filed",
    regexUnMatchErrorText: String= "Invalid input",
    isEnabled: Boolean= true
) {
    val errorState= remember {
        mutableStateOf(false)
    }
    if (regex!=null){
        errorState.value= !value.matches(Regex(regex)) && value!= ""
    }
    Column {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier2)
                .then(
                    if (readOnly) {
                        modifier
                    } else {
                        Modifier
                    }
                ),
            textStyle = LocalTextStyle.current.copy(fontFamily = fontFamily, fontSize = 18.sp),
            value = value,
            enabled = isEnabled,
            readOnly = readOnly,
            onValueChange = { onValueChanged(it) },
            suffix = { if (suffix!=null) Text(text = suffix) },
            keyboardOptions = keyboardOptions,
            trailingIcon = {
                if (value!="" && !readOnly && isEnabled){
                    Image(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .clickable {
                                onClose()
                            }
                            .padding(3.dp),
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close Icon"
                    )
                }
            },
            //placeholder = { Text(text = placeholder) },
            label = {
                Text(
                    text = label,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    color = formAccent
                )
            },
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
            singleLine = singleLine
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

