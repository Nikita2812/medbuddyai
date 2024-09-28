package com.rohnsha.medbuddyai.screens.misc

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohnsha.medbuddyai.R
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent

@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
@Composable
fun NormalErrorStateLayout(
    state: Int // 0 for normal, 1 for error
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = conditionalStrings(state = state, string0 = "Normal", string1 = "Unrecognized Error"),
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
    ){ value ->
        Column(
            modifier = Modifier
                .padding(value)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.tertiary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = conditionalStrings(
                    state = state,
                    string0 = "You don't seem to have any problems that we were able to diagnose/ recognize.",
                    string1 = "There seem to be technical issues with our diagnosis right now."
                ),
                modifier = Modifier
                    .padding(top = 8.dp, start = 24.dp, end = 24.dp)
                    .align(Alignment.CenterHorizontally),
                fontFamily = fontFamily,
                textAlign = TextAlign.Center,
                color = lightTextAccent
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(
                    if (state == 0) R.drawable.undraw_healthy_habit_kwe6 else R.drawable.undraw_warning_re_eoyh
                ),
                contentDescription = "Loading Image",
                modifier = Modifier
                    .size(150.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {  },
                colors = ButtonDefaults.buttonColors(
                    containerColor = customBlue
                )
            ) {
                Text(text = "Scan More", color = Color.White, fontSize = 18.sp, fontFamily = fontFamily)
            }
            Spacer(modifier = Modifier.padding(bottom = 38.dp))
        }
    }
}

private fun conditionalStrings(string0: String, string1: String, state: Int): String {
    return if (state == 0) string0 else string1
}