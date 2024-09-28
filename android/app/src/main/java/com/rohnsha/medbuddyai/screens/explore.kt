package com.rohnsha.medbuddyai.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HistoryToggleOff
import androidx.compose.material.icons.outlined.BrunchDining
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.database.appData.disease.diseaseDBviewModel
import com.rohnsha.medbuddyai.database.userdata.scan_history.scanHistoryViewModel
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.screens.scan.DataListFull
import com.rohnsha.medbuddyai.ui.theme.BGMain
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    padding: PaddingValues,
    navController: NavHostController,
    scanHistoryViewModel: scanHistoryViewModel,
    diseaseDBviewModel: diseaseDBviewModel
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Explore",
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
    ) { values ->
        val lastScans= scanHistoryViewModel.scanHistoryEntries.collectAsState().value

        LazyColumn(
            modifier = Modifier
                .padding(values)
                .padding(padding)
                .padding(top = 20.dp)
                .fillMaxSize()
                .background(color = Color.White, shape = RoundedCornerShape(8.dp))
        ) {
            item {
                Text(
                    text = "Explore",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 30.dp, start = 24.dp)
                )
                explore_tools(navController = navController)
                Text(
                    text = "Wellbeing",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 12.dp, start = 24.dp)
                )
                ExploreWellbeing(navController = navController)
                Text(
                    text = "Self awareness",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 12.dp, start = 24.dp)
                )
                ExploreSelfAware(navController = navController)
            }

            item {
                Text(
                    text = "Pending Rechecks",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 18.dp, start = 24.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }


            items(lastScans.take(n = 3)){data ->
                DataListFull(
                    title = data.title,
                    subtitle = data.domain,
                    data = data.domain,
                    additionData = data.timestamp.let {
                        val instant = Instant.ofEpochMilli(it)
                        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
                        formatter.format(dateTime)
                    },
                    imageVector = Icons.Filled.HistoryToggleOff,
                    colorLogo = Color.White,
                    additionalDataColor = lightTextAccent,
                    colorLogoTint = Color.Black,
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
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "View More",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 15.sp,
                        modifier = Modifier
                            .padding(top = 14.dp, bottom = 21.dp)
                            .clickable {
                            },
                        color = customBlue
                    )
                }
            }
        }
    }
}

@Composable
fun ExploreSelfAware(
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
    ){
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        ) {
            explore_tabs(
                title = "Asthma",
                icon = Icons.Outlined.MedicalInformation,
                weight = .49f,
                navController = navController,
                route = bottomNavItems.Scan.route
            )
            Spacer(modifier = Modifier.width(12.dp))
            explore_tabs(
                title = "Diabetes",
                icon = Icons.Outlined.MedicalInformation,
                weight = 1f,
                navController = navController,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        ) {
            explore_tabs(
                title = "Asthma",
                icon = Icons.Outlined.MedicalInformation,
                weight = .49f,
                navController = navController,
                route = bottomNavItems.Scan.route
            )
            Spacer(modifier = Modifier.width(12.dp))
            explore_tabs(
                title = "Diabetes",
                icon = Icons.Outlined.MedicalInformation,
                weight = 1f,
                navController = navController,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
fun ExploreWellbeing(
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
    ) {
        DataListFull(
            title = "Suggested Actions/ Habits",
            subtitle = "AI-based recommendations",
            imageVector = Icons.Outlined.Grain,
            colorLogo = Color.White,
            additionalDataColor = lightTextAccent,
            colorLogoTint = Color.Black,
            onClickListener = {
                Log.d("logStatus", "clicked")
            }
        )
        DataListFull(
            title = "Calorie Tracker",
            subtitle = "snap & track",
            imageVector = Icons.Outlined.Grain,
            colorLogo = Color.White,
            additionalDataColor = lightTextAccent,
            colorLogoTint = Color.Black,
            onClickListener = {
                Log.d("logStatus", "clicked")
            }
        )
        DataListFull(
            title = "Diet Plan",
            subtitle = "that suits you best",
            imageVector = Icons.Outlined.BrunchDining,
            colorLogo = Color.White,
            additionalDataColor = lightTextAccent,
            colorLogoTint = Color.Black,
            onClickListener = {
                Log.d("logStatus", "clicked")
            }
        )
    }
}

@Composable
fun explore_tools(
    navController: NavHostController
) {
    val isDoctorExpanded = remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ){
        explore_home(navController = navController, snackBarToggleVM = snackBarToggleVM())
        Spacer(modifier = Modifier.height(6.dp))
        DataListFull(
            title = "Import medical history",
            subtitle = "improves diagnosis",
            imageVector = Icons.Outlined.FileDownload,
            colorLogo = Color.White,
            additionalDataColor = lightTextAccent,
            colorLogoTint = Color.Black,
            onClickListener = {
                Log.d("logStatus", "clicked")
            }
        )
        Log.d("logStatus", isDoctorExpanded.value.toString())
    }
}