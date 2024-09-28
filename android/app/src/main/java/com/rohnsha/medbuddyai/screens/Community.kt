package com.rohnsha.medbuddyai.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CloseFullscreen
import androidx.compose.material.icons.outlined.FeaturedPlayList
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material.icons.outlined.Quickreply
import androidx.compose.material.icons.outlined.ShortText
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.database.userdata.communityTable.communityDBVM
import com.rohnsha.medbuddyai.database.userdata.communityTable.postWithReply
import com.rohnsha.medbuddyai.database.userdata.communityTable.postWithState
import com.rohnsha.medbuddyai.domain.viewmodels.communityVM
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.domain.viewmodels.userAuthVM
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.customGreen
import com.rohnsha.medbuddyai.ui.theme.customRed
import com.rohnsha.medbuddyai.ui.theme.customYellow
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    padding: PaddingValues,
    navController: NavHostController,
    snackBarViewModel: snackBarToggleVM,
    communityViewModel: communityVM,
    communityDBVM: communityDBVM,
    userAuthVM: userAuthVM
) {

    val processingState= communityViewModel.isCurrentlyPosting.collectAsState().value
    val listPosted= communityViewModel.postedList.collectAsState().value.filter { !it.isPosted }
    var isUserUnAuthenticated by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(true) {
        isUserUnAuthenticated= userAuthVM.isUserUnAuthenticated()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Community",
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
        val postsWithReplies = remember {
            mutableStateListOf<postWithReply>()
        }

        LaunchedEffect(key1 = postsWithReplies) {
            for (data in communityDBVM.mergePostReplies()){
                if (!postsWithReplies.contains(data)){
                    postsWithReplies.add(data)
                }
            }
            postsWithReplies.sortByDescending { it.post.timestamp }
        }

        Log.d("dataSnapCOmmPostReply", postsWithReplies.toString())

        val isCreateExpanded= remember {
            mutableStateOf(false)
        }
        val title= remember {
            mutableStateOf("")
        }
        val content= remember {
            mutableStateOf("")
        }
        val scope= rememberCoroutineScope()
        LazyColumn(
            modifier = Modifier
                .padding(values)
                .padding(padding)
                .padding(top = 20.dp)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                .padding(top = 30.dp, start = 24.dp, end = 24.dp)
        ) {
            item {
                Text(
                    text = "Create",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    explore_tabs(
                        title = if (isCreateExpanded.value) "Post Now" else "Write",
                        icon = if (isCreateExpanded.value) Icons.Outlined.TaskAlt else Icons.Outlined.PostAdd,
                        weight = if (isCreateExpanded.value) .56f else 1f,
                        onClickListener = {
                            if (isUserUnAuthenticated){
                                snackBarViewModel.SendToast(
                                    message = "Authentication is required to post",
                                    indicator_color = customYellow,
                                    padding = PaddingValues(),
                                    icon = Icons.Outlined.AccountCircle
                                )
                            } else {
                                if (isCreateExpanded.value) {
                                    if (content.value!=""){
                                        communityViewModel.post(
                                            resetValues = {
                                                title.value= ""
                                                content.value= ""
                                                isCreateExpanded.value= false
                                            },
                                            content= content.value,
                                            onCompleteLambda = {
                                                isCreateExpanded.value= false
                                                scope.launch {
                                                    postsWithReplies.add(
                                                        postWithReply(
                                                            post = it,
                                                            replies = emptyList()
                                                        )
                                                    )
                                                    postsWithReplies.sortByDescending { it.post.timestamp }
                                                }
                                                snackBarViewModel.SendToast(
                                                    message = "Post was uploaded successfully",
                                                    indicator_color = customGreen,
                                                    padding = PaddingValues(2.dp)
                                                )
                                            }
                                        )
                                    } else snackBarViewModel.SendToast(
                                        "Content field cannot be empty",
                                        customRed,
                                        PaddingValues(0.dp)
                                    )
                                } else isCreateExpanded.value= true
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    explore_tabs(
                        title = if (isCreateExpanded.value) "Close" else "Activity",
                        icon = if (isCreateExpanded.value) Icons.Outlined.CloseFullscreen else Icons.Outlined.FeaturedPlayList,
                        weight = if(isCreateExpanded.value) 1f else 0f,
                        onClickListener = {
                            if (isCreateExpanded.value) isCreateExpanded.value= false else snackBarViewModel.SendToast(
                                message = "The feature is still under making!",
                                indicator_color = Color.Red,
                                padding = PaddingValues(2.dp)
                        ) }
                    )
                }
                AnimatedVisibility (
                    visible = isCreateExpanded.value,
                ){
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextInputThemed(
                            value = content.value,
                            onValueChanged = { content.value= it },
                            label = "Contents",
                            icon = Icons.Outlined.ShortText,
                            onClose = { content.value = "" },
                            singleLine = false
                        )
                    }
                }
                if (processingState){
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
            if (processingState){
                items(listPosted){ it: postWithState ->
                    val data= it.post
                    CommunityPostItem(
                        title =data.author,
                        subtitle =data.id,
                        colorLogo = customBlue,
                        postData =data.content,
                        onClickListener = null
                    )
                }
            }
            item {
                Text(
                    text = "Recent Posts",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .then(
                            if (processingState){
                                Modifier.padding(bottom = 6.dp, top = 12.dp)
                            } else Modifier.padding(bottom = 6.dp, top = 18.dp)
                        )
                )
            }
            if (postsWithReplies.isNotEmpty()){
                items(postsWithReplies){ it ->
                    val data= it.post
                    CommunityPostItem(
                        title = data.author?: "Unknown",
                        subtitle = data.id?: "Unknown",
                        data = "${it.replies.size} replies",
                        additionData = data.timestamp?.let {
                            communityViewModel.calculateTimeDifference(
                                it.toLong())
                        }
                            ?: "Unspecified",
                        colorLogo = customBlue,
                        postData = data.content?: "Unknown",
                        onClickListener = {
                            it.post.id.let { it1 -> Log.d("repliesID", it1) }
                            navController.navigate(
                                bottomNavItems.CommunityReply.returnPostID(
                                    data.id
                        )) }
                    )
                }
            }
        }
    }
}

@Composable
fun CommunityPostItem(
    title: String,
    subtitle: String,
    data: String? = null,
    additionData: String? =null,
    imageVector: ImageVector= Icons.Outlined.Quickreply,
    colorLogo: Color,
    additionalDataColor: Color?= null,
    colorLogoTint: Color? = null,
    onClickListener: (() -> Unit)? =null,
    postData: String
) {
    Box(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
            .clickable {
                if (onClickListener != null) {
                    onClickListener()
                }
            },
        contentAlignment = Alignment.CenterStart
    ){
        Column {
            Row(
                modifier = Modifier
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(13.dp))
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(colorLogo, CircleShape),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = title.first().toString(),
                        fontSize = 18.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        color = colorLogoTint ?: Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 13.dp)
                ) {
                    Text(
                        modifier = Modifier,
                        text = title.clip(25),
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        color = MaterialTheme.colorScheme.onPrimary

                    )
                    Text(
                        modifier = Modifier
                            .offset(y = (-2).dp),
                        text = subtitle,
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        color = lightTextAccent
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 18.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    if (data==null && additionData==null){
                        LinearProgressIndicator(
                            modifier = Modifier
                                .width(32.dp)
                                .align(Alignment.End)
                        )
                    }
                    if (data != null) {
                        Text(
                            modifier = Modifier,
                            text = data,
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight(600)
                        )
                    }
                    additionData?.let {
                        Text(
                            modifier = Modifier
                                .offset(y = (-2).dp),
                            text = it,
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            color = additionalDataColor ?: colorLogo,
                            fontWeight = FontWeight(600)
                        )
                    }
                }
            }
            Text(
                text = postData,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp,
                fontFamily = fontFamily,
                modifier = Modifier
                    .padding(start = 13.dp, end = 24.dp, bottom = 13.dp)
                    .fillMaxWidth()
            )
        }
    }
}

fun String.clip(
    maxChar: Int
): String{
    return if (this.length>=maxChar){
        "${this.substring(0, maxChar)}..."
    } else{
        this
    }
}