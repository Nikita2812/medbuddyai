package com.rohnsha.medbuddyai.screens.scan

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.AltRoute
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ConnectWithoutContact
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.JoinLeft
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.rohnsha.medbuddyai.database.appData.disease.diseaseDBviewModel
import com.rohnsha.medbuddyai.database.userdata.chatbot.chatDB_VM
import com.rohnsha.medbuddyai.database.userdata.chatbot.messages.messageEntity
import com.rohnsha.medbuddyai.database.userdata.currentUser.currentUserDataVM
import com.rohnsha.medbuddyai.database.userdata.keys.keyDC
import com.rohnsha.medbuddyai.database.userdata.keys.keyVM
import com.rohnsha.medbuddyai.database.userdata.scan_history.scanHistory
import com.rohnsha.medbuddyai.database.userdata.scan_history.scanHistoryViewModel
import com.rohnsha.medbuddyai.domain.dataclass.disease_data_dataClass
import com.rohnsha.medbuddyai.domain.dataclass.disease_version
import com.rohnsha.medbuddyai.domain.dataclass.rbStructure
import com.rohnsha.medbuddyai.domain.viewmodels.chatVM
import com.rohnsha.medbuddyai.domain.viewmodels.communityVM
import com.rohnsha.medbuddyai.domain.viewmodels.photoCaptureViewModel
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.domain.viewmodels.userAuthVM
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.screens.TextInputThemed
import com.rohnsha.medbuddyai.screens.misc.LoadingLayout
import com.rohnsha.medbuddyai.screens.misc.NormalErrorStateLayout
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.customGreen
import com.rohnsha.medbuddyai.ui.theme.customRed
import com.rohnsha.medbuddyai.ui.theme.customYellow
import com.rohnsha.medbuddyai.ui.theme.dashBG
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

