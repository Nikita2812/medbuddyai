package com.rohnsha.medbuddyai.navigation.bottombar

import WebViewLoaderSc
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rohnsha.medbuddyai.ContextUtill
import com.rohnsha.medbuddyai.database.appData.disease.diseaseDBviewModel
import com.rohnsha.medbuddyai.database.appData.disease_questions.questionVM
import com.rohnsha.medbuddyai.database.userdata.chatbot.chatDB_VM
import com.rohnsha.medbuddyai.database.userdata.communityTable.communityDBVM
import com.rohnsha.medbuddyai.database.userdata.currentUser.currentUserDataVM
import com.rohnsha.medbuddyai.database.userdata.keys.keyVM
import com.rohnsha.medbuddyai.database.userdata.scan_history.scanHistoryViewModel
import com.rohnsha.medbuddyai.domain.viewmodels.DeocdeReportVM
import com.rohnsha.medbuddyai.domain.viewmodels.chatVM
import com.rohnsha.medbuddyai.domain.viewmodels.classificationVM
import com.rohnsha.medbuddyai.domain.viewmodels.communityVM
import com.rohnsha.medbuddyai.domain.viewmodels.photoCaptureViewModel
import com.rohnsha.medbuddyai.domain.viewmodels.sideStateVM
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.domain.viewmodels.userAuthVM
import com.rohnsha.medbuddyai.screens.BMIScreen
import com.rohnsha.medbuddyai.screens.ChatBotScreen
import com.rohnsha.medbuddyai.screens.CommunityReply
import com.rohnsha.medbuddyai.screens.CommunityScreen
import com.rohnsha.medbuddyai.screens.DecodeReportUI
import com.rohnsha.medbuddyai.screens.DiseasesCatelogue
import com.rohnsha.medbuddyai.screens.DocumentationScreen
import com.rohnsha.medbuddyai.screens.ExploreScreen
import com.rohnsha.medbuddyai.screens.HomeScreen
import com.rohnsha.medbuddyai.screens.MoreScreen
import com.rohnsha.medbuddyai.screens.UserAddComp
import com.rohnsha.medbuddyai.screens.UserStatScreen
import com.rohnsha.medbuddyai.screens.auth.OnBoardingScreen
import com.rohnsha.medbuddyai.screens.auth.UserAuthScreen
import com.rohnsha.medbuddyai.screens.auth.WelcomeLogoScreen
import com.rohnsha.medbuddyai.screens.mAIScreen
import com.rohnsha.medbuddyai.screens.preferences.APIScreen
import com.rohnsha.medbuddyai.screens.preferences.ProfileInfoScreen
import com.rohnsha.medbuddyai.screens.scan.DoctorScreen
import com.rohnsha.medbuddyai.screens.scan.ScanCategoryScreen
import com.rohnsha.medbuddyai.screens.scan.ScanQuestions
import com.rohnsha.medbuddyai.screens.scan.ScanResultScreen
import com.rohnsha.medbuddyai.screens.scan.ScanScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates

