package com.rohnsha.medbuddyai.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Warning
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.database.appData.disease.diseaseDBviewModel
import com.rohnsha.medbuddyai.database.userdata.chatbot.chatDB_VM
import com.rohnsha.medbuddyai.database.userdata.chatbot.chats.chatEntity
import com.rohnsha.medbuddyai.database.userdata.currentUser.currentUserDataVM
import com.rohnsha.medbuddyai.database.userdata.scan_history.scanHistory
import com.rohnsha.medbuddyai.database.userdata.scan_history.scanHistoryViewModel
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.screens.scan.DataListFull
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.customYellow
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserStatScreen(
    userIndexx: Int,
    padding: PaddingValues,
    scanHistoryViewModel: scanHistoryViewModel,
    diseaseDBviewModel: diseaseDBviewModel,
    navController: NavHostController,
    chatdbVm: chatDB_VM,
    currentUserDataVM: currentUserDataVM,
    snackBarToggleVM: snackBarToggleVM,
    viewMode: Int // 0: both 1: scanHistory 2: chatHistory
) {
    val scope= rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Activity",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 26.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                actions = {
                    if (viewMode == 0 || viewMode == 1)
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (currentUserDataVM.isDefaultUser(userIndexx) || currentUserDataVM.userCount.value == 1) {
                                        snackBarToggleVM.SendToast(
                                            message = if (currentUserDataVM.userCount.value==1){
                                                "Cannot delete last user"
                                            } else "Cannot delete default User",
                                            indicator_color = customYellow,
                                            icon = Icons.Outlined.Warning
                                        )
                                    } else {
                                        currentUserDataVM.deleteUser(userIndexx)
                                        chatdbVm.deleteChats(userIndexx)
                                        scanHistoryViewModel.deleteScanHistory(userIndexx)
                                        navController.popBackStack()
                                    }
                                }
                            }) {
                            if (viewMode == 0) {
                                Icon(
                                    imageVector = Icons.Default.PersonRemove,
                                    contentDescription = "Delete Icon"
                                )
                            }
                        }
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
                }
            )
        },
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.tertiary
    ){ values ->

        val filteredScanHistory= remember {
            mutableStateListOf<scanHistory>()
        }
        if (viewMode == 0 || viewMode == 1){
            val history= scanHistoryViewModel.scanHistoryEntries.collectAsState().value.filter { it.userIndex == userIndexx }
            filteredScanHistory.clear()
            filteredScanHistory.addAll(history)
        }

        val filteredChatHistory= remember {
            mutableStateListOf<chatEntity>()
        }
        LaunchedEffect(key1 = Unit) {
            if (viewMode==0 || viewMode==2){
                val chatHistory= chatdbVm.readChatHistory().filter { it.userIndex == userIndexx }
                filteredChatHistory.clear()
                filteredChatHistory.addAll(chatHistory)
            }
        }

        LaunchedEffect(key1 = true) {
            if (viewMode==2 && userIndexx== Int.MAX_VALUE){
                chatdbVm.readChatHistory().let {
                    filteredChatHistory.clear()
                    filteredChatHistory.addAll(it)
                }
            }
        }

        if (viewMode==1){
            val history= scanHistoryViewModel.scanHistoryEntries.collectAsState().value
            history.let {
                filteredScanHistory.clear()
                filteredScanHistory.addAll(it)
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(values)
                .padding(padding)
                .padding(top = 20.dp)
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(top = 30.dp)
        ){
            if (filteredScanHistory.isNotEmpty()){
                item {
                    Text(
                        text = "Scan Reports",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 15.sp,
                        modifier = Modifier
                            .padding(bottom = 12.dp, start = 24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                items(if (viewMode==0) filteredScanHistory.take(5) else filteredScanHistory){ data ->
                    DataListFull(
                        title = data.title,
                        subtitle = data.timestamp.let {
                            val instant = Instant.ofEpochMilli(it)
                            val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                            val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
                            formatter.format(dateTime)
                        },
                        imageVector = Icons.Filled.History,
                        colorLogo = customBlue,
                        additionalDataColor = lightTextAccent,
                        colorLogoTint = Color.White,
                        onClickListener = {
                            diseaseDBviewModel.inputNameToSearch(data) {
                                navController.navigate(
                                    bottomNavItems.ScanResult.returnScanResIndex(
                                        1,
                                        9999
                                    )
                                )
                            }
                            Log.d("logStatus", "clicked")
                        }
                    )
                }
                if (filteredScanHistory.size>5 && viewMode==0){
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 21.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "View More",
                                fontFamily = fontFamily,
                                fontWeight = FontWeight(600),
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .padding(top = 14.dp)
                                    .clickable {
                                        navController.navigate(
                                            bottomNavItems.userStatScreen.returnUserIndexx(
                                                userIndex = userIndexx,
                                                viewModeInt = 1
                                            )
                                        )
                                    },
                                color = customBlue,
                            )
                        }
                    }
                }
            }
            if (filteredChatHistory.isNotEmpty()){
                item {
                    Text(
                        text = "Chat History",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 15.sp,
                        modifier = Modifier
                            .padding(top = 18.dp, start = 24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                items(if (viewMode==0) filteredChatHistory.take(5) else filteredChatHistory){
                    DataListFull(
                        title = when(it.mode){
                            0 -> "QnA"
                            1 -> "Symptom Checker"
                            else -> "Unknown"
                        },
                        colorLogo = customBlue,
                        subtitle = "Chat ID: ${it.id}",
                        imageVector = Icons.Outlined.History,
                        onClickListener = {
                            navController.navigate(bottomNavItems.Chatbot.returnChatID(chatMode = it.mode, chatID = it.id))
                        }
                    )
                }
            }
            if (filteredChatHistory.size>5 && viewMode==0){
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 21.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "View More",
                            fontFamily = fontFamily,
                            fontWeight = FontWeight(600),
                            fontSize = 15.sp,
                            modifier = Modifier
                                .padding(top = 14.dp)
                                .clickable {
                                    navController.navigate(
                                        bottomNavItems.userStatScreen.returnUserIndexx(
                                            userIndex = userIndexx,
                                            viewModeInt = 2
                                        )
                                    )
                                },
                            color = customBlue,
                        )
                    }
                }
            }
            if (filteredChatHistory.isEmpty() && filteredScanHistory.isEmpty()){
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 21.dp),
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = "No History",
                            fontFamily = fontFamily,
                            fontWeight = FontWeight(600),
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }

}