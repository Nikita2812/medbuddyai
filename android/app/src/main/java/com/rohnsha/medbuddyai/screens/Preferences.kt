package com.rohnsha.medbuddyai.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.R
import com.rohnsha.medbuddyai.database.userdata.currentUser.currentUserDataVM
import com.rohnsha.medbuddyai.domain.dataclass.moreActions
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.domain.viewmodels.userAuthVM
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.formAccent
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    padding: PaddingValues,
    navController: NavHostController,
    currentUserDataVM: currentUserDataVM,
    userAuthVM: userAuthVM,
    snackBarToggleVM: snackBarToggleVM
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = bottomNavItems.Preferences.title,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 26.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            )
        },
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.tertiary
    ) { values ->
        val scope= rememberCoroutineScope()
        val name= remember {
            mutableStateOf("Guest User")
        }
        val isUserUnAuthenticated= userAuthVM.isUserUnAuthenticated()

        if (!isUserUnAuthenticated){
            LaunchedEffect(key1 = true) {
                val userData= currentUserDataVM.getAllUsers().filter { it.isDefaultUser }[0]
                name.value= "${userData.fname} ${userData.lname}"
            }
        }

        val context= LocalContext.current
        val profileAction= mutableListOf<moreActions>()
        if (!isUserUnAuthenticated){
            profileAction.add(
                moreActions(title = "Personal Informations", onClick = {
                    Log.d("action", "MoreScreen: Personal Informations")
                    navController.navigate(bottomNavItems.ProfileScreen.route)
                })
            )
        }
        profileAction.add(moreActions("API Secrets") { navController.navigate(bottomNavItems.ApiScreen.route) })
        val settingActions= listOf(
            moreActions("Data Usage") { navController.navigate(bottomNavItems.documentations.returnDoc(0)) },
            moreActions(
            if (isUserUnAuthenticated) "Sign In" else "Sign Out"
            ) {
                if (isUserUnAuthenticated){
                    navController.navigate(bottomNavItems.LogoWelcome.isLocalAccEnbld(false))
                } else {
                    scope.launch {
                        userAuthVM.signOut(
                            context = context,
                            navController = navController
                        )
                    }
                }
            }
        )
        LazyColumn(
            modifier = Modifier
                .padding(values)
                .padding(padding)
                .padding(top = 20.dp)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                .padding(top = 30.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .height(75.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(start = 18.dp)
                    ) {
                        Text(
                            text = "Hello,",
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = name.value,
                            fontSize = 18.sp,
                            fontWeight = FontWeight(600),
                            fontFamily = fontFamily,
                            modifier = Modifier
                                .offset(y = (-2).dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 24.dp)
                    ) {
                        Image(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile Avatar",
                            modifier = Modifier
                                .size(70.dp)
                                .background(MaterialTheme.colorScheme.secondary, CircleShape)
                                .padding(10.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                }
            }
            item {
                TextSubHead(
                    text = "Profile Informations",
                    modifier = Modifier
                        .padding(start = 24.dp, top = 18.dp, bottom = 8.dp)
                )
            }
            items(profileAction){
                MoreOptions(data = it)
            }
            item {
                TextSubHead(
                    text = "Settings",
                    modifier = Modifier
                        .padding(start = 24.dp, top = 18.dp, bottom = 8.dp)
                )
            }
            items(settingActions){
                MoreOptions(data = it)
            }
            item {
                TextSubHead(
                    text = "About Us",
                    modifier = Modifier
                        .padding(start = 24.dp, top = 18.dp, bottom = 8.dp)
                )
            }
            item { AboutApp(navController = navController) }
        }
    }
}

@Composable
fun AboutApp(
    navController: NavHostController
) {
    Column {
        Row(
            modifier = Modifier
                .height(125.dp)
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp)),
        ){
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 13.dp, top = 13.dp),
                horizontalAlignment = Alignment.Start
            ) {
                AboutUsTitleData(title = "Version", data = "1.6.0 Plant Sown")
                AboutUsTitleData(title = "Build Number", data = "2024.09.25.15")
                val context= LocalContext.current
                Row(
                    modifier = Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        imageVector = Icons.Filled.Language,
                        contentDescription = "website",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .clickable {
                                val webURL = "https://swasthai.rohanshaw.me/"
                                navController.navigate(
                                    bottomNavItems.webUIScreen.returnDocURL(webURL)
                                )
                            }
                            .padding(4.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    /*
                    Image(
                        imageVector = Icons.Filled.StarHalf,
                        contentDescription = "rate code",
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White, CircleShape)
                            .padding(4.dp)
                    )*/
                }
                AboutUsTitleData(title = "Maintainer", data = "Rohan Shaw", isLightAccent = true)
                Spacer(modifier = Modifier.height(13.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.logo_welcme),
                contentDescription = "logo",
                modifier = Modifier
                    .size(125.dp)
                    .padding(20.dp),
                colorFilter = ColorFilter.tint(color = customBlue)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun AboutUsTitleData(
    title: String,
    data: String,
    isLightAccent: Boolean = false
) {
    Row {
        Text(
            text = "${title}: ",
            fontSize = 14.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight(600),
            modifier = Modifier,
            color = if (isLightAccent) lightTextAccent else MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = data,
            fontSize = 14.sp,
            fontFamily = fontFamily,
            color = if (isLightAccent) lightTextAccent else MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun MoreOptions(
    data: moreActions
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .height(54.dp)
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
                .clickable { data.onClick() },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                modifier = Modifier
                    .padding(horizontal = 18.dp),
                text = data.title,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                fontFamily = fontFamily,
                fontWeight = FontWeight(600),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ){
                Image(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "action icon",
                    colorFilter = ColorFilter.tint(color = formAccent),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun TextSubHead(
    text: String,
    modifier: Modifier = Modifier
        .padding(top = 18.dp, start = 24.dp)
) {
    Text(
        text = text,
        fontFamily = fontFamily,
        fontWeight = FontWeight(600),
        fontSize = 14.sp,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onPrimary
    )
}