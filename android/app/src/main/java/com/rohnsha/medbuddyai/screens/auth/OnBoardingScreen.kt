package com.rohnsha.medbuddyai.screens.auth

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.R
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    navController: NavHostController
) {

    val pageState= rememberPagerState(initialPage = 0, pageCount = { 4 })
    val scope= rememberCoroutineScope()
    HorizontalPager(state = pageState) { page ->
        OnBoardingPageContent(pageNumber = page, btnAction = {
            scope.launch {
                pageState.animateScrollToPage(pageState.currentPage + 1)
            }
        }, navController = navController)
    }
}

@Composable
fun OnBoardingPageContent(
    pageNumber: Int,
    btnAction: () -> Unit,
    navController: NavHostController
) {
    androidx.compose.material3.Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.tertiary
    ){ values ->
        val sharedPrefs= navController.context.getSharedPreferences("onBoardingBoolean", Context.MODE_PRIVATE)

        Column(
            modifier = Modifier
                .padding(values)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.tertiary)
        ){
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Skip",
                color = customBlue,
                fontSize = 18.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight(600),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 24.dp)
                    .clickable {
                        sharedPrefs.edit().putBoolean("isOnBoardingCompleted", true).apply()
                        navController.navigate(route = bottomNavItems.LogoWelcome.isLocalAccEnbld()){
                            popUpTo(navController.graph.startDestinationId){
                                inclusive = true
                            }
                        }
                    }
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(288.dp),
                contentAlignment = Alignment.BottomCenter
            ){
                Image(painter = painterResource(id = when (pageNumber) {
                    0 -> R.drawable.fitness
                    1 -> R.drawable.undraw_live_photo_re_4khn
                    2 -> R.drawable.undraw_online_discussion_re_nn7e
                    3 -> R.drawable.undraw_certification_re_ifll
                    else -> R.drawable.fitness
                }), contentDescription = null, modifier= Modifier.padding(horizontal = 24.dp).size(224.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp))
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Spacer(modifier = Modifier.height(30.dp))
                LazyRow {
                    items(4){
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .then(
                                if (it == pageNumber) {
                                    Modifier.background(customBlue)
                                } else {
                                    Modifier.background(MaterialTheme.colorScheme.secondary)
                                }
                            ))
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = when (pageNumber) {
                        0 -> "Welcome to MedbuddyAI"
                        1 -> "On-Device Scanning"
                        2 -> "Chatbot & Forum"
                        3 -> "Your Safety is our Priority"
                        else -> ""
                    },
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(700),
                    fontSize = 26.sp,
                    color= MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = when (pageNumber) {
                        0 -> "Manage your health with AI! it's all in one place."
                        1 -> "Scan and detect possible disease, with on-device local processing."
                        2 -> "Ask your queries with our chatbot powered by RAG, engage with other users."
                        3 -> "Every action is stored and processed locally, ensuring your safety."
                        else -> ""
                    },
                    color = lightTextAccent,
                    fontSize = 18.sp,
                    fontFamily = fontFamily,
                    modifier = Modifier,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
                if(pageNumber==3){
                    Button(
                        onClick = {
                            sharedPrefs.edit().putBoolean("isOnBoardingCompleted", true).apply()
                            navController.navigate(route = bottomNavItems.LogoWelcome.isLocalAccEnbld()){
                                popUpTo(navController.graph.startDestinationId){
                                    inclusive = true
                                }
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = customBlue,
                            contentColor = Color.White
                        ))
                    {
                        Text(text = "Let's Get Started")
                    }
                }else {
                    OutlinedButton(
                        onClick = {
                            btnAction()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = customBlue),
                        border = BorderStroke(width = 1.dp, color = customBlue)
                    ) {
                        Text(text = "Next Step")
                    }
                }
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}