private lateinit var disease_results: MutableState<disease_data_dataClass>
private lateinit var verificationResults: MutableState<rbStructure>
private lateinit var photoCaptureViewModel: photoCaptureViewModel
private lateinit var diseaseDBvm: diseaseDBviewModel
private lateinit var scanHistoryVM: scanHistoryViewModel
private lateinit var otherDiseaseData: List<disease_version>
private lateinit var modalState : MutableState<Boolean>
private var isStillLoading by Delegates.notNull<Boolean>()
private var isNormal by Delegates.notNull<Boolean>()
private var isErrored by Delegates.notNull<Boolean>()
private var mode by Delegates.notNull<Int>()
private lateinit var snackbarControl: snackBarToggleVM
private lateinit var communityVModel: communityVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultScreen(
    padding: PaddingValues,
    viewModel: photoCaptureViewModel,
    navController: NavHostController,
    scanHistoryViewModel: scanHistoryViewModel,
    resultsLevel: Int=1, // 0-> Scan, 1-> Scan History, 2-> Read Only,
    diseaseDBviewModel: diseaseDBviewModel,
    indexClassification: Int, // model to be triggered
    snackbarHostState: snackBarToggleVM,
    communityVM: communityVM,
    chatdbVm: chatDB_VM,
    chatVM: chatVM,
    currentUserDataVM: currentUserDataVM,
    keyVM: keyVM,
    scanMode: Int, // 0 -> normal scan, 1 -> disease_catalogue scan
    userAuthVM: userAuthVM
) {
    Log.d("scanMode", scanMode.toString())

    photoCaptureViewModel = viewModel
    diseaseDBvm = diseaseDBviewModel
    snackbarControl = snackbarHostState
    communityVModel = communityVM
    mode = resultsLevel
    scanHistoryVM = scanHistoryViewModel
    val context= LocalContext.current
    disease_results = remember {
        mutableStateOf(disease_data_dataClass())
    }
    when(resultsLevel){
        0 -> {
            BackHandler {
                when(scanMode){
                    1 -> {
                        navController.popBackStack(
                            bottomNavItems.ScanResult.returnScanResIndex(
                                2,
                                9999,
                                1
                            ),
                            inclusive = true
                        )
                    }
                    else -> {
                        navController.popBackStack(bottomNavItems.ScanQA.returnScanIndex(indexClassification, ), inclusive = true)
                    }
                }
                photoCaptureViewModel.flushValues()
            }
            isStillLoading = photoCaptureViewModel.isLoadingBoolean.collectAsState().value
            isNormal = photoCaptureViewModel.isNormalBoolean.collectAsState().value
            isErrored = photoCaptureViewModel.isErroredBoolean.collectAsState().value
            LaunchedEffect(key1 = true){
                delay(500L)
                //photoCaptureViewModel.onClassify(context, index = indexClassification)
            }
            disease_results.value= photoCaptureViewModel.classificationData.collectAsState().value
            otherDiseaseData = photoCaptureViewModel.getDiseaseVersionData(group_number = indexClassification, isMaxIndex = false)
        }
        1 -> {
            isStillLoading = diseaseDBviewModel.isLoadingBoolean.collectAsState().value
            isNormal =false
            isErrored = diseaseDBviewModel.isErroredBoolean.collectAsState().value
            LaunchedEffect(key1 = Unit){
                delay(500L)
                diseaseDBviewModel.searchByName()
            }
            disease_results.value= diseaseDBviewModel.data.collectAsState().value
            otherDiseaseData = listOf(disease_version("", "", 0f, 01.0))
        }
        2 -> {
            isStillLoading = diseaseDBviewModel.isLoadingBoolean.collectAsState().value
            isNormal =false
            isErrored = diseaseDBviewModel.isErroredBoolean.collectAsState().value
            LaunchedEffect(key1 = Unit){
                delay(500L)
                diseaseDBviewModel.retrieveData()
            }
            disease_results.value= diseaseDBviewModel.data.collectAsState().value
            otherDiseaseData = listOf(disease_version("", "", 0f, 01.0))
        }
    }

    Log.d("selectedDep", indexClassification.toString())
    modalState = rememberSaveable {
        mutableStateOf(false)
    }
    val infoStateOpen = remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            if (!isStillLoading && !isErrored && !isNormal){
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = disease_results.value.disease_name,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight(600),
                            fontSize = 26.sp,
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(bottomNavItems.documentations.returnDoc(2))
                        }) {
                            Icon(
                                imageVector = Icons.Default.QuestionMark,
                                contentDescription = "Documentations Icon"
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            when(scanMode){
                                1 -> {
                                    navController.popBackStack(
                                        bottomNavItems.ScanResult.returnScanResIndex(
                                            2,
                                            9999,
                                            1
                                        ),
                                        inclusive = true
                                    )
                                }
                                else -> {
                                    navController.popBackStack(bottomNavItems.ScanQA.returnScanIndex(indexClassification, ), inclusive = true)
                                }
                            }
                            photoCaptureViewModel.flushValues()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back Icon"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                )
            }
        },
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.tertiary
    ) { values ->

        val rbList= remember {
            mutableStateListOf<rbStructure>()
        }
        verificationResults= remember {
            mutableStateOf(rbStructure(false, ""))
        }

        LaunchedEffect(key1 = true) {
            val keys= keyVM.getKeySecretPairs().filter { it.secretKey != ""  && it.serviceName != "swasthai" }
            keys.forEach {
                rbList.add(
                    rbStructure(
                        isChecked = false,
                        title = it.serviceName
                    )
                )
            }
        }

        val scope= rememberCoroutineScope()
        if (!isStillLoading){
            if (isNormal){
                NormalErrorStateLayout(state = 0)
                Log.d("loggingStatus", "normal")
            } else if (isErrored){
                NormalErrorStateLayout(state = 1)
                Log.d("loggingStatus", "errored")
            } else {
                ScanResultsSuccess(
                    padding = padding,
                    values = values,
                    indexClassification = indexClassification,
                    navController = navController,
                    currentUserDataVM = currentUserDataVM,
                    chatdbVm = chatdbVm,
                    chatVM = chatVM,
                    snackbarHostState = snackbarHostState,
                    userAuthVM = userAuthVM,
                    keyVM = keyVM
                )
                if (modalState.value && rbList.isNotEmpty()){
                    ModalBottomSheet(
                        onDismissRequest = {
                            modalState.value=false
                        },
                    ) {
                        BOMContent(
                            rbList = rbList,
                            resultsRB = {
                                verificationResults.value= it
                            },
                            coroutineScope=scope,
                            buttonClicked = {
                                modalState.value= false
                            },
                            keyVM = keyVM
                        )
                    }
                } else if (rbList.isEmpty() && modalState.value){
                    snackbarHostState.SendToast(
                        message = "Add API secrets to enable verification",
                        indicator_color = customRed,
                        icon = Icons.Outlined.Warning
                    )
                    modalState.value= false
                }
            }
        } else {
            LoadingLayout(
                titleList = listOf(
                    "Charting a Course to Health Discovery", "Preparing to Enlighten and Empower",
                    "Loading the Canvas of Your Health Journey", "Awaiting Your Arrival in the Health Universe",
                    "Elevating Your Health IQ One Byte at a Time", "Stepping into the Digital Library of Wellness",
                    "Welcome to the Wonderland of Health Wisdom", "Fueling Curiosity for a Healthier Tomorrow"
            ))
        }
    }
}

