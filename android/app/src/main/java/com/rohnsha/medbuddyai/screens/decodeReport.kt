package com.rohnsha.medbuddyai.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.outlined.BrowseGallery
import androidx.compose.material.icons.outlined.Compare
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.HelpOutline
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.ContextUtill
import com.rohnsha.medbuddyai.database.userdata.currentUser.currentUserDataVM
import com.rohnsha.medbuddyai.database.userdata.keys.keyDC
import com.rohnsha.medbuddyai.database.userdata.keys.keyVM
import com.rohnsha.medbuddyai.domain.viewmodels.DeocdeReportVM
import com.rohnsha.medbuddyai.navigation.bottombar.bottomNavItems
import com.rohnsha.medbuddyai.navigation.sidebar.screens.sideBarModifier
import com.rohnsha.medbuddyai.screens.scan.CameraPreview
import com.rohnsha.medbuddyai.screens.scan.CameraPreviewSegmentOp
import com.rohnsha.medbuddyai.screens.scan.FlashlightMode
import com.rohnsha.medbuddyai.screens.scan.ThemedModelBottomSheet
import com.rohnsha.medbuddyai.screens.scan.takePhoto
import com.rohnsha.medbuddyai.ui.theme.customBlue
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private lateinit var isConfirming: MutableState<Boolean>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecodeReportUI(
    navController: NavHostController,
    currentUserDataVM: currentUserDataVM,
    keyVM: keyVM,
    viewModelPhotoSave: DeocdeReportVM
) {
    val scope= rememberCoroutineScope()
    val context= LocalContext.current
    val sharedPreferences = LocalContext.current.getSharedPreferences("PermissionState", Context.MODE_PRIVATE)

    var permissionBOM by remember { mutableStateOf(false) }
    var isPermissionPermanentlyDenied by remember{ mutableStateOf(
        sharedPreferences.getBoolean("isPermissionDeclined", false)
    ) }

    isConfirming = remember {
        mutableStateOf(false)
    }

    val defaultService= remember {
        mutableStateOf(keyDC("", ""))
    }
    LaunchedEffect(true) {
        val keypairs= keyVM.getKeySecretPairs()
        val swasthaiEngine = keypairs.find { it.serviceName == "swasthai" }
        defaultService.value = swasthaiEngine ?: keypairs.firstOrNull() ?: defaultService.value
    }

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
                        text = "Decode Report",
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
                        //bomStateDUser.value = true
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
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.tertiary
    ){ value->
        if (defaultService.value.serviceName==""){
            var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
            ThemedModelBottomSheet(
                title = "Add API key to proceed",
                onDismissReq = {},
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("In order to use decode report feature, we need you to add ")
                        pushStringAnnotation(
                            tag = "URL",
                            annotation = "login"
                        )
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("third-party api keys")
                        }
                        append(".")
                    },
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 14.sp
                    ),
                    onTextLayout = { textLayoutResult = it },
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures { offset ->
                            textLayoutResult?.let { layoutResult ->
                                val position = layoutResult.getOffsetForPosition(offset)
                                layoutResult.getLineForOffset(position).let { line ->
                                    layoutResult.getLineStart(line)
                                    layoutResult.getLineEnd(line)
                                    val annotations = layoutResult.layoutInput.text
                                        .getStringAnnotations(layoutResult.getLineStart(line), layoutResult.getLineEnd(line))
                                    annotations.firstOrNull { it.start <= position && it.end >= position }?.let { annotation ->
                                        if (annotation.tag == "URL" && annotation.item == "login") {
                                            navController.navigate(
                                                bottomNavItems.ApiScreen.route
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
                Text(
                    modifier = Modifier,
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    text = "API keys are stored on device and are not synced or stored on cloud!"
                )
            }
        }

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
                //.padding(padding)
                .padding(top = 20.dp)
                .fillMaxSize()
        ){
            val flashLightMode= remember {
                mutableStateOf(FlashlightMode.Off)
            }
            val controller= remember {
                LifecycleCameraController(ContextUtill.ContextUtils.getApplicationContext()).apply {
                    setEnabledUseCases(
                        CameraController.IMAGE_CAPTURE
                    )
                }
            }
            when(flashLightMode.value){
                FlashlightMode.On -> controller.cameraControl?.enableTorch(true)
                FlashlightMode.Off -> controller.cameraControl?.enableTorch(false)
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
                    isConfirmation = isConfirming,
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
                    imgBitmap = viewModelPhotoSave.bitmap.collectAsState().value,
                )

                CameraPreviewSegmentOp(
                    title = "Works best with ",
                    dataItem = "Pathological Report"
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
            ){
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
                val bitmapImg= viewModelPhotoSave.bitmap.collectAsState().value
                Button(
                    onClick = {
                        if (!isConfirming.value){
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
                        } else {
                            scope.launch {
                                viewModelPhotoSave.decode()
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
                                .Media.getBitmap(context.contentResolver, it)
                        } else {
                            val source =
                                it?.let { it1 ->
                                    ImageDecoder.createSource(
                                        context.contentResolver,
                                        it1
                                    )
                                }
                            bitmap.value = source?.let { it1 -> ImageDecoder.decodeBitmap(it1) }
                        }
                        val convertedBitmap = bitmap.value?.copy(Bitmap.Config.ARGB_8888, false)
                        convertedBitmap?.let { it1 ->
                            viewModelPhotoSave.onTakePhoto(it1)
                            isConfirming.value = true
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
                        //bomExample.value= true
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Compare,
                            contentDescription = "import from gallery",
                            tint= MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}