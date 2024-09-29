package com.rohnsha.medbuddyai.screens.scan

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MotionPhotosAuto
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PsychologyAlt
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.outlined.BrowseGallery
import androidx.compose.material.icons.outlined.CenterFocusWeak
import androidx.compose.material.icons.outlined.Compare
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.MotionPhotosAuto
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PsychologyAlt
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.rohnsha.medbuddyai.ContextUtill
import com.rohnsha.medbuddyai.R
import com.rohnsha.medbuddyai.database.userdata.currentUser.currentUserDataVM
import com.rohnsha.medbuddyai.database.userdata.keys.keyVM
import com.rohnsha.medbuddyai.domain.analyzer
import com.rohnsha.medbuddyai.domain.dataclass.classification
import com.rohnsha.medbuddyai.domain.viewmodels.classificationVM
import com.rohnsha.medbuddyai.domain.viewmodels.photoCaptureViewModel
import com.rohnsha.medbuddyai.domain.viewmodels.sideStateVM
import com.rohnsha.medbuddyai.domain.viewmodels.snackBarToggleVM
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.navigation.sidebar.screens.sideBarModifier
import com.rohnsha.medbuddyai.screens.BOMChangeDUser
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private lateinit var viewModelPhotoSave: photoCaptureViewModel
private lateinit var viewModelClassification: classificationVM
private lateinit var isConfirming: MutableState<Boolean>
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    padding: PaddingValues,
    navController: NavHostController,
    photoCaptureVM: photoCaptureViewModel,
    classifierVM: classificationVM,
    index: Int,
    sideStateVM: sideStateVM,
    currentUserDataVM: currentUserDataVM,
    keyVM: keyVM,
    snackBarToggleVM: snackBarToggleVM,
    mode: Int //specifies if user is intended for normal scan [0] or from diseases catelogue [1]
) {
    viewModelPhotoSave = photoCaptureVM
    viewModelClassification = classifierVM

    val context= LocalContext.current
    val sharedPreferences = LocalContext.current.getSharedPreferences("PermissionState", Context.MODE_PRIVATE)
    var permissionBOM by remember { mutableStateOf(false) }
    var isPermissionPermanentlyDenied by remember{ mutableStateOf(
        sharedPreferences.getBoolean("isPermissionDeclined", false)
    ) }

    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ){ isGranted ->
        if (isGranted) {
            sharedPreferences.edit().putBoolean("isPermissionDeclined", false).apply()
            Log.d("ScanScreen", "Camera permission granted")
        } else {
            if (!shouldShowRequestPermissionRationale(
                    context as ComponentActivity,
                    Manifest.permission.CAMERA
                )
            ) {
                // If rationale should not be shown, it means the user has permanently denied the permission
                isPermissionPermanentlyDenied = true
                sharedPreferences.edit().putBoolean("isPermissionDeclined", true).apply()
                Log.d("ScanScreen", "Camera permission permanently denied")
            } else {
                permissionBOM= true
                Log.d("ScanScreen", "Camera permission denied")
            }
        }
    }

    LaunchedEffect(key1 = Unit){
        when{
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> Log.d("ScanScreen", "permission already granted")

            shouldShowRequestPermissionRationale(
                context as ComponentActivity,
                Manifest.permission.CAMERA
            ) -> {
                Log.d("ScanScreen", "show rationale, ask for permission")
                permissionBOM= true
            }

            isPermissionPermanentlyDenied -> {
                permissionBOM= true
            }

            else -> {
                Log.d("ScanScreen", "requesting permission")
                cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    val bomStateDUser= remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Scan",
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
    ) { value ->
        val scope= rememberCoroutineScope()

        if (permissionBOM){
            ThemedModelBottomSheet(
                title = if (isPermissionPermanentlyDenied) "Permanently Declined" else "Permission Request",
                ctaText = "Grant Now",
                onClickListener = {
                    scope.launch {
                        if (isPermissionPermanentlyDenied){
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                            context.startActivity(intent)
                        } else {
                            cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }

                },
                onDismissReq = {
                    permissionBOM= false
                }
            ) {
                Text(
                    modifier = Modifier,
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    text = "In order to scan images directly from your camera, we need you to grant us camera permission."
                )
                if (isPermissionPermanentlyDenied)
                    Text(
                        modifier = Modifier,
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        text = "Seems like you've permanently denied camera permission, we will take you to app settings to enable it"
                    )
            }
        }


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

        Column(
            modifier = Modifier
                .padding(value)
                .padding(padding)
                .padding(top = 20.dp)
                .fillMaxSize()
        ) {
            ScanMainScreen(
                navController,
                index, snackBarToggleVM = snackBarToggleVM, scannMode = mode, photoCaptureVM = photoCaptureVM
            )
        }
    }
}

@Composable
fun ScanOptions() {
    val autoBool= remember {
        mutableStateOf(true)
    }
    val xRayBool= remember {
        mutableStateOf(false)
    }
    val mriBool= remember {
        mutableStateOf(false)
    }
    val skinBool = remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
        ScanOptionsItem(unselectedIcon = Icons.Outlined.MotionPhotosAuto, selectedIcon = Icons.Filled.MotionPhotosAuto, description_icon = "auto", clickAction = {
            autoBool.value= true
            xRayBool.value=false
            mriBool.value= false
            skinBool.value= false
            Log.d(
                "clicked",
                "xray $xRayBool, autobool $autoBool"
            )
        }, enabledState = autoBool, text = "Auto")
        Spacer(modifier = Modifier.width(13.dp))
        ScanOptionsItem(unselectedIcon = Icons.Outlined.CenterFocusWeak, description_icon = "data_array", clickAction = {
            autoBool.value= false
            xRayBool.value=true
            mriBool.value= false
            skinBool.value= false
            Log.d(
                "clicked",
                "xray $xRayBool, autobool $autoBool"
            )
        }, enabledState = xRayBool, text = "X-Ray", selectedIcon = Icons.Filled.CenterFocusStrong)
        Spacer(modifier = Modifier.width(13.dp))
        ScanOptionsItem(unselectedIcon = Icons.Outlined.PsychologyAlt, description_icon = "data_array", clickAction = {
            autoBool.value= false
            xRayBool.value=false
            mriBool.value=true
            skinBool.value=false
            Log.d(
                "clicked",
                "xray $xRayBool, autobool $autoBool"
            )
        }, enabledState = mriBool, text = "MRI", selectedIcon = Icons.Filled.PsychologyAlt)
        Spacer(modifier = Modifier.width(13.dp))
        ScanOptionsItem(unselectedIcon = Icons.Outlined.Person, description_icon = "data_array", clickAction = {
            autoBool.value= false
            xRayBool.value=false
            mriBool.value=false
            skinBool.value=true
            Log.d(
                "clicked",
                "xray $xRayBool, autobool $autoBool"
            )
        }, enabledState = skinBool, text = "Skin Manifestation", selectedIcon = Icons.Filled.Person)
    }
}

@Composable
fun ScanOptionsItem(
    unselectedIcon: ImageVector,
    selectedIcon: ImageVector,
    description_icon: String,
    clickAction: () -> Unit,
    enabledState: MutableState<Boolean>,
    text: String
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = clickAction),
        color = MaterialTheme.colorScheme.primary.copy(0f)
    ) {
        Row(
            modifier = Modifier
                .height(34.dp)
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                )
                .then(
                    if (enabledState.value) Modifier.background(
                        color = Color.White,
                        shape = RoundedCornerShape(6.dp)
                    ) else Modifier
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center
            ){
                Icon(
                    modifier = Modifier
                        .padding(start = 9.dp, end = 3.dp)
                        .height(24.dp)
                        .width(24.dp),
                    imageVector = if (enabledState.value) selectedIcon else unselectedIcon,
                    contentDescription = description_icon
                )
            }
            if (enabledState.value){
                Text(
                    modifier = Modifier
                        .padding(start = 3.dp, end = 9.dp),
                    text = text,
                    fontWeight = FontWeight(600)
                )
            }
        }
    }
}

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier= Modifier,
    imgBitmap: Bitmap? = null,
    isConfirmation: MutableState<Boolean> = isConfirming
) {
    val lifecycleOwner= LocalLifecycleOwner.current
    val coroutineScope= rememberCoroutineScope()
    if (isConfirmation.value){
        if (imgBitmap!=null){
            Image(
                bitmap = imgBitmap.asImageBitmap(),
                contentDescription = "Captured Image",
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
    } else{
        AndroidView(
            factory = {
                PreviewView(it).apply {
                    this.controller= controller
                    controller.bindToLifecycle(lifecycleOwner)

                    // implement touch to focus
                    setOnTouchListener { view, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                view.performClick()
                                true
                            }
                            MotionEvent.ACTION_UP -> {
                                val factory = SurfaceOrientedMeteringPointFactory(
                                    width.toFloat(),
                                    height.toFloat()
                                )
                                val point = factory.createPoint(event.x, event.y)
                                val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                                    .setAutoCancelDuration(3, TimeUnit.SECONDS)
                                    .build()

                                coroutineScope.launch {
                                    controller.cameraControl?.startFocusAndMetering(action)
                                }
                                true
                            }
                            else -> false
                        }
                    }
                }
            },
            modifier = modifier
        )
    }
}

