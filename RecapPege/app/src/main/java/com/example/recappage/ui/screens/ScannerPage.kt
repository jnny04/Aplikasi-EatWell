package com.example.recappage.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.recappage.R
// Import Popup
import com.example.recappage.ui.components.DetectionResultPopup
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.components.TopBorder
import com.example.recappage.ui.theme.SourceSans3
import com.example.recappage.ui.theme.SourceSerifPro
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.viewmodel.ScanFoodViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme // 👈 WAJIB IMPORT INI

enum class ScannerUiState {
    Idle, Camera, Loading, Result
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerPage(
    navController: NavHostController,
    viewModel: ScanFoodViewModel = hiltViewModel(),
    regViewModel: RegistrationViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) { regViewModel.loadUserProfile() }
    val photoUrl by regViewModel.profileImageUrl
    var currentUiState by remember { mutableStateOf(ScannerUiState.Idle) }
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    val isLoading by viewModel.isLoading
    val result by viewModel.detectionResult
    val error by viewModel.errorMessage

    // LOGIKA STATE
    LaunchedEffect(isLoading, result, error) {
        if (!isLoading && error != null) {
            Toast.makeText(context, "Gagal: $error", Toast.LENGTH_LONG).show()
        }
        currentUiState = when {
            isLoading -> ScannerUiState.Loading
            result != null -> ScannerUiState.Result
            else -> ScannerUiState.Idle
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    viewModel.scanImage(bitmap)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            if (currentUiState == ScannerUiState.Idle || currentUiState == ScannerUiState.Result || currentUiState == ScannerUiState.Loading) {
                TopBorder(navController = navController, showProfile = true, photoUrl = photoUrl)
            }
        },
        bottomBar = {
            if (currentUiState == ScannerUiState.Idle || currentUiState == ScannerUiState.Result || currentUiState == ScannerUiState.Loading) {
                Component18(navController = navController)
            }
        },
        // 🔥 GANTI: containerColor = Color.White → MaterialTheme.colorScheme.background
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            // LAYER 1: TAMPILAN UTAMA (IDLE)
            if (currentUiState != ScannerUiState.Camera) {
                ScannerIdleContent(
                    paddingValues = paddingValues,
                    navController = navController,
                    onGalleryClick = { galleryLauncher.launch("image/*") },
                    onCameraClick = {
                        if (permissionState.status.isGranted)
                            currentUiState = ScannerUiState.Camera
                        else permissionState.launchPermissionRequest()
                    }
                )
            }

            // LAYER 2: OVERLAYS
            if (currentUiState == ScannerUiState.Camera) {
                CameraView({ bmp -> viewModel.scanImage(bmp) }, { currentUiState = ScannerUiState.Idle })
            }

            if (currentUiState == ScannerUiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        // 🔥 LOADING: background warna gelap transparan
                        .background(Color.Black.copy(alpha = 0.3f))
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    // 🔥 LOADING: color = Color(0xFFFC7100) → primary
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            if (currentUiState == ScannerUiState.Result) {
                result?.let { item ->
                    DetectionResultPopup(
                        item = item,
                        onDismiss = { viewModel.resetState() },
                        onAddClick = {
                            viewModel.saveResultToDiary(item)
                            navController.popBackStack()
                        },
                        onRetakeClick = { viewModel.resetState() }
                    )
                }
            }
        }
    }
}

