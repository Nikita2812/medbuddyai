package com.rohnsha.medbuddyai

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rohnsha.medbuddyai.domain.viewmodels.sideStateVM
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavGraph
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.navigation.sidebar.domain.NavItemSidebar
import com.rohnsha.medbuddyai.navigation.sidebar.domain.customDrawerState
import com.rohnsha.medbuddyai.navigation.sidebar.domain.isOpened
import com.rohnsha.medbuddyai.navigation.sidebar.screens.CustomDrawer
import com.rohnsha.medbuddyai.ui.theme.MedbuddyAITheme
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.formAccent

class MainActivity : ComponentActivity() {
    lateinit var snackBarToggle: snackBarToggleVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var isLoadingCompleted= false
        installSplashScreen().setKeepOnScreenCondition{
            !isLoadingCompleted
        }
        ContextUtill.ContextUtils.initialize(this)
        setContent {
            MedbuddyAITheme {
                val items= listOf(
                    bottomNavItems.Home,
                    bottomNavItems.mAI,
                    bottomNavItems.Community,
                    bottomNavItems.Preferences,
                )
                var selectedItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }
                val navcontroller= rememberNavController()
                val navBackStackEntry by navcontroller.currentBackStackEntryAsState()
                val currentDestination= navBackStackEntry?.destination
                val currentItemIndex = items.indexOfFirst { it.route == currentDestination?.route }
                val bottomDestination= items.any { it.route== currentDestination?.route }

                if (currentItemIndex != -1 && currentItemIndex != selectedItemIndex){
                    selectedItemIndex= currentItemIndex
                }

                snackBarToggle= viewModel<snackBarToggleVM>()

                Scaffold(
                    bottomBar = {
                        if (bottomDestination){
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                items.forEachIndexed { index, bottomNavItems ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex==index,
                                        onClick = {
                                            selectedItemIndex= index
                                            navcontroller.navigate(bottomNavItems.route){
                                                popUpTo(navcontroller.graph.findStartDestination().id)
                                                launchSingleTop= true
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = bottomNavItems.title,
                                                fontSize = 12.sp,
                                                fontFamily = fontFamily,
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = MaterialTheme.colorScheme.secondary,
                                            selectedIconColor = customBlue,
                                            unselectedIconColor = formAccent,
                                            selectedTextColor = customBlue
                                        ),
                                        icon = {
                                            Icon(
                                                imageVector = if (selectedItemIndex==index){
                                                    bottomNavItems.selectedIcon
                                                } else bottomNavItems.unselectedIcon,
                                                contentDescription = bottomNavItems.title
                                            )
                                        },
                                        alwaysShowLabel = false,
                                    )
                                }
                            }
                        }
                    },
                    containerColor = Color.Blue
                ) { paddingValues ->

                    val sidebarStateVM= viewModel<sideStateVM>()

                    var drawerState = sidebarStateVM.sidebarState.collectAsState().value
                    Log.d("drawerStateMain", drawerState.toString())
                    var selectedNavigationItem by remember { mutableStateOf(NavItemSidebar.Home) }

                    BackHandler(enabled = drawerState.isOpened()) {
                        drawerState = customDrawerState.Closed
                    }

                    val packageName= this.packageName

                    Log.d("dbStatus", "package name: ${this.packageName}")
                    Log.d("dbStatus", "version name: ${packageManager.getPackageInfo(this.packageName, 0).versionName}")

                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .fillMaxSize(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if(drawerState.isOpened()){
                            CustomDrawer(
                                selectedNavigationItem = selectedNavigationItem,
                                onNavigationItemClick = {
                                    selectedNavigationItem = it
                                },
                                onCloseClick = { drawerState = customDrawerState.Closed },
                                sideStateVM = sidebarStateVM
                            )
                        }
                        bottomNavGraph(
                            navController = navcontroller,
                            padding = paddingValues,
                            snackBarVM = snackBarToggle,
                            packageInfo = packageManager.getPackageInfo(packageName, 0),
                            sideDrawerState = sidebarStateVM
                        )
                    }

                    Log.d("snackbarState", snackBarToggle.readyToSendToast.collectAsState().value.toString())
                    isLoadingCompleted=true
                    if (snackBarToggle.readyToSendToast.collectAsState().value){
                        snackBarToggle.MySnackbar()
                    }
                }
            }
        }
    }
}
