package com.example.recappage.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.recappage.R
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.components.TopBorder


@Composable
fun ScannerPage(navController: NavHostController) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmapPreview by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showResultPopup by remember { mutableStateOf(false) }

    // Buat coroutine scope
    val scope = rememberCoroutineScope()

    // Launcher kamera
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            isLoading = true
            bitmapPreview = bitmap

            // Jalankan delay di coroutine, bukan LaunchedEffect
            scope.launch {
                delay(2000) // simulasi loading 2 detik
                isLoading = false
                showResultPopup = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            Component18(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBorder(navController = navController)

            // Header
            Image(
                painter = painterResource(id = R.drawable.lets_check),
                contentDescription = "Lets Check",
                modifier = Modifier
                    .padding(top = 24.dp, start = 20.dp, bottom = 12.dp)
                    .align(Alignment.Start)
                    .width(240.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Kotak preview foto
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(500.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(color = Color(0xFFFF6600))
                    }

                    bitmapPreview != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(bitmapPreview),
                            contentDescription = "Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    else -> {
                        Text("No Image", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tombol Back, Camera, Retake
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { navController.popBackStack() }
                )

                Image(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Camera",
                    modifier = Modifier
                        .size(80.dp)
                        .clickable { launcher.launch(null) }
                )

                Image(
                    painter = painterResource(id = R.drawable.retake),
                    contentDescription = "Retake",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            bitmapPreview = null
                            imageUri = null
                        }
                )
            }
        }

// POPUP RESULT
        if (showResultPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .wrapContentHeight()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    // Tombol X (close)
                    Image(
                        painter = painterResource(id = R.drawable.x),
                        contentDescription = "Close",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(20.dp)
                            .clickable { showResultPopup = false }
                    )

                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Foto hasil kamera (255.dp)
                        if (bitmapPreview != null) {
                            Image(
                                painter = rememberAsyncImagePainter(bitmapPreview),
                                contentDescription = "Food Result",
                                modifier = Modifier
                                    .height(255.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .height(255.dp)
                                    .fillMaxWidth()
                                    .background(Color.LightGray, RoundedCornerShape(12.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nama makanan (BOLD & besar)
                        Text(
                            text = "Chicken Salad Wrap",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nutrisi (kiri label, kanan value)
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Carbs", fontSize = 14.sp, color = Color.Black)
                                Text("100 g", fontSize = 14.sp, color = Color.Black)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Protein", fontSize = 14.sp, color = Color.Black)
                                Text("100 g", fontSize = 14.sp, color = Color.Black)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Fat", fontSize = 14.sp, color = Color.Black)
                                Text("100 g", fontSize = 14.sp, color = Color.Black)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Total Calories",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    "100 kcal",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Add to today's intake (pakai gambar, dibesarin)
                        Image(
                            painter = painterResource(id = R.drawable.todays_intake),
                            contentDescription = "Add to today's intake",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)   // ✅ lebih besar
                                .clickable {
                                    // TODO: action add to intake
                                }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Retake button (gambar)
                        Image(
                            painter = painterResource(id = R.drawable.retake),
                            contentDescription = "Retake",
                            modifier = Modifier
                                .size(45.dp)
                                .clickable {
                                    bitmapPreview = null
                                    showResultPopup = false
                                    launcher.launch(null)
                                }
                        )
                    }
                }
            }
        }
    }
}