@Composable
fun ScanResultsSuccess(
    padding: PaddingValues,
    values: PaddingValues,
    indexClassification: Int,
    navController: NavHostController,
    currentUserDataVM: currentUserDataVM,
    chatdbVm: chatDB_VM,
    chatVM: chatVM,
    snackbarHostState: snackBarToggleVM,
    userAuthVM: userAuthVM,
    keyVM: keyVM
) {
    val confidence= photoCaptureViewModel.maxIndex.collectAsState().value.confident
    val isSuccesfful= remember {
        mutableStateOf(false)
    }
    if (mode ==0){
        val defaultUser= currentUserDataVM.defaultUserIndex.collectAsState().value
        LaunchedEffect(key1 = disease_results.value){
            isSuccesfful.value= scanHistoryVM.addScanHistory(scanHistory(
                timestamp = System.currentTimeMillis(),
                title = disease_results.value.disease_name,
                domain = disease_results.value.domain,
                confidence = confidence,
                userIndex =defaultUser
            ))
        }
    }
    Log.d("isSuccessfull", isSuccesfful.value.toString())
    val optionTxtFieldState= remember {
        mutableStateOf(Int.MAX_VALUE)
    }
    LazyColumn(
        modifier = Modifier
            .padding(values)
            .padding(padding)
    ){
        item {
            Column(
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, top = 12.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment= Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                ){
                    val color= remember {
                        mutableStateOf(Color.Transparent)
                    }
                    val painter= rememberAsyncImagePainter(
                        model = disease_results.value.cover_link,
                        onError = {
                            color.value= customRed.copy(alpha = .25f)
                            Log.e("coil", "error response: ${it.painter}, ${it.result}")
                        },
                    )
                    Image(
                        painter = painter,
                        contentScale = ContentScale.Crop,
                        contentDescription = "null",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.value)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (mode ==0){
                    ClassificationConf(indexClassification = indexClassification)
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            ) {
                Spacer(
                    modifier = Modifier.height(24.dp)
                )
                OptionScanResults(
                    navController = navController,
                    flag = {
                        optionTxtFieldState.value= it
                    },
                    indexClassification = indexClassification,
                    userAuthVM = userAuthVM,
                    keyVM = keyVM,
                    snackbarHostState = snackbarHostState
                )
                ScanResultActions(
                    optionTxtFieldState = optionTxtFieldState,
                    scanHistoryVM = scanHistoryVM,
                    chatdbVm = chatdbVm,
                    insertionSuccessToDB = isSuccesfful.value,
                    chatVM = chatVM, navController = navController
                )
                Spacer(
                    modifier = Modifier.height(30.dp)
                )
                if (verificationResults.value.isChecked){
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.CenterStart
                    ){
                        Text(
                            text = verificationResults.value.title,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(horizontal = 25.dp),
                            textAlign = TextAlign.Start
                        )
                    }
                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                }
                DataBox(
                    title = "Know About Disease",
                    data = disease_results.value.introduction
                )
                Spacer(
                    modifier = Modifier.height(20.dp)
                )
                DataBox(
                    title = "Symptoms",
                    data = disease_results.value.symptoms
                )
                Spacer(
                    modifier = Modifier.height(20.dp)
                )
                DataBox(
                    title = "When to see doctor",
                    data = disease_results.value.thresholds
                )
            }
        }
        if (otherDiseaseData.isNotEmpty()){
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface)
                ){
                    Spacer(modifier = Modifier.height(20.dp))
                    if (mode==0 && otherDiseaseData.isNotEmpty()){
                        Text(
                            text = "Other Possible Outcomes",
                            fontFamily = fontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight(600),
                            modifier = Modifier
                                .padding(start = 25.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(13.dp))
                    }
                }
            }
            if (mode ==0){
                items(otherDiseaseData){ data ->
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        DataListFull(
                            title =data.disease_name,
                            subtitle =data.version,
                            data ="X + ${String.format("%.2f", data.confidence)}%",
                            imageVector = Icons.Outlined.JoinLeft,
                            colorLogo = dashBG,
                            colorLogoTint = Color.White
                        )
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                if (otherDiseaseData.isNotEmpty()){
                    Spacer(modifier = Modifier.height(13.dp))
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                Text(
                    text = "View Model Parameters",
                    fontFamily = fontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight(600),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(bottom = 18.dp)
                        .clickable {
                            snackbarHostState.SendToast(
                                message = "No data available",
                                indicator_color = customYellow,
                                icon = Icons.Outlined.Engineering
                            )
                        }
                )
            }
        }
    }
}

