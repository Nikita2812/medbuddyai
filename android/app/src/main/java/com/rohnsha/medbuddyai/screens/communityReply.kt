package com.rohnsha.medbuddyai.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.AddComment
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.database.userdata.communityTable.Reply
import com.rohnsha.medbuddyai.database.userdata.communityTable.communityDBVM
import com.rohnsha.medbuddyai.domain.viewmodels.communityVM
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.domain.viewmodels.userAuthVM
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.customGreen
import com.rohnsha.medbuddyai.ui.theme.customYellow
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityReply(
    padding: PaddingValues,
    postID: String,
    communityVM: communityVM,
    snackBarToggleVM:snackBarToggleVM,
    communityDBVM: communityDBVM,
    navController: NavHostController,
    userAuthVM: userAuthVM
) {
    val replyData= remember {
        mutableStateListOf<Reply>()
    }
    val isCurrentlyRepl= communityVM.isCurrentlyReplying.collectAsState().value
    LaunchedEffect(key1 = true) {
        for(reply in communityDBVM.filterReplies(postID = postID)){
            replyData.add(reply)
            Log.d("repliesa", "replyDataTxt: $reply")
        }
    }
    val isUserAuthenticated by remember {
        mutableStateOf(!userAuthVM.isUserUnAuthenticated())
    }
    Log.d("replies", replyData.toString())
    val replies = remember {
        mutableStateListOf<Reply>()
        //replyData.filter { it.id.substringBeforeLast(":").trim() == postID } as MutableList<Reply>
    }
    Log.d("replies", "replyData: ${replyData}\nreplies: $replies")
    val messageField= remember {
        mutableStateOf("")
    }
    val pendingReplies= communityVM.replyList.collectAsState().value.filter { it.id.substringBeforeLast(":").trim() == postID }
    val scope= rememberCoroutineScope()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Replies",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 26.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
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
        Log.d("replies", pendingReplies.toString())
        Column {
            LazyColumn(
                modifier = Modifier
                    .padding(values)
                    .padding(padding)
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                    .padding(top = 30.dp)
            ){
                if (replies.isNotEmpty()){
                    //val postDetails= communityDBVM.getPostDetails(postID)
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                            Text(
                                text = "Viewing replies to ",//${postDetails.author}'s post",
                                color = lightTextAccent,
                                fontFamily = fontFamily,
                                fontSize = 13.sp,
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
                items(replyData){
                    Box(modifier = Modifier.padding(horizontal = 20.dp)){
                        CommunityPostItem(
                            title = it.author,
                            subtitle = it.id,
                            data = "",
                            colorLogo = customBlue,
                            postData = it.content.toString(),
                            additionData = it.timestamp?.let { it1 ->
                                communityVM.calculateTimeDifference(
                                    it1.toLong())
                            }
                        )
                    }
                }
                items(pendingReplies){ data ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp)){
                        CommunityPostItem(
                            title =data.author,
                            subtitle ="temp: ${data.id}",
                            colorLogo = customBlue,
                            postData =data.content,
                            onClickListener = null
                        )
                    }
                }
            }
            if (isUserAuthenticated){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier= Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(bottom = 10.dp, start = 13.dp, end = 10.dp)
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value =messageField.value,
                        onValueChange = { messageField.value= it },
                        modifier = Modifier
                            .weight(1f)
                            .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp)),
                        shape= RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.secondary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                            disabledContainerColor = MaterialTheme.colorScheme.secondary,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                        ),
                        placeholder = { Text(text = "Enter reply", color= lightTextAccent) }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Outlined.AddComment, contentDescription = "",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable {
                                scope.launch {
                                    if (messageField.value != "") {
                                        communityVM.addReply(
                                            replyContent = messageField.value,
                                            postID = postID,
                                            onCompleteLambda = {
                                                val reply = it
                                                messageField.value = ""
                                                replyData.add(reply)
                                                snackBarToggleVM.SendToast(
                                                    message = "Reply was uploaded successfully",
                                                    indicator_color = customGreen,
                                                    padding = PaddingValues(2.dp)
                                                )
                                            },
                                            resetValues = {
                                                messageField.value=""
                                            }
                                        )
                                    } else {
                                        snackBarToggleVM.SendToast(
                                            message = "Enter a reply to be sent",
                                            indicator_color = customYellow,
                                            padding = PaddingValues(2.dp),
                                            icon = Icons.Outlined.Warning
                                        )
                                    }
                                }
                            }
                            .background(customBlue)
                            .padding(10.dp),
                        tint = Color.White
                    )
                }
            }
        }

    }
}