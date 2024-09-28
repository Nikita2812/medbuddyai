package com.rohnsha.medbuddyai.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.R
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.fontFamily

@Composable
fun WelcomeLogoScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    isLocalAccountEnabled: Boolean
) {
    Scaffold { value ->
        Column(
            modifier = modifier
                .padding(value)
                .fillMaxSize()
                .background(customBlue),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_welcme),
                contentDescription = "logo",
                modifier = modifier
                    .padding(top = 120.dp)
                    .size(120.dp)
            )
            Spacer(modifier = Modifier.height(29.dp))
            Text(text = "Welcome to", fontSize = 28.sp, fontFamily = fontFamily, color = Color.White.copy(alpha = 0.5043f))
            Text(text = "MedbuddyAI", fontSize = 47.sp, fontFamily = fontFamily, color = Color.White, fontWeight = FontWeight(600))
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { navController.navigate(bottomNavItems.userAuth.returnAuthMode(1)) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                )
            ) {
                Text(text = "Login", color = customBlue, fontSize = 18.sp, fontFamily = fontFamily)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(text = "Don't have an account? ", fontSize = 14.sp, fontFamily = fontFamily, color = Color.White)
                Text(text = "Sign up", fontSize = 14.sp, fontFamily = fontFamily, color = Color.White, fontWeight = FontWeight(600),
                    modifier = Modifier.clickable { navController.navigate(bottomNavItems.userAuth.returnAuthMode(0)) })
            }
            if (isLocalAccountEnabled){
                Row {
                    Text(text = "Or ", fontSize = 14.sp, fontFamily = fontFamily, color = Color.White)
                    Text(text = "Create a Local account instead", fontSize = 14.sp, fontFamily = fontFamily, color = Color.White, fontWeight = FontWeight(600),
                        modifier = Modifier.clickable { navController.navigate(bottomNavItems.UserInputScreen.route) })
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}