@Composable
fun ClassificationConf(indexClassification: Int) {
    val modelData= photoCaptureViewModel.getDiseaseVersionData(
        isMaxIndex = true, group_number = indexClassification,
    )
    Log.d("modelAccuracy", modelData[0].toString())
    Row(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier
                .padding(start = 13.dp)
        ) {
            Text(
                modifier = Modifier,
                text = "${String.format("%.2f", modelData[0].modelAccuracy)}%",
                fontSize = 14.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight(600),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                modifier = Modifier
                    .offset(y = (-2).dp),
                text = "Accuracy",
                fontSize = 14.sp,
                fontFamily = fontFamily,
                color = lightTextAccent
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier,
                text = modelData[0].version,
                fontSize = 14.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight(600),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                modifier = Modifier
                    .offset(y = (-2).dp),
                text = "Version",
                fontSize = 14.sp,
                fontFamily = fontFamily,
                color = lightTextAccent
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .padding(end = 18.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                modifier = Modifier,
                text = "${String.format("%.2f", modelData[0].confidence)}%",
                fontSize = 14.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight(600),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                modifier = Modifier
                    .offset(y = (-2).dp),
                text = "Matched",
                fontSize = 14.sp,
                fontFamily = fontFamily,
                color = lightTextAccent,
                //fontWeight = FontWeight(600)
            )
        }
    }
}

