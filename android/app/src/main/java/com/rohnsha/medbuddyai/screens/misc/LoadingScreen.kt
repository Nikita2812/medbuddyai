package com.rohnsha.medbuddyai.screens.misc

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohnsha.medbuddyai.R
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.formAccent
import kotlin.random.Random

@Composable
fun LoadingLayout(
    titleList: List<String>
) {
    val random = Random(System.currentTimeMillis())
    val title= titleList[random.nextInt(0, (titleList.size-1))]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.tertiary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.95f),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(R.drawable.undraw_loading_re_5axr),
                contentDescription = "Loading Image",
                modifier = Modifier
                    .size(150.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize(),
            Alignment.BottomCenter
        ){
            Text(
                modifier = Modifier.padding(bottom = 20.dp),
                text = title,
                fontWeight = FontWeight(600),
                fontFamily = fontFamily,
                color = formAccent,
                fontSize = 14.sp
            )
        }
    }
}