private var isPatientsUnAdded by Delegates.notNull<Boolean>()

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun bottomNavGraph(
    navController: NavHostController,
    padding: PaddingValues,
    snackBarVM: snackBarToggleVM,
    packageInfo: PackageInfo,
    sideDrawerState: sideStateVM
) {
    val savePhotoViewModel= viewModel<photoCaptureViewModel>()
    val classifierVM= viewModel<classificationVM>()
    val communityVM= viewModel<communityVM>()
    val userAuth= viewModel<userAuthVM>()
    val currentUserVM= viewModel<currentUserDataVM>()
    val keyVM= viewModel<keyVM>()
    val questionVM= viewModel<questionVM>()
    val communityDBVModel= viewModel<communityDBVM>()
    val _auth= FirebaseAuth.getInstance()
    val chatVM= viewModel<chatVM>()
    val decodeReportVM= viewModel<DeocdeReportVM>()

    runBlocking {
        currentUserVM.countEntries()
    }

    val credentialManager by lazy {
        CredentialManager.create(ContextUtill.ContextUtils.getApplicationContext())
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        val notificationState: PermissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        if (!notificationState.status.isGranted) {
            ActivityCompat.requestPermissions(
                LocalContext.current as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    val username = remember {
        mutableStateOf("")
    }

    Log.d("username", "topNavGraph: ${_auth.currentUser!=null}")

    if (_auth.currentUser!=(null)){
        LaunchedEffect(key1 = true) {
            currentUserVM.getQueryData(isDefaultUser = true)
        }
        username.value= currentUserVM.userName.collectAsState().value
    }

    Log.d("username", "bottomNavGraph: ${username.value}")

    val dbRef= Firebase.database.reference
    userAuth.initialize(instance = _auth, dbReference = dbRef, currentUserVM, keyVM = keyVM, username = username.value, communityVM = communityVM, credMan = credentialManager)
    communityVM.initialize(instance = _auth, dbReference = dbRef, username = username.value, communityDBVModel = communityDBVModel, snackBarToggleVM = snackBarVM)
    val scanHistoryviewModel= viewModel<scanHistoryViewModel>()
    val diseaseDBviewModel= viewModel<diseaseDBviewModel>()
    val chatdbVM= viewModel<chatDB_VM>()
    val context: Context= LocalContext.current

    LaunchedEffect(key1 = true){
        scanHistoryviewModel.readScanHistory()
    }

    Log.d("dbStatus", "state: ${diseaseDBviewModel.updatingDiseaseDB.collectAsState().value}")
    LaunchedEffect(key1 = true){
        communityVM.getFeed()
        Log.d("dbStatus", "Starting VM")
        delay(750L)
        packageInfo.versionName?.let { diseaseDBviewModel.fetchUpdatedDB(versionName = it, context = context) }
        Log.d("dbStatus", "Ending VM")
    }

    val sharedPreferences = LocalContext.current.getSharedPreferences("onBoardingBoolean", Context.MODE_PRIVATE)
    val isOnBoardingCompleted = sharedPreferences.getBoolean("isOnBoardingCompleted", false)

    LaunchedEffect(true) { isPatientsUnAdded= currentUserVM.getAllUsers().isNotEmpty() }
    val userCount= currentUserVM.userCount.collectAsState().value

    NavHost(
        navController = navController,
        startDestination = if (userAuth.isUserUnAuthenticated()){
            if (isOnBoardingCompleted){
                if (userCount>0){
                    bottomNavItems.Home.route
                } else bottomNavItems.LogoWelcome.isLocalAccEnbld()
            } else bottomNavItems.onBoardingScreen.route
        } else bottomNavItems.Home.route
    ){
        composable(route = bottomNavItems.Home.route){
            HomeScreen(
                padding = padding,
                navController = navController,
                scanHistoryVM = scanHistoryviewModel,
                diseaseDBviewModel = diseaseDBviewModel,
                chatdbVm = chatdbVM,
                currentUserDataVM = currentUserVM,
                snackBarToggleVM = snackBarVM,
                sideStateVM = sideDrawerState
            )
        }
        composable(route = bottomNavItems.Community.route){
            CommunityScreen(
                padding = padding,
                navController = navController,
                snackBarViewModel = snackBarVM,
                communityViewModel = communityVM,
                communityDBVM = communityDBVModel,
                userAuthVM = userAuth
            )
        }
        composable(
            route= bottomNavItems.UserInputScreen.route
        ){
            UserAddComp(
                navController = navController,
                currentUserDataVM = currentUserVM,
                snackBarToggleVM = snackBarVM
            )
        }
        composable(
            route = bottomNavItems.decodeReportScreen.route
        ){
            DecodeReportUI(
                navController = navController,
                currentUserDataVM = currentUserVM,
                keyVM = keyVM,
                viewModelPhotoSave = decodeReportVM
            )
        }
        composable(route = bottomNavItems.CommunityReply.route,
                arguments = listOf(
                    navArgument(postID){
                        type= NavType.StringType
                    }
                )
            ){
            CommunityReply(
                padding = padding,
                postID = it.arguments?.getString(postID)!!,
                communityVM = communityVM,
                snackBarToggleVM = snackBarVM,
                communityDBVM = communityDBVModel,
                navController = navController,
                userAuthVM = userAuth
            )
        }
        composable(route = bottomNavItems.Preferences.route){
            MoreScreen(padding = padding, navController = navController, currentUserDataVM = currentUserVM, snackBarToggleVM = snackBarVM, userAuthVM = userAuth)
        }
        composable(
            route = bottomNavItems.Scan.route,
            arguments = listOf(
                navArgument(scanIndexKey){
                    type= NavType.IntType
                },
                navArgument(scanMode){
                    type= NavType.IntType
                }
            )
        ){
            ScanScreen(
                padding = padding,
                navController = navController,
                photoCaptureVM = savePhotoViewModel,
                classifierVM = classifierVM,
                index = it.arguments!!.getInt(scanIndexKey),
                sideStateVM = sideDrawerState, currentUserDataVM = currentUserVM,
                keyVM = keyVM, snackBarToggleVM = snackBarVM, mode = it.arguments!!.getInt(scanMode)
            )
        }

        composable(
            route = bottomNavItems.ScanQA.route,
            arguments = listOf(
                navArgument(scanIndexKey){
                    type= NavType.IntType
                },
                navArgument(scanQAMode){
                    type= NavType.IntType
                }
            )
        ){
            ScanQuestions(
                indexClassification = it.arguments!!.getInt(scanIndexKey),
                photoCaptureViewModel = savePhotoViewModel,
                sideStateVM = sideDrawerState,
                padding = padding,
                navHostController = navController, scanMode = it.arguments!!.getInt(scanQAMode)
            )
        }

        composable(route = bottomNavItems.ScanCategory.route){
            ScanCategoryScreen(
                padding = padding,
                navController,
                photoCaptureViewModel = savePhotoViewModel,
                snackBarViewModel = snackBarVM,
                sideStateVM = sideDrawerState, currentUserDataVM = currentUserVM,
                keyVM = keyVM
            )
        }

        composable(
            route = bottomNavItems.DiseaseCatelogue.route,
            arguments = listOf(
                navArgument(domainSelection){
                    type= NavType.IntType
                }
            )
        ){
            DiseasesCatelogue(padding, diseaseDBviewModel, navController, it.arguments!!.getInt(
                domainSelection
            ))
        }

        composable(
            route = bottomNavItems.Explore.route
        ){
            ExploreScreen(
                padding = padding,
                navController = navController,
                scanHistoryViewModel = scanHistoryviewModel,
                diseaseDBviewModel
            )
        }

        composable(
            route = bottomNavItems.BMI.route
        ){
            BMIScreen(
                padding = padding,
                navController = navController,
                keyVM = keyVM
            )
        }

        composable(route = bottomNavItems.mAI.route){
            mAIScreen(
                padding = padding,
                navController, chatdbVM, snackBarToggleVM = snackBarVM
            )
        }

        composable(route = bottomNavItems.onBoardingScreen.route){
            OnBoardingScreen(navController = navController)
        }

        composable(
            route = bottomNavItems.Chatbot.route,
            arguments = listOf(
                navArgument(chatID){
                    type= NavType.IntType
                },
                navArgument(chatMode){
                    type= NavType.IntType
                }
            )
        ){
            ChatBotScreen(
                paddingValues = padding,
                snackBarVM,
                communityVM = communityVM,
                scanHistoryViewModel = scanHistoryviewModel,
                chatdbVm = chatdbVM,
                chatID = it.arguments!!.getInt(chatID),
                mode = it.arguments!!.getInt(chatMode),
                diseaseDBviewModel = diseaseDBviewModel,
                sideStateVM = sideDrawerState,
                currentUserDataVM = currentUserVM,
                chatVM = chatVM,
                keyVM = keyVM,
                navController = navController,
                userAuthVM = userAuth
            )
        }

        composable(
            route = bottomNavItems.ScanResult.route,
            arguments = listOf(
                navArgument(scanResultKey){
                    type= NavType.IntType
                },
                navArgument(scanResultIndex){
                    type= NavType.IntType
                },
                navArgument(scanResultMode){
                    type= NavType.IntType
                }
            )
        ){
            ScanResultScreen(
                padding = padding,
                chatVM = chatVM,
                navController = navController,
                viewModel = savePhotoViewModel,
                scanHistoryViewModel = scanHistoryviewModel,
                diseaseDBviewModel = diseaseDBviewModel,
                resultsLevel = it.arguments!!.getInt(scanResultKey),
                indexClassification = it.arguments!!.getInt(scanResultIndex),
                snackbarHostState = snackBarVM,
                communityVM = communityVM,
                currentUserDataVM = currentUserVM,
                chatdbVm = chatdbVM,
                keyVM = keyVM,
                scanMode = it.arguments!!.getInt(scanResultMode),
                userAuthVM = userAuth
            )
        }
        composable(
            route = bottomNavItems.DoctorScreen.route,
            arguments = listOf(
                navArgument(domainID){
                    type= NavType.IntType
                }
            )
        ){
            DoctorScreen(
                padding = padding,
                diseaseDBviewModel = diseaseDBviewModel,
                domain = it.arguments!!.getInt(domainID),
                navController = navController
            )
        }
        composable(
            route = bottomNavItems.LogoWelcome.route,
            arguments = listOf(
                navArgument(isLocalAccountEnabled){
                    type= NavType.BoolType
                }
            )
        ){
            WelcomeLogoScreen(
                navController = navController,
                isLocalAccountEnabled = it.arguments!!.getBoolean(isLocalAccountEnabled)
            )
        }
        composable(
            route = bottomNavItems.ApiScreen.route
        ){
            APIScreen(keyVM, navController = navController, snackBarToggleVM = snackBarVM)
        }
        composable(route = bottomNavItems.ProfileScreen.route){
            ProfileInfoScreen(
                currentUserDataVM = currentUserVM, navController = navController, userAuthVM = userAuth,
                snackBarToggleVM = snackBarVM
            )
        }
        composable(
            route = bottomNavItems.documentations.route,
            arguments = listOf(
                navArgument(docIndex){
                    type= NavType.IntType
                }
            )
        ){
            DocumentationScreen(index = it.arguments!!.getInt(docIndex), navController)
        }
        composable(
            route = bottomNavItems.userStatScreen.route,
            arguments = listOf(
                navArgument(userIndex){
                    type= NavType.IntType
                },
                navArgument(viewMode){
                    type= NavType.IntType
                }
            )
        ){
            UserStatScreen(
                userIndexx = it.arguments!!.getInt(userIndex),
                navController = navController,padding = padding, scanHistoryViewModel = scanHistoryviewModel, diseaseDBviewModel = diseaseDBviewModel,
                chatdbVm = chatdbVM,
                currentUserDataVM = currentUserVM,
                snackBarToggleVM = snackBarVM,
                viewMode = it.arguments!!.getInt(viewMode)
            )
        }
        composable(
            route= bottomNavItems.webUIScreen.route,
            arguments = listOf(
                navArgument(docURL){
                    type= NavType.StringType
                }
            )
        ){ backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString(docURL) ?: "https://swasthai.rohanshaw.me/"
            val decodedUrl = Uri.decode(encodedUrl)
            WebViewLoaderSc(url = decodedUrl, navController = navController)
        }
        composable(
            route = bottomNavItems.userAuth.route,
            arguments = listOf(
                navArgument(authMode){
                    type= NavType.IntType
                }
            )
        ){
            UserAuthScreen(mode = it.arguments!!.getInt(authMode), navController = navController, userAuthVM = userAuth
                , snackBarToggleVM = snackBarVM)
        }
    }
}