@Composable
fun DataListFull(
    title: String,
    subtitle: String?=null,
    data: String? = null,
    additionData: String? =null,
    imageVector: ImageVector ?= null,
    iconText: String?= null,
    colorLogo: Color,
    actionIcon: ImageVector= Icons.Outlined.ArrowForward,
    additionalDataColor: Color?= null,
    colorLogoTint: Color? = null,
    onClickListener: (() -> Unit)? =null,
) {
    Box(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .height(60.dp)
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
            .then(
                if (onClickListener != null) Modifier.clickable { onClickListener() } else Modifier
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageVector != null) {
                Image(
                    modifier = Modifier
                        .padding(start = 13.dp)
                        .size(34.dp)
                        .background(colorLogo, CircleShape)
                        .padding(6.dp),
                    imageVector = imageVector,
                    contentDescription = "$imageVector icon",
                    colorFilter = ColorFilter.tint(colorLogoTint ?: MaterialTheme.colorScheme.surface)
                )
            } else {
                Box(
                    modifier = Modifier
                        .padding(start = 13.dp)
                        .size(34.dp)
                        .background(colorLogo, CircleShape)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ){
                    if (iconText != null) {
                        Text(
                            text = iconText,
                            fontSize = 12.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight(600),
                            color = colorLogoTint?: MaterialTheme.colorScheme.surface,
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(start = 13.dp)
            ) {
                Text(
                    modifier = Modifier,
                    text = title,
                    fontSize = 14.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                if (subtitle!=null){
                    Text(
                        modifier = Modifier
                            .offset(y = (-2).dp),
                        text = subtitle,
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        color = lightTextAccent
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 18.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (data != null) {
                    Text(
                        modifier = Modifier,
                        text = data,
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                additionData?.let {
                    Text(
                        modifier = Modifier
                            .offset(y = (-2).dp),
                        text = it,
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        color = additionalDataColor ?: colorLogo,
                        fontWeight = FontWeight(600)
                    )
                }
                if (additionData==null && data==null){
                    Image(
                        imageVector = actionIcon,
                        contentDescription = "front icon",
                        colorFilter = ColorFilter.tint(additionalDataColor ?: lightTextAccent),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BOMContent(
    rbList: SnapshotStateList<rbStructure>,
    coroutineScope: CoroutineScope,
    buttonClicked: () -> Unit,
    keyVM: keyVM,
    resultsRB: (rbStructure) -> Unit,
) {

    val list= remember {
        rbList.toList().toMutableStateList()
    }

    val currentlySelected= remember {
        mutableStateOf("")
    }

    val currentlySelectedKeyPairs= remember {
        mutableStateOf(keyDC("",""))
    }

    LaunchedEffect(key1 = currentlySelected.value) {
        currentlySelectedKeyPairs.value= keyVM.getSecretKey(serviceName = currentlySelected.value)
    }

    Log.d("bom", currentlySelected.value.toString())

    val isProcessing= remember {
        mutableStateOf(false)
    }
    val context= LocalContext.current

    LazyColumn(
        modifier = Modifier
            .padding(start = 24.dp, bottom = 30.dp, end = 24.dp)
    ) {
        item {
            Row {
                Text(
                    text = "Remote Models",
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600),
                    fontFamily = fontFamily
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .animateContentSize(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = LinearOutSlowInEasing
                            )
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Done",
                        fontSize = 18.sp,
                        fontWeight = FontWeight(600),
                        fontFamily = fontFamily,
                        color = customBlue,
                        modifier = Modifier.clickable {
                            isProcessing.value= true
                            coroutineScope.launch {
                                photoCaptureViewModel.verifyFromRemote(
                                    keyPair = currentlySelectedKeyPairs.value,
                                    disease_name = disease_results.value.disease_name,
                                    isSuccessfulListerner = { successBool, response ->
                                        isProcessing.value= false
                                        if (successBool){
                                            buttonClicked()
                                            resultsRB(
                                                rbStructure(
                                                    isChecked = true,
                                                    title = if (response.isMatched){
                                                        "Validations found matching results (${response.confidence*100}% confidence)"
                                                    } else {
                                                        "Validations found contradictory results (${response.confidence*100}% confidence)"
                                                    }
                                                )
                                            )
                                        } else{
                                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    )
                    if (isProcessing.value){
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .size(16.dp),
                            color = lightTextAccent,
                            strokeWidth = 1.5.dp
                        )
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(18.dp))
        }
        items(list){ data ->
            if (data.isChecked){
                currentlySelected.value= data.title
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .pointerInput(key1 = true) {
                        detectTapGestures {
                            list.replaceAll {
                                it.copy(
                                    isChecked = (it.title==data.title)
                                )
                            }
                        }},
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = Icons.Outlined.MedicalInformation,
                    contentDescription = "options pre",
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp))
                        .padding(10.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                )
                Spacer(modifier = Modifier.width(18.dp))
                Text(
                    text = data.title,
                    fontFamily = fontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .then(
                            if (!data.isChecked) Modifier.border(
                                width = 1.dp,
                                color = Color(0xFFD4D4D4),
                                shape = CircleShape
                            ) else Modifier
                        )
                ){
                    if (data.isChecked){
                        Image(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "options",
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.onPrimary, CircleShape)
                                .padding(3.dp),
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.surface)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
        item { Spacer(modifier = Modifier.height(47.dp)) }
    }
}

@Composable
fun OptionScanResults(
    flag: (Int) -> Unit,
    navController: NavHostController,
    indexClassification: Int,
    userAuthVM: userAuthVM,
    keyVM: keyVM,
    snackbarHostState: snackBarToggleVM
) {
    var userUnAuthenticatedBom by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(true) {
        val services= keyVM.get3rdPartyAPICount()
        userUnAuthenticatedBom= userAuthVM.isUserUnAuthenticated() && services==0
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OptionsScanResultUNI(
            title = "Ask mAI",
            icon = Icons.Outlined.QuestionAnswer,
            onClickListener = {
                if (userUnAuthenticatedBom) {
                    snackbarHostState.SendToast(
                        message = "Please login or add APIs to continue",
                        indicator_color = customRed,
                        icon = Icons.Outlined.Login
                    )
                } else flag(0)
            }
        )
        OptionsScanResultUNI(
            title = "Compose",
            icon = Icons.Outlined.ConnectWithoutContact,
            onClickListener = {
                if (userAuthVM.isUserUnAuthenticated()){
                    snackbarHostState.SendToast(
                        message = "Please login to continue",
                        indicator_color = customRed,
                        icon = Icons.AutoMirrored.Outlined.Login
                    )
                } else flag(1)
            }
        )
        OptionsScanResultUNI(
            title = "Doctors",
            icon = Icons.Outlined.VolunteerActivism,
            onClickListener = {
                navController.navigate(
                    bottomNavItems.DoctorScreen.returnDomainID(
                        disease_results.value.domain.toInt()
                    )) }
        )
        OptionsScanResultUNI(
            title = when(mode){
                0 -> { "Verify" }
                1 -> {"Rescan"}
                else -> { "Scan" }
            },
            icon = when(mode){
                0 -> { Icons.Outlined.AltRoute }
                else -> { Icons.Outlined.Camera }
            },
            onClickListener = {
                when(mode){
                    0 -> {
                        modalState.value= true
                    }
                    2 -> {
                        navController.navigate(bottomNavItems.Scan.returnScanIndex(disease_results.value.domain.toInt(), mode = 1))
                    }
                    else -> {
                        navController.navigate(bottomNavItems.Scan.returnScanIndex(disease_results.value.domain.toInt()))
                    }
                }
            }
        )
    }
}

@Composable
fun OptionsScanResultUNI(
    title: String,
    icon:ImageVector,
    onClickListener: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(49.dp)
                .height(49.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))
                .clickable(
                    onClick = onClickListener
                ),
            contentAlignment = Alignment.Center
        ){
            Image(
                imageVector =icon,
                contentDescription = null,
                modifier = Modifier
                    .height(24.dp)
                    .width(24.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun DataBox(
    title: String,
    data: String
) {
    Column {
        Text(
            text = title,
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight(600),
            modifier = Modifier
                .padding(start = 25.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(
            modifier = Modifier.height(14.dp)
        )
        Text(
            text = data.replace("new_line", "\n"),
            color = MaterialTheme.colorScheme.onPrimary,
            fontFamily = fontFamily,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(horizontal = 25.dp),
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun ScanResultActions(
    optionTxtFieldState: MutableState<Int>,
    scanHistoryVM: scanHistoryViewModel,
    insertionSuccessToDB: Boolean,
    chatdbVm: chatDB_VM,
    chatVM: chatVM,
    navController: NavHostController
) {
    val textData= remember {
        mutableStateOf("")
    }
    val scan_historyData= remember {
        mutableStateOf(
            scanHistory(
                timestamp = 0L,
                title = "",
                domain = 0.toString(),
                confidence = 0f,
                userIndex = 0
            )
        )
    }
    val messageChat= remember {
        mutableStateOf(
            messageEntity(
                message = "",
                timestamp = 0L,
                chatId = Int.MAX_VALUE,
                isBotMessage = false,
                isError = false
            )
        )
    }
    Log.d("optionTxtFieldState", "initialMessge: ${messageChat.value}")
    if (optionTxtFieldState.value==0 && insertionSuccessToDB){
        LaunchedEffect(key1 = true) {
            scan_historyData.value= scanHistoryVM.getRecentScan()
            val chatID= chatdbVm.getChatCounts() + 1
            messageChat.value= messageChat.value.copy(
                chatId = chatID,
                timestamp = scan_historyData.value.timestamp,
                message = textData.value
            )
        }
        Log.d("optionTxtFieldState", "lastScan: ${scan_historyData.value}")
        Log.d("optionTxtFieldState", "updatedMessge: ${messageChat.value}")
    }
    AnimatedVisibility(visible = optionTxtFieldState.value!=Int.MAX_VALUE) {
        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 25.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 3.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                .padding(12.dp)
        ){
            //Spacer(modifier = Modifier.height(14.dp))
            TextInputThemed(
                value = textData.value,
                onValueChanged = { textData.value= it },
                label = "Enter the value",
                onClose = { textData.value= "" }
            )
            Text(
                text = when (optionTxtFieldState.value){
                    0 -> {
                        "Report is attached to chat for reference"
                    }
                    1 ->{
                        "Report is not attached to posts for user privacy"
                    }
                    else -> {
                        ""
                    }
                },
                color = lightTextAccent,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    when(optionTxtFieldState.value){
                        0 -> {
                            chatVM.importChatWithAttachment(
                                messageEntity = messageChat.value.copy(
                                    message = textData.value
                                ),
                                lastScanData = scan_historyData.value
                            )
                            navController.navigate(
                                bottomNavItems.Chatbot.returnChatID(
                                    chatID = messageChat.value.chatId,
                                    chatMode = 2
                                )
                            )
                            Log.d("optionTxtFieldState", "finalMSG: ${messageChat.value}")
                        }
                        1 -> {
                            communityVModel.post(
                                content = textData.value,
                                onCompleteLambda = {
                                    snackbarControl.SendToast(
                                        message = "Successfully posted",
                                        indicator_color = customGreen,
                                        padding = PaddingValues(),
                                        icon = Icons.Outlined.Check
                                    )
                                }
                            )
                            textData.value= ""
                            optionTxtFieldState.value = Int.MAX_VALUE
                        }
                    }
                          },
                colors = ButtonDefaults.buttonColors(containerColor = customBlue, contentColor = Color.White),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = when(optionTxtFieldState.value){
                    0 -> "Start chat"
                    1 -> "Post on community"
                    else -> "Submit"
                })
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}