fun takePhoto(
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    toCcamFeed: (classification) -> Unit,
){
    Log.d("successIndexModelTF", "entered")

    var classificationResult: classification = classification(indexNumber = 404, confident = 404f)
    controller.takePicture(
        ContextCompat.getMainExecutor(ContextUtill.ContextUtils.getApplicationContext()),
        object : OnImageCapturedCallback(){
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                Log.d("successIndexModelTF", "entered1")

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoTaken(rotatedBitmap)
                toCcamFeed(classificationResult)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)

                Log.d("successIndexModelTF", "jii $exception", )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanMainScreen(
    navController: NavHostController,
    index: Int,
    snackBarToggleVM: snackBarToggleVM,
    photoCaptureVM: photoCaptureViewModel,
    scannMode: Int
) {
    val bomExample= remember {
        mutableStateOf(false)
    }
    val conttext= LocalContext.current
    var itt= classification(0, 6f)
    val detecteddClassification= remember {
        mutableStateOf(itt.indexNumber)
    }
    val flashLightMode= remember {
        mutableStateOf(FlashlightMode.Off)
    }
    Log.d("classificationCLassifier", detecteddClassification.value.toString())
   val errorText= remember {
       mutableStateOf("")
   }
    errorText.value = if (detecteddClassification.value == 0) {
        when (index) {
            6, 7 -> "chest xray"
            3 -> "brain mri"
            8, 9 -> "skin photo"
            0, 1, 2, 4, 5 -> "biopsy scan"
            else -> "scan report"
        }
    } else {
        "Go ahead"
    }
    val scope= rememberCoroutineScope()
    val analyzer= remember {
        analyzer(
            context = conttext,
            onResults = {
                detecteddClassification.value= it.indexNumber
            },
            index = index
        )
    }
    val controller= remember {
        LifecycleCameraController(ContextUtill.ContextUtils.getApplicationContext()).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                CameraController.IMAGE_ANALYSIS
            )
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(ContextUtill.ContextUtils.getApplicationContext()),
                analyzer
            )
        }
    }
    when(flashLightMode.value){
        FlashlightMode.On -> controller.cameraControl?.enableTorch(true)
        FlashlightMode.Off -> controller.cameraControl?.enableTorch(false)
    }
    val isPredictingBool= remember {
        mutableStateOf(false)
    }

    isConfirming = remember {
        mutableStateOf(false)
    }

    val bomError= rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        val imgBit= photoCaptureVM.bitmaps.collectAsState().value?.asImageBitmap()
        if (bomExample.value && imgBit!=null){
            ModalBottomSheet(
                onDismissRequest = { bomExample.value= false },
            ) {
                Column {
                    Row {
                        Text(
                            modifier = Modifier
                                .padding(start = 30.dp),
                            text = "Expected vs Input Image",
                            fontSize = 19.sp,
                            fontWeight = FontWeight(600),
                            fontFamily = fontFamily
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            modifier = Modifier
                                .clickable {
                                    bomExample.value = false
                                }
                                .padding(end = 30.dp),
                            text = "Close",
                            fontSize = 17.sp,
                            color = customBlue,
                            fontWeight = FontWeight(600),
                            fontFamily = fontFamily
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Image(
                            painter = painterResource(
                                id = when(index){
                                    0 -> R.drawable.lymph
                                    1 -> R.drawable.colon
                                    2 -> R.drawable.oral
                                    3 -> R.drawable.brain
                                    4, 5 -> R.drawable.breast
                                    6, 7 -> R.drawable.chest_xray
                                    8, 9 -> R.drawable.skin
                                    11 -> R.drawable.kidney
                                    else -> R.drawable.logo_welcme
                                }
                            ),
                            contentScale = ContentScale.Crop,
                            contentDescription = "null",
                            modifier = Modifier
                                .weight(0.5f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Transparent)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Image(
                            bitmap = imgBit,
                            contentScale = ContentScale.Crop,
                            contentDescription = "null",
                            modifier = Modifier
                                .weight(0.5f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Transparent)
                        )
                    }
                    Spacer(modifier = Modifier.height(45.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(topEnd = 8.dp, topStart = 8.dp))
                .fillMaxHeight(.85f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            CameraPreview(
                controller = controller,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    // implements touch-to-zoom
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            scope.launch {
                                val currentZoom = controller.zoomState.value?.zoomRatio ?: 1f
                                val newZoom = currentZoom * zoom
                                controller.setZoomRatio(newZoom.coerceIn(1f, controller.cameraInfo?.zoomState?.value?.maxZoomRatio ?: 1f))
                            }
                        }
                    },
                imgBitmap = viewModelPhotoSave.bitmaps.collectAsState().value,
            )

            CameraPreviewSegmentOp(
                title = if (detecteddClassification.value == 0) "Works best with " else "Expected image found, ",
                dataItem = errorText.value
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(horizontal = 24.dp)
                .padding(top = 12.dp, bottom = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (!isConfirming.value) {
                    when(flashLightMode.value){
                        FlashlightMode.Off -> {
                            flashLightMode.value = FlashlightMode.On
                        }
                        FlashlightMode.On -> {
                            flashLightMode.value = FlashlightMode.Off
                        }
                    }
                } else {
                    isConfirming.value = false
                }
            }) {
                Icon(
                    imageVector = if (!isConfirming.value){
                        when(flashLightMode.value){
                            FlashlightMode.Off -> Icons.Outlined.FlashOff
                            FlashlightMode.On -> Icons.Filled.FlashOn
                        }
                    } else Icons.Outlined.RestartAlt,
                    contentDescription = if (!isConfirming.value) "Show accuracy button" else "Rescan",
                    tint= MaterialTheme.colorScheme.onPrimary
                )
            }
            val bitmapImg= viewModelPhotoSave.bitmaps.collectAsState().value
            Button(
                onClick = {
                    if (!isConfirming.value){
                        isPredictingBool.value= true
                        scope.launch {
                            takePhoto(
                                controller = controller,
                                onPhotoTaken = viewModelPhotoSave::onTakePhoto,
                                toCcamFeed = {
                                    Log.d("checkConfirmation", "clicked")
                                    isConfirming.value= true
                                }
                            )
                        }
                        isPredictingBool.value= false
                    } else {
                        scope.launch {
                            if (bitmapImg != null) {
                                isPredictingBool.value= true
                                delay(600L)
                                val branchClassification = viewModelClassification.classify(
                                    conttext, bitmapImg, scanOption = 999, index = index)[0]
                                Log.d("bitmapResults", branchClassification.toString())
                                if (branchClassification.indexNumber==0){
                                    bomError.value=true
                                } else {
                                    //navController.navigate(bottomNavItems.ScanResult.returnScanResIndex(0, index = index))
                                    navController.navigate(bottomNavItems.ScanQA.returnScanIndex(index = index, mode = scannMode))
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = customBlue,
                    contentColor = Color.White
                )
            ){
                Text(
                    text = if (!isConfirming.value) "Capture" else "Proceed",
                    fontSize = 16.sp,
                    fontWeight = FontWeight(600),
                    fontFamily = fontFamily
                )
            }
            if (!isConfirming.value){
                val imageURI = remember {
                    mutableStateOf<Uri?>(null)
                }
                val bitmap = remember {
                    mutableStateOf<Bitmap?>(null)
                }
                val getImageFromGallery = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()){
                    imageURI.value=it
                }
                imageURI.value.let {
                    if (Build.VERSION.SDK_INT < 28) {
                        bitmap.value = MediaStore.Images
                            .Media.getBitmap(conttext.contentResolver, it)
                    } else {
                        val source =
                            it?.let { it1 ->
                                ImageDecoder.createSource(
                                    conttext.contentResolver,
                                    it1
                                )
                            }
                        bitmap.value = source?.let { it1 -> ImageDecoder.decodeBitmap(it1) }
                    }
                    val convertedBitmap = bitmap.value?.copy(Bitmap.Config.ARGB_8888, false)
                    convertedBitmap?.let { it1 ->
                        viewModelPhotoSave.onTakePhoto(it1)
                        isConfirming.value = true
                        errorText.value = if (viewModelClassification.classify(
                                context = conttext,
                                it1,
                                scanOption = 999, index = index
                            )[0].indexNumber == 0
                        ) {
                            when (index) {
                                6, 7 -> "chest xray"
                                3 -> "brain mri"
                                8, 9 -> "skin photo"
                                0, 1, 2, 4, 5 -> "biopsy scan"
                                else -> "scan report"
                            }
                        } else {
                            "Go ahead"
                        }
                    }
                }
                IconButton(onClick = { getImageFromGallery.launch("image/*") }) {
                    Icon(
                        imageVector = Icons.Outlined.BrowseGallery,
                        contentDescription = "import from gallery",
                        tint= MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                IconButton(onClick = {
                    bomExample.value= true
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Compare,
                        contentDescription = "import from gallery",
                        tint= MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            if (bomError.value){
                ModalBottomSheet(onDismissRequest = {
                    isConfirming.value= false
                    bomError.value= false
                    isPredictingBool.value= false
                }) {
                    Row {
                        Text(
                            modifier = Modifier
                                .padding(start = 30.dp),
                            text = "Type Error",
                            fontSize = 19.sp,
                            fontWeight = FontWeight(600),
                            fontFamily = fontFamily
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            modifier = Modifier
                                .clickable {
                                    isConfirming.value = false
                                    bomError.value = false
                                    isPredictingBool.value = false
                                }
                                .padding(end = 30.dp),
                            text = "Rescan",
                            fontSize = 17.sp,
                            color = customBlue,
                            fontWeight = FontWeight(600),
                            fontFamily = fontFamily
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 30.dp),
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        text = "The image you uploaded didn't matched to any of the currently supported image types."
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 30.dp, top = 14.dp, end = 30.dp, bottom = 45.dp),
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        text = "Please upload correct image type to proceed or rescan?"
                    )
                }
            }
        }
    }
}

enum class FlashlightMode {
    Off,
    On
}

@Composable
fun CameraPreviewSegmentOp(
    title: String,
    dataItem: String
) {
    Row(
        modifier = Modifier
            .padding(bottom = 6.dp, top = 20.dp)
            .height(30.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontFamily = fontFamily,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = dataItem,
            fontFamily = fontFamily,
            fontWeight = FontWeight(600),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemedModelBottomSheet(
    title: String,
    ctaText: String? = null,
    onClickListener: (() -> Unit)? = null,
    onDismissReq: () -> Unit,
    contents: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissReq
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 30.dp)
        ) {
            Row {
                Text(
                    modifier = Modifier,
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight(600),
                    fontFamily = fontFamily
                )
                Spacer(modifier = Modifier.weight(1f))
                if (ctaText != null && onClickListener !=null) {
                    Text(
                        modifier = Modifier
                            .clickable {
                                onClickListener()
                            },
                        text = ctaText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight(600),
                        fontFamily = fontFamily,
                        color = customBlue
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            contents()
            Spacer(modifier = Modifier.height(45.dp))
        }
    }
}