// 🔥 KONTEN UTAMA SCANNER
@Composable
fun ScannerIdleContent(
    paddingValues: PaddingValues,
    navController: NavHostController,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    // 🔥 Peta warna hijau brand
    val brandGreen = MaterialTheme.colorScheme.primary // Menggunakan primary yang Anda set ke Orange/Hijau
    val brandGreenAlpha = brandGreen.copy(alpha = 0.4f)
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            // 🔥 GANTI: background(Color.White) → background(MaterialTheme.colorScheme.background)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Let’s check what you’re eating…",
            fontFamily = SourceSerifPro,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            // 🔥 GANTI: color = Color(0xFF5CA135) → brandGreen
            color = brandGreen,
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // BOX GALERI
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .padding(horizontal = 16.dp)
                .border(
                    width = 2.dp,
                    // 🔥 GANTI: color = Color(0xFF5CA135).copy(alpha = 0.4f) → brandGreenAlpha
                    color = brandGreenAlpha,
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
                .clickable { onGalleryClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    // 🔥 GANTI: tint = Color(0xFF5CA135).copy(alpha = 0.4f) → brandGreenAlpha
                    tint = brandGreenAlpha
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Judul Besar
                Text(
                    text = "AI FOOD SCANNER",
                    textAlign = TextAlign.Center,
                    fontFamily = SourceSans3,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    // 🔥 GANTI: color = Color(0xFF5CA135) → brandGreen
                    color = brandGreen,
                    letterSpacing = 2.sp
                )

                // 2. Sub-judul Kecil
                Text(
                    text = "(Tap to Upload)",
                    textAlign = TextAlign.Center,
                    fontFamily = SourceSans3,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    // 🔥 GANTI: color = Color(0xFF5CA135) → brandGreen
                    color = brandGreen,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        InfoNote()
        Spacer(modifier = Modifier.height(4.dp))

        // AREA TOMBOL BAWAH (BACK, CAMERA, LOG)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            // 1. Tombol Back (Kiri)
            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.backbutton),
                    contentDescription = null,
                    // 🔥 GANTI: tint = Color(0xFF5CA135) → brandGreen
                    tint = brandGreen,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { navController.popBackStack() }
                )
                // 🔥 GANTI: color = Color(0xFF5CA135) → brandGreen
                Text("Back", color = brandGreen, fontSize = 11.sp, fontFamily = SourceSans3)
            }

            // 2. TOMBOL KAMERA (SOLUSI FINAL UKURAN)
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .wrapContentSize()
                    .clickable { onCameraClick() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "camera",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.requiredSize(90.dp)
                )
            }

            // 3. Tombol Log Manually (Kanan)
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                Button(
                    onClick = { navController.navigate("intake_detail") },
                    modifier = Modifier
                        .height(40.dp)
                        .widthIn(min = 90.dp),
                    // 🔥 GANTI: containerColor = Color(0xFF5CA135) → brandGreen
                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Log manually",
                        color = Color.White,
                        fontFamily = SourceSans3,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// Camera Helper
@Composable
fun CameraView(onImageCaptured: (Bitmap) -> Unit, onClose: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
                val executor = ContextCompat.getMainExecutor(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                    imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
                    } catch (e: Exception) { Log.e("CameraX", "Bind failed", e) }
                }, executor)
                previewView
            }, modifier = Modifier.fillMaxSize()
        )
        IconButton(onClick = onClose, modifier = Modifier.align(Alignment.TopStart).padding(16.dp).statusBarsPadding()) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(32.dp))
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 50.dp).size(80.dp).clip(CircleShape).background(Color.White).clickable {
            scope.launch { captureImage(context, imageCapture, onImageCaptured) }
        }.padding(5.dp)) {
            // 🔥 GANTI: background(Color(0xFFFC7100)) → primary
            Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(MaterialTheme.colorScheme.primary))
        }
    }
}

private fun captureImage(context: Context, imageCapture: ImageCapture?, onImageCaptured: (Bitmap) -> Unit) {
    val executor = ContextCompat.getMainExecutor(context)
    imageCapture?.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val rotation = image.imageInfo.rotationDegrees
            if (rotation != 0) {
                val matrix = Matrix()
                matrix.postRotate(rotation.toFloat())
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }
            onImageCaptured(bitmap)
            image.close()
        }
        override fun onError(exception: ImageCaptureException) {}
    })
}

@Composable
fun InfoNote() {
    // 🔥 Peta warna abu-abu sekunder
    val secondaryGray = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(20.dp).border(
                1.4.dp,
                // 🔥 GANTI: Color(0xFF8C8C8C) → secondaryGray
                secondaryGray,
                CircleShape
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "!",
                // 🔥 GANTI: Color(0xFF8C8C8C) → secondaryGray
                color = secondaryGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.offset(y = (-1).dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            "Note: Calorie values shown are estimates, meant to help you track your intake easily.",
            fontSize = 10.sp,
            fontFamily = SourceSans3,
            // 🔥 GANTI: color = Color.Gray → secondaryGray
            color = secondaryGray,
            lineHeight = 12.sp,
            modifier = Modifier.padding(top = 1.dp)
        )
    }
}