package com.rohnsha.medbuddyai.screens.scan

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.QuestionMark
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.database.appData.disease_questions.questionMsg
import com.rohnsha.medbuddyai.database.appData.disease_questions.questionVM
import com.rohnsha.medbuddyai.database.appData.disease_questions.questions
import com.rohnsha.medbuddyai.database.userdata.chatbot.messages.messageEntity
import com.rohnsha.medbuddyai.domain.dataclass.disease_data_dataClass
import com.rohnsha.medbuddyai.domain.dataclass.disease_version
import com.rohnsha.medbuddyai.domain.dataclass.moreActions
import com.rohnsha.medbuddyai.domain.viewmodels.photoCaptureViewModel
import com.rohnsha.medbuddyai.domain.viewmodels.sideStateVM
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.navigation.sidebar.screens.sideBarModifier
import com.rohnsha.medbuddyai.screens.Messages
import com.rohnsha.medbuddyai.screens.misc.LoadingLayout
import com.rohnsha.medbuddyai.screens.misc.NormalErrorStateLayout
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private lateinit var otherDiseaseData: List<disease_version>
private lateinit var disease_results: MutableState<disease_data_dataClass>

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQuestions(
    indexClassification: Int,
    photoCaptureViewModel: photoCaptureViewModel,
    sideStateVM: sideStateVM,
    padding: PaddingValues,
    navHostController: NavHostController,
    scanMode: Int
) {

    val context= LocalContext.current
    disease_results = remember {
        mutableStateOf(disease_data_dataClass())
    }
    val qList= remember {
        mutableListOf<questions>()
    }

    val index= remember {
        mutableIntStateOf(0)
    }
    val yesCount= remember {
        mutableIntStateOf(0)
    }

    val questionVM= viewModel<questionVM>()

    val isStillLoading = photoCaptureViewModel.isLoadingBoolean.collectAsState().value
    val isNormal = photoCaptureViewModel.isNormalBoolean.collectAsState().value
    val isErrored = photoCaptureViewModel.isErroredBoolean.collectAsState().value
    LaunchedEffect(key1 = true){
        delay(500L)
        photoCaptureViewModel.onClassify(context, index = indexClassification)
    }
    disease_results.value= photoCaptureViewModel.classificationData.collectAsState().value
    otherDiseaseData = photoCaptureViewModel.getDiseaseVersionData(group_number = indexClassification, isMaxIndex = false)

    Log.d("ScanQuestions", "scanMainItem: ${disease_results.value}")
    Log.d("ScanQuestions", "scanMainItem: $otherDiseaseData")

    if (disease_results.value.domain != ""){
        LaunchedEffect(key1 = true) {
            questionVM.getQuestions(
                domain = disease_results.value.domain.toLong(),
                index = disease_results.value.diseaseIndex.toLong()
            )
        }
    }
    val q= questionVM.questionList.collectAsState().value.take(10)
    Log.d("ScanQuestions", "q: $q")
    val questions= remember {
        mutableListOf<questionMsg>()
    }
    if (q.isNotEmpty()){
        LaunchedEffect(key1 = Unit) {
            questions.add(
                questionMsg(
                    questions = q[index.intValue],
                    isBotMessage = true
                )
            )
            index.intValue++
        }
    }

    Log.d("ScanQuestions", "scanMainQuwstion: ${qList}")

    Log.d("ScanQuestionsDebug", "scanMainQuwstion: ${questions}")
    Log.d("ScanQuestionsDebug", "scanMainQuwstion: ${q}")

    val listGreet= listOf(
        moreActions("Yes, I have symptoms") {
            questions.add(
                questionMsg(
                    questions = questions(
                        domain = Int.MAX_VALUE.toLong(),
                        index = Int.MAX_VALUE.toLong(),
                        question = "Yes"
                    ),
                    isBotMessage = false
                )
            )
            yesCount.intValue++
        },
        moreActions("No") {
            questions.add(
                questionMsg(
                    questions = questions(
                        domain = Int.MAX_VALUE.toLong(),
                        index = Int.MAX_VALUE.toLong(),
                        question = "No"
                    ),
                    isBotMessage = false
                )
            )
        },
    )

    Log.d("ScanQuestions", "scanMainQuwstionYESCount: ${yesCount.intValue}, index: ${index.intValue}")

    val scrollState= rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = questions.size) {
        if (questions.isNotEmpty()){
            scrollState.animateScrollToItem(questions.size-1)
        }
    }

    Scaffold(
        topBar = {
            if (!isStillLoading && !isErrored && !isNormal){
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Self Declaration",
                            fontFamily = fontFamily,
                            fontWeight = FontWeight(600),
                            fontSize = 26.sp
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    actions = {
                        IconButton(onClick = {
                            navHostController.navigate(
                                bottomNavItems.documentations.returnDoc(
                                    1
                                )
                            )}) {
                            Icon(
                                imageVector = Icons.Outlined.QuestionMark,
                                contentDescription = "Documentations button"
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navHostController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back Icon"
                            )
                        }
                    }
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .then(sideBarModifier(sideStateVM = sideStateVM)),
        containerColor = MaterialTheme.colorScheme.tertiary
    ){value->
        if (!isStillLoading){
            if (isNormal){
                NormalErrorStateLayout(state = 0)
                Log.d("loggingStatus", "normal")
            } else if (isErrored){
                NormalErrorStateLayout(state = 1)
                Log.d("loggingStatus", "errored")
            } else{
                Column(
                    modifier = Modifier
                        .padding(value)
                        .padding(padding)
                        .padding(top = 20.dp)
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp))
                        .padding(top = 26.dp, start = 24.dp, end = 24.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f),
                        state = scrollState
                    ) {
                        items(questions){
                            if (it.isErrored){
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center){
                                    Text(
                                        text = it.questions.question,
                                        color = lightTextAccent,
                                        fontFamily = fontFamily,
                                        fontSize = 10.sp,
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            } else {
                                Messages(
                                    messageInfo = messageEntity(
                                        message = it.questions.question,
                                        isError =  it.isErrored,
                                        isBotMessage = it.isBotMessage,
                                        chatId = Int.MAX_VALUE,
                                        timestamp = System.currentTimeMillis()
                                    ))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (q.size > (questions.size/2) && q.isNotEmpty()){
                        LazyRow(
                            Modifier.offset(x= (-4).dp)
                        ) {
                            item { Spacer(modifier = Modifier.width(6.dp)) }
                            items(listGreet){
                                Text(
                                    text = it.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight(600),
                                    fontFamily = fontFamily,
                                    color = customBlue,
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp, vertical = 10.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .clickable {
                                            Log.d("ScanQuestions", "scanMainQuwstion: clicked")
                                            it.onClick()
                                            Log.d(
                                                "ScanQuestions",
                                                "scanMainQuwstionBoolean: ${q.size != (index.intValue - 1)}, index: ${index.intValue}, q.size: ${q.size}"
                                            )
                                            questions.add(
                                                questionMsg(
                                                    questions = q[index.intValue],
                                                    isBotMessage = true
                                                )
                                            )
                                            if (q.size != (index.intValue + 1)) {
                                                Log.d("ScanQuestions", "scanMainQuwstion: emterted")
                                                index.intValue++
                                            } else {
                                                questions.add(
                                                    questionMsg(
                                                        questions = questions(
                                                            domain = Int.MAX_VALUE.toLong(),
                                                            index = Int.MAX_VALUE.toLong(),
                                                            question = "You are done with the self declaration. Navigating to main menu"
                                                        ),
                                                        isBotMessage = true,
                                                        isErrored = true
                                                    )
                                                )
                                                scope.launch {
                                                    delay(3000L)
                                                    Log.d(
                                                        "ScanQuestions",
                                                        "scanMainQuwstion: navigation initiated"
                                                    )
                                                    Log.d(
                                                        "percecntagesScan",
                                                        "scanMainQuwstion: ${(yesCount.intValue / index.intValue.toFloat()) * 100}"
                                                    )
                                                    photoCaptureViewModel.updateConfidence((yesCount.intValue / (index.intValue + 1).toFloat()) * 100)
                                                    navHostController.navigate(
                                                        bottomNavItems.ScanResult.returnScanResIndex(
                                                            level = 0,
                                                            index = indexClassification,
                                                            mode = scanMode
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        .padding(vertical = 3.dp, horizontal = 14.dp)
                                )
                            }
                        }
                    }
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