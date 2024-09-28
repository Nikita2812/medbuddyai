package com.rohnsha.medbuddyai.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material.icons.outlined.TipsAndUpdates
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.database.appData.disease.diseaseDBviewModel
import com.rohnsha.medbuddyai.domain.dataclass.disease_data_dataClass
import com.rohnsha.medbuddyai.domain.dataclass.rbStructure
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.screens.scan.DataListFull
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseasesCatelogue(
    padding: PaddingValues,
    diseaseDBviewModel: diseaseDBviewModel,
    navController: NavHostController,
    domainSelection: Int
) {
    val selectedDomain= remember {
        mutableStateOf(Int.MAX_VALUE)
    }

    selectedDomain.value= domainSelection

    val isSelected= remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = bottomNavItems.DiseaseCatelogue.title,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 26.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                actions = {
                    /*Image(
                        imageVector = Icons.Outlined.BlurOn,
                        contentDescription = "Show accuracy button",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(24.dp)
                            .clickable {

                            }
                    )*/
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
                    /*Image(
                        imageVector = Icons.Outlined.ArrowBackIosNew,
                        contentDescription = "Show accuracy button",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(24.dp)
                            .padding(2.dp)
                            .clickable {

                            }
                    )*/
                }
            )
        },
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.tertiary
    ){ value ->
        val lists= remember {
            mutableStateOf(emptyList<disease_data_dataClass>())
        }

        LaunchedEffect(key1 = selectedDomain.value) {
            isSelected.value=selectedDomain.value!=Int.MAX_VALUE
            lists.value= if (selectedDomain.value==Int.MAX_VALUE){
                diseaseDBviewModel.readDB()
            } else{
                diseaseDBviewModel.searchByDomain(selectedDomain.value.toString())
            }
        }
        val bomState= remember {
            mutableStateOf(false)
        }

        val listItem= remember {
            mutableStateListOf(
                rbStructure(
                    isChecked = selectedDomain.value==0,
                    title = "Blood & Lymphatics"
                ),
                rbStructure(
                    isChecked = selectedDomain.value==1,
                    title = "Digestive"
                ),
                rbStructure(
                    isChecked = selectedDomain.value==2,
                    title = "Hand & Neck"
                ),
                rbStructure(
                    isChecked = selectedDomain.value==3,
                    title = "Nervous System",
                ),
                rbStructure(
                    isChecked = selectedDomain.value==4,
                    title = "Reproductive System"
                ),
                rbStructure(
                    isChecked = selectedDomain.value==6,
                    title = "Respiratory System"
                ),
                rbStructure(
                    isChecked = selectedDomain.value==8,
                    title = "Skin"
                ),
                rbStructure(
                    isChecked = selectedDomain.value==11,
                    title = "Urinary System"
                )
            )
        }

        if (bomState.value){
            ModalBottomSheet(onDismissRequest = { bomState.value= false }) {
                RadioButtonList(items = listItem, selectedItem = selectedDomain.value) {
                    selectedDomain.value= it
                    bomState.value= false
                }
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
                Spacer(modifier = Modifier.height(24.dp))
                LazyRow(
                    modifier = Modifier
                        .padding(start = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        if (isSelected.value){
                            Text(
                                text = "Clear all",
                                fontFamily = fontFamily,
                                fontSize = 14.sp,
                                color = customBlue,
                                fontWeight = FontWeight(600),
                                modifier = Modifier.clickable {
                                    selectedDomain.value=Int.MAX_VALUE
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        OptionsFilter(
                            text = "Organ System Type",
                            imageVector = Icons.Outlined.ArrowDropDown,
                            onClickListener = {
                                bomState.value= true
                            },
                            state = isSelected.value
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(lists.value){
                DataListFull(
                    imageVector = Icons.Outlined.TipsAndUpdates,
                    title = it.disease_name,
                    colorLogo = MaterialTheme.colorScheme.surface,
                    colorLogoTint = MaterialTheme.colorScheme.onPrimary,
                    onClickListener = {
                        diseaseDBviewModel.saveData(data = it){
                            navController.navigate(
                                // same navigation route used in ScanResults for managing BackStack
                                bottomNavItems.ScanResult.returnScanResIndex(
                                    2,
                                    9999,
                                    1
                                )
                            )
                        }
                    },
                    subtitle = when(it.domain){
                        "0" -> "Blood & Lymphatics"
                        "1" -> "Digestive"
                        "2" -> "Hand & Neck"
                        "3" -> "Nervous System"
                        "4" -> "Reproductive System"
                        "6", "7" -> "Respiratory System"
                        "8", "9" -> "Skin"
                        "11" -> "Urinary System"
                        else -> ""
                    }
                )
            }
        }
    }
}

@Composable
fun OptionsFilter(text: String, imageVector: ImageVector, onClickListener: () -> Unit, state: Boolean) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (state) {
                    Modifier.background(customBlue)
                } else {
                    Modifier.background(MaterialTheme.colorScheme.secondary)
                }
            )
            .clickable { onClickListener() }
            .padding(vertical = 6.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = text,
            fontFamily = fontFamily,
            fontSize = 14.sp,
            color = if (state) Color.White else lightTextAccent
        )
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier
                .size(18.dp),
            tint = if (state) Color.White else lightTextAccent
        )
    }
}

@Composable
fun RadioButtonList(
    items: List<rbStructure>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {

    val itemSelected= remember {
        mutableStateOf(selectedItem)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        Row(
            Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Choose disease category",
                fontSize = 18.sp,
                fontWeight = FontWeight(600),
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Done",
                fontSize = 18.sp,
                fontWeight = FontWeight(600),
                fontFamily = fontFamily,
                color = customBlue,
                modifier = Modifier.clickable { onItemSelected(itemSelected.value) }
            )
        }
        Text(
            text = "Clear all",
            fontSize = 14.sp,
            fontWeight = FontWeight(600),
            fontFamily = fontFamily,
            color = customBlue,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.secondary)
                .clickable { }
                .padding(vertical = 3.dp, horizontal = 14.dp)
        )
        items.forEachIndexed { index, data ->
            Row(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .pointerInput(key1 = true) {
                        detectTapGestures {
                            itemSelected.value= index
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = Icons.Outlined.MedicalInformation,
                    contentDescription = "options pre",
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp))
                        .padding(10.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                )
                Spacer(modifier = Modifier.width(18.dp))
                Text(
                    text = data.title,
                    fontFamily = fontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600)
                )
                Spacer(modifier = Modifier.weight(1f))
                val checkStat= itemSelected.value==index
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .then(
                            if (!checkStat) Modifier.border(
                                width = 1.dp,
                                color = Color(0xFFD4D4D4),
                                shape = CircleShape
                            ) else Modifier
                        )
                ){
                    if (checkStat){
                        Image(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "options",
                            modifier = Modifier
                                .background(customBlue, CircleShape)
                                .padding(3.dp),
                            colorFilter = ColorFilter.tint(color = Color.White)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}