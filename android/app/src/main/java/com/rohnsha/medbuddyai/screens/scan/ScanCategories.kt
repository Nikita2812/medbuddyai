package com.rohnsha.medbuddyai.screens.scan

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AutoMode
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.database.userdata.currentUser.currentUserDataVM
import com.rohnsha.medbuddyai.database.userdata.keys.keyVM
import com.rohnsha.medbuddyai.domain.dataclass.modelMarketPlace
import com.rohnsha.medbuddyai.domain.viewmodels.photoCaptureViewModel
import com.rohnsha.medbuddyai.domain.viewmodels.sideStateVM
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.navigation.sidebar.screens.sideBarModifier
import com.rohnsha.medbuddyai.screens.BOMChangeDUser
import com.rohnsha.medbuddyai.ui.theme.customRed
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanCategoryScreen(
    padding: PaddingValues,
    navController: NavHostController,
    photoCaptureViewModel: photoCaptureViewModel,
    snackBarViewModel: snackBarToggleVM,
    sideStateVM: sideStateVM,
    currentUserDataVM: currentUserDataVM,
    keyVM: keyVM
    ) {

    val bomStateDUser= remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Select AI Model",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 26.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                actions = {
                    IconButton(onClick = {
                        bomStateDUser.value = true
                        //sideStateVM.toggleState()
                    }) {
                        Icon(
                            imageVector = Icons.Default.SettingsSuggest,
                            contentDescription = "Menu Icon"
                        )
                    }
                },
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
            .fillMaxSize()
            .then(sideBarModifier(sideStateVM = sideStateVM)),
        containerColor = MaterialTheme.colorScheme.tertiary
    ){ value ->
        LaunchedEffect(key1 = true, block = {
            photoCaptureViewModel.flushValues()
        })

        val respiratoryDiseases= listOf(
            modelMarketPlace("Lungs", "Various",
                "Pneumonia, Tuberculosis", false, 6
            ),
            modelMarketPlace("Lungs (Cancerous)", "V2023.06.51",
                "Lungs Adenocarcinoma, Squamous Cell Carcinoma", true, 7)
        )
        val brainCateg= listOf(
            modelMarketPlace("Brain", "V2024.01.51",
                "Gioma, Meningioma, Pituitary Tumor", false, 3
            )
        )

        val miscellaneousCateg= listOf(
            modelMarketPlace("Oral (Cancerous)", "V2024.01.51",
                "Oral Squamous Cell Carcinoma", true, 2)
        )

        val kidneyCateg= listOf(
            modelMarketPlace("Kidney", "V2024.01.24",
                "Kidney tumor", false, 11)
        )

        val skinManifestation= listOf(
            modelMarketPlace("Skin", "V2024.06.01",
                "Pigmentations, Eczima, Acne",
                false, 8),
            modelMarketPlace("Skin (Cancerous)", "V2024.06.01",
                "Skin cancer", true, 9)
        )

        val lymphatic= listOf(
            modelMarketPlace("Lymph Nodes", "V2024.06.01",
                "Acute Lymphoblastic Leukemia (Pre, Pro)", false, 0)
        )

        val digestive= listOf(
            modelMarketPlace("Colon (Cancerous)", "V2024.06.01",
                "Colon Adenocarcinoma", true, 1)
        )

        val reproductive= listOf(
            modelMarketPlace("Breast (Cancerous)", "V2024.06.01",
                "Breast Cancer", true, 4),
            modelMarketPlace("Cervical (Cancerous)", "V2024.06.01",
                "Cervical Cancer", true, 5)
        )

        if (bomStateDUser.value){
            ModalBottomSheet(
                onDismissRequest = { bomStateDUser.value = false },
            ) {
                BOMChangeDUser(currentUserDataVM = currentUserDataVM, keyVM = keyVM, onClickListener = {
                    bomStateDUser.value= false
                    currentUserDataVM.switchDefafultUser(it)
                }, isUserChangeable = true)
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(value)
                .padding(padding)
                .padding(top = 20.dp)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp))
        ){
            item {
                Text(
                    text = "Blood & Lymphatic System",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 26.dp, start = 24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(lymphatic){
                ModelItem(
                    title = it.modelName,
                    subtitle = it.modelVersion,
                    colorLogo = Color.White,
                    onClickListener = { navController.navigate(bottomNavItems.Scan.returnScanIndex(1)) },
                    description = it.description,
                    isCancerous = it.isCancerous
                )
            }
            item {
                Text(
                    text = "Digestive System",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 26.dp, start = 24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(digestive){
                ModelItem(
                    title = it.modelName,
                    subtitle = it.modelVersion,
                    colorLogo = Color.White,
                    onClickListener = { navController.navigate(bottomNavItems.Scan.returnScanIndex(1)) },
                    description = it.description,
                    isCancerous = it.isCancerous
                )
            }
            item {
                Text(
                    text = "Hand & Neck",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 26.dp, start = 24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(miscellaneousCateg){
                ModelItem(
                    title = it.modelName,
                    subtitle = it.modelVersion,
                    colorLogo = Color.White,
                    onClickListener = { navController.navigate(bottomNavItems.Scan.returnScanIndex(1)) },
                    description = it.description,
                    isCancerous = it.isCancerous
                )
            }
            item {
                Text(
                    text = "Nervous System",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 26.dp, start = 24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(brainCateg){
                ModelItem(
                    title = it.modelName,
                    subtitle = it.modelVersion,
                    colorLogo = Color.White,
                    onClickListener = { navController.navigate(bottomNavItems.Scan.returnScanIndex(1)) },
                    description = it.description,
                    isCancerous = it.isCancerous
                )
            }
            item {
                Text(
                    text = "Reproductive System",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 26.dp, start = 24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(reproductive){
                ModelItem(
                    title = it.modelName,
                    subtitle = it.modelVersion,
                    colorLogo = Color.White,
                    onClickListener = { if (it.domainIndex==5){
                        snackBarViewModel.SendToast(
                            "One of more field is empty!",
                            customRed,
                            PaddingValues(0.dp)
                        )
                    } else {
                        navController.navigate(bottomNavItems.Scan.returnScanIndex(it.domainIndex))
                    }
                                                              },
                    description = it.description,
                    isCancerous = it.isCancerous
                )
            }
            item{
                Text(
                    text = "Respiratory System",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 30.dp, start = 24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(respiratoryDiseases){
                ModelItem(
                    title = it.modelName,
                    subtitle = it.modelVersion,
                    colorLogo = Color.White,
                    onClickListener = { navController.navigate(route = bottomNavItems.Scan.returnScanIndex(it.domainIndex)) },
                    description = it.description,
                    isCancerous = it.isCancerous
                )
            }
            item {
                Text(
                    text = "Skin Diseases",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 26.dp, start = 24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(skinManifestation){
                ModelItem(
                    title = it.modelName,
                    subtitle = it.modelVersion,
                    colorLogo = Color.White,
                    onClickListener = { navController.navigate(bottomNavItems.Scan.returnScanIndex(it.domainIndex)) },
                    description = it.description,
                    isCancerous = it.isCancerous
                )
            }
            item {
                Text(
                    text = "Urinary System",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(600),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(top = 26.dp, start = 24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(kidneyCateg){
                ModelItem(
                    title = it.modelName,
                    subtitle = it.modelVersion,
                    colorLogo = Color.White,
                    onClickListener = { navController.navigate(bottomNavItems.Scan.returnScanIndex(1)) },
                    description = it.description,
                    isCancerous = it.isCancerous
                )
            }
            item { Spacer(modifier = Modifier.height(18.dp)) }
        }
    }
}

@Composable
fun ModelItem(
    title: String,
    subtitle: String?=null,
    data: String? = null,
    additionData: String? =null,
    isCancerous:Boolean,
    imageVector: ImageVector?= if(isCancerous) Icons.Outlined.Hub else Icons.Outlined.AutoMode,
    iconText: String?= null,
    colorLogo: Color = Color.White,
    actionIcon: ImageVector = Icons.Outlined.ArrowForward,
    additionalDataColor: Color?= null,
    colorLogoTint: Color? = null,
    onClickListener: (() -> Unit)? = null,
    description:String,
) {
    Box(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .height(90.dp)
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
            .then(
                if (onClickListener != null) Modifier.clickable { onClickListener() } else Modifier
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (imageVector != null) {
                    Image(
                        modifier = Modifier
                            .padding(start = 13.dp)
                            .size(34.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .padding(6.dp),
                        imageVector = imageVector,
                        contentDescription = "$imageVector icon",
                        colorFilter = ColorFilter.tint(colorLogoTint ?: MaterialTheme.colorScheme.onPrimary)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(start = 13.dp)
                            .size(34.dp)
                            .background(colorLogo, CircleShape)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ){
                        if (iconText != null) {
                            Text(
                                text = iconText,
                                fontSize = 12.sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight(600),
                                color = colorLogoTint?: Color.White,
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(start = 13.dp)
                ) {
                    Text(
                        modifier = Modifier,
                        text = title,
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    if (subtitle!=null){
                        Text(
                            modifier = Modifier
                                .offset(y = (-2).dp),
                            text = subtitle,
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
                            color = lightTextAccent
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 18.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    if (data != null) {
                        Text(
                            modifier = Modifier,
                            text = data,
                            fontSize = 14.sp,
                            fontFamily = fontFamily,
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
                    if (additionData==null && data==null){
                        Image(
                            imageVector = actionIcon,
                            contentDescription = "front icon",
                            colorFilter = ColorFilter.tint(additionalDataColor ?: lightTextAccent),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Text(
                modifier = Modifier
                    .padding(top = 8.dp, start = 13.dp),
                text = description,
                fontSize = 14.sp,
                fontFamily = fontFamily,
                color = lightTextAccent,
                maxLines = 1
            )
        }
    }
}