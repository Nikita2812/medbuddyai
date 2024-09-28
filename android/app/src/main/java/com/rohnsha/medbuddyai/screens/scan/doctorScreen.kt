package com.rohnsha.medbuddyai.screens.scan

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.EditLocation
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MedicalInformation
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.database.appData.disease.diseaseDBviewModel
import com.rohnsha.medbuddyai.database.appData.doctors.doctor
import com.rohnsha.medbuddyai.domain.dataclass.rbStructure
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.screens.OptionsFilter
import com.rohnsha.medbuddyai.screens.SymptomsList
import com.rohnsha.medbuddyai.ui.theme.ViewDash
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.customGrey
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorScreen(
    padding: PaddingValues,
    diseaseDBviewModel: diseaseDBviewModel,
    domain: Int,
    navController: NavHostController
) {
    Log.d("selectedDep", "domain: $domain")
    val dept= remember {
        mutableStateOf(
            returnSpeciality(domain = domain)
        )
    }
    val location= remember {
        mutableStateOf("")
    }
    val lockedDept= returnSpeciality(domain = domain)

    Log.d("selectedDep", "dept: $dept")
    Log.d("selectedDep", "lockedDept: $lockedDept")

    val doctorList= remember {
        mutableStateListOf<doctor>()
    }

    val isSelected= remember {
        mutableStateOf(false)
    }
    isSelected.value= dept.value!=lockedDept
    val uniqueDept= remember {
        mutableStateListOf<rbStructure>()
    }

    LaunchedEffect(key1 = dept.value) {
        doctorList.clear()
        val data= diseaseDBviewModel.queryDoctors(speciality = dept.value)
        data.forEach { doctorList.add(it) }
    }

    Log.d("selectedDep", "Dep: $dept")

    LaunchedEffect(key1 = true) {
        val uniqueDeptData= diseaseDBviewModel.getUniqueDeptList()
        Log.d("selectedDep", "uniqueDeptData: $uniqueDeptData")
        uniqueDeptData.forEachIndexed { _, s ->
            uniqueDept.add(
                rbStructure(
                    isChecked = dept.value==s,
                    title = s
                )
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Doctors Nearby",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 26.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                actions = {
                    if (location.value != ""){
                        IconButton(onClick = { location.value= "" }) {
                            Icon(imageVector = Icons.Outlined.EditLocation, contentDescription = "location button")
                        }
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
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.tertiary
    ){ value ->

        val bomState = remember {
            mutableStateOf(false)
        }
        val selectedDomain= remember {
            mutableIntStateOf(Int.MAX_VALUE)
        }
        val locationSupported= remember {
            mutableStateListOf(
                "Kolkata"
            )
        }
        val scope= rememberCoroutineScope()

        val context= LocalContext.current

        if (bomState.value){
            ModalBottomSheet(onDismissRequest = { bomState.value= false }) {
                BOMDoctorFilter(
                    items = uniqueDept,
                    selectedDept = dept.value,
                    onItemSelected = {
                        dept.value= it
                        bomState.value= false
                    }
                )
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
            }
            if (location.value == ""){
                item {
                    Text(
                        text = "Select location to proceed",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 15.sp,
                        modifier = Modifier
                            .padding(bottom = 12.dp, start = 24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                items(locationSupported){
                    Box(modifier = Modifier.padding(horizontal = 24.dp)){
                        SymptomsList(title = it, onClickListener = {
                            location.value= it
                        })
                    }
                }
            } else {
                item {
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
                                        dept.value= returnSpeciality(domain)
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            OptionsFilter(
                                text = "Department",
                                imageVector = Icons.Outlined.ArrowDropDown,
                                onClickListener = {
                                    bomState.value= true
                                },
                                state = isSelected.value
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Showing ${dept.value} in ${location.value}",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(bottom = 6.dp, start = 24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                items(doctorList){
                    DataListFull(
                        imageVector = Icons.Outlined.Link,
                        title = it.name,
                        subtitle = "${extractYears(it.experience)}+ years",
                        additionalDataColor = lightTextAccent,
                        colorLogo = MaterialTheme.colorScheme.surface,
                        data = it.pricing,
                        additionData = it.area,
                        colorLogoTint = customGrey,
                        onClickListener = {
                            scope.launch {
                                navController.navigate(bottomNavItems.webUIScreen.returnDocURL(it.link))
                            }
                        }
                    )
                }
            }
        }
    }

}

fun extractYears(input: String): Int? {
    val regex = Regex("""(\d+)\s+years""")
    val matchResult = regex.find(input)
    return matchResult?.groups?.get(1)?.value?.toInt()
}

@Composable
fun BOMDoctorFilter(
    items: List<rbStructure>,
    selectedDept: String,
    onItemSelected: (String) -> Unit
) {

    val selectedDep= remember {
        mutableStateOf(selectedDept)
    }

    Log.d("selectedDep", "selectedDep: $selectedDep")

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        item {
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
                    modifier = Modifier.clickable { onItemSelected(selectedDep.value) }
                )
            }
        }
        item {
            Text(
                text = "Clear all",
                fontSize = 14.sp,
                fontWeight = FontWeight(600),
                fontFamily = fontFamily,
                color = customBlue,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ViewDash)
                    .clickable { }
                    .padding(vertical = 3.dp, horizontal = 14.dp)
            )
        }
        items(items){ data ->
            Row(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .pointerInput(key1 = true) {
                        detectTapGestures {
                            selectedDep.value = data.title
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = Icons.Outlined.MedicalInformation,
                    contentDescription = "options pre",
                    modifier = Modifier
                        .size(40.dp)
                        .background(ViewDash, RoundedCornerShape(16.dp))
                        .padding(10.dp)
                )
                Spacer(modifier = Modifier.width(18.dp))
                Text(
                    text = if (data.title!="") data.title else "Unspecified",
                    fontFamily = fontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600)
                )
                Spacer(modifier = Modifier.weight(1f))
                val checkStat= selectedDep.value==data.title
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
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

}

private fun returnSpeciality(domain: Int): String {
    return when(domain){
        0 -> "Hematologist"
        1 -> "Gastroenterologist"
        2 -> "Head and Neck Surgeon"
        3 -> "Neurosurgeon"
        4 -> "Oncologist"
        5 -> "Gynecologist/Obstetrician"
        7 -> "Pulmonologist"
        8, 9 -> "Dermatologist"
        11 -> "Urologist"
        else -> "General Physician"
    }
}