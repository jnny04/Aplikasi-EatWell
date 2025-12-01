package com.example.recappage.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.recappage.R
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.viewmodel.StreakViewModel
import com.example.recappage.ui.theme.SourceSans3
import androidx.compose.material3.MaterialTheme
import androidx.core.content.FileProvider // ✅ Import ini penting
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@Composable
fun ProfileScreen(
    navController: NavHostController,
    streakViewModel: StreakViewModel,
    regViewModel: RegistrationViewModel,
    modifier: Modifier = Modifier
) {
    val serifBold = FontFamily(Font(R.font.source_serif_pro_bold))
    val context = LocalContext.current

    // State untuk menu pop-up
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        streakViewModel.refreshStreak()
    }

    val streakDays by streakViewModel.streakDays.collectAsState()
    LaunchedEffect(Unit) {
        regViewModel.loadUserProfile()
    }
    val isLoading = regViewModel.isLoading.value

    // Image Picker Logic (Galeri)
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    // 🔥 LOGIKA KAMERA BARU 🔥
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val takePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess && tempCameraUri != null) {
            // Jika foto berhasil diambil, set ke imageUri agar tampil di layar
            imageUri = tempCameraUri
        }
    }

    val loadedUrl = regViewModel.profileImageUrl.value
    val scroll = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Header Image
        Image(
            painter = painterResource(id = R.drawable.untitleddesign91),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.TopCenter)
                .shadow(6.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp, start = 16.dp, end = 16.dp, bottom = 90.dp)
                    .verticalScroll(scroll),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Top Bar Text
                Box(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Edit Profile",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp,
                        fontFamily = serifBold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    TextButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text("×", fontSize = 22.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                }

                Spacer(Modifier.height(6.dp))

                // ===========================
                //   PROFILE CARD
                // ===========================
                Box(
                    modifier = Modifier
                        .width(380.dp)
                        .height(285.dp)
                        .shadow(6.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {

                    val backgroundModel = imageUri ?: loadedUrl
                    if (backgroundModel != null) {
                        AsyncImage(
                            model = backgroundModel,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { scaleX = 1.2f; scaleY = 1.2f }
                                .clip(RoundedCornerShape(12.dp))
                                .blur(18.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile_blur),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().graphicsLayer { scaleX = 1.2f; scaleY = 1.2f }.clip(RoundedCornerShape(12.dp))
                        )
                    }

                    // === AVATAR SECTION ===
                    Box(modifier = Modifier.align(Alignment.CenterStart).padding(start = 24.dp)) {

                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(106.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, MaterialTheme.colorScheme.onPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val finalImageSource = imageUri ?: loadedUrl

                                if (finalImageSource != null) {
                                    AsyncImage(
                                        model = finalImageSource,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(100.dp).clip(CircleShape)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        modifier = Modifier.size(100.dp)
                                    )
                                }
                            }
                        }

                        // 🔥 ICON KAMERA & MENU POP-UP
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp)
                                .offset(x = (-4).dp, y = (-4).dp)
                                .clickable { showMenu = true }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.camera_profile),
                                contentDescription = "Change Picture",
                                modifier = Modifier.fillMaxSize()
                            )

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                // OPSI 1: Choose from Gallery
                                DropdownMenuItem(
                                    text = { Text("Choose from Gallery", color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        showMenu = false
                                        pickImage.launch("image/*")
                                    }
                                )

                                // 🔥 OPSI 2: TAKE PHOTO (Sekarang Berfungsi!)
                                DropdownMenuItem(
                                    text = { Text("Take Photo", color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        showMenu = false
                                        // 1. Buat file temporary
                                        val file = context.createImageFile()
                                        // 2. Dapatkan URI aman menggunakan FileProvider
                                        val uri = FileProvider.getUriForFile(
                                            Objects.requireNonNull(context),
                                            context.packageName + ".provider", // Sesuaikan dengan authorities di Manifest
                                            file
                                        )
                                        // 3. Simpan URI ke state dan luncurkan kamera
                                        tempCameraUri = uri
                                        takePhotoLauncher.launch(uri)
                                    }
                                )

                                // Garis Pemisah
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                // OPSI 3: REMOVE PHOTO (Selalu Muncul)
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Remove Photo",
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    // Disable tombol jika tidak ada foto
                                    enabled = (loadedUrl != null || imageUri != null),
                                    onClick = {
                                        showMenu = false
                                        regViewModel.deleteProfilePicture(
                                            onSuccess = {
                                                imageUri = null
                                                Toast.makeText(context, "Foto profil dihapus.", Toast.LENGTH_SHORT).show()
                                            },
                                            onFailure = { errorMsg ->
                                                Toast.makeText(context, "Gagal: $errorMsg", Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Username
                    Text(
                        text = regViewModel.username.value,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontFamily = serifBold,
                        modifier = Modifier.align(Alignment.BottomStart).padding(start = 32.dp, bottom = 50.dp)
                    )

                    // Streak
                    Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 28.dp)) {
                        Image(painter = painterResource(id = R.drawable.streak), contentDescription = null, modifier = Modifier.size(150.dp))
                        Text(text = "$streakDays days", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 20.sp, modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-30).dp))
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ===========================
                //   EDITABLE FIELDS
                // ===========================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    EditableField("Email", regViewModel.email.value) { regViewModel.email.value = it }
                    GenderDropdownField("Gender", regViewModel.gender.value ?: "") { regViewModel.gender.value = it }
                    SuffixEditableField("Age", regViewModel.age.value, "years") { regViewModel.age.value = it }
                    SuffixEditableField("Height", regViewModel.height.value, "cm") { regViewModel.height.value = it }
                    SuffixEditableField("Weight", regViewModel.weight.value, "kg") { regViewModel.weight.value = it }
                }

                Spacer(Modifier.height(30.dp))

                // SAVE BUTTON
                Button(
                    onClick = {
                        regViewModel.updateUserProfile(
                            context = context,
                            imageUri = imageUri,
                            onSuccess = {
                                // JANGAN reset imageUri ke null disini agar foto baru tetap tampil
                                Toast.makeText(context, "Profil berhasil disimpan!", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { errorMsg ->
                                Toast.makeText(context, "Gagal simpan: $errorMsg", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 32.dp)
                ) {
                    Text(
                        "Save Changes",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Delete Account",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(textDecoration = TextDecoration.Underline),
                    modifier = Modifier
                        .clickable {
                            regViewModel.showDeleteDialog.value = true
                        }
                        .padding(8.dp)
                )
                Spacer(Modifier.height(40.dp))
            }
        }

        // Popup Konfirmasi Delete Account
        if (regViewModel.showDeleteDialog.value) {
            AlertDialog(
                onDismissRequest = { regViewModel.showDeleteDialog.value = false },
                title = {
                    Text(text = "Delete Account?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                },
                text = {
                    Text("Are you sure you want to delete your account permanently? This action cannot be undone and all your data will be lost.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            regViewModel.showDeleteDialog.value = false
                            regViewModel.deleteAccount(
                                onSuccess = { navController.navigate("sign_in") { popUpTo(0) } },
                                onFailure = { errorMsg -> Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show() }
                            )
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { regViewModel.showDeleteDialog.value = false }
                    ) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        Component18(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}

// -----------------------------------------------------------------
// KOMPONEN HELPER
// -----------------------------------------------------------------

@Composable
private fun EditableField(
    label: String,
    text: String,
    onChange: (String) -> Unit
) {
    val fontLabel = FontFamily(Font(R.font.source_serif_pro_regular))

    Column {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = fontLabel,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        BasicTextField(
            value = text,
            onValueChange = onChange,
            textStyle = TextStyle(
                fontFamily = SourceSans3,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .padding(vertical = 6.dp)
                .fillMaxWidth()
        )

        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
    }
}

@Composable
private fun SuffixEditableField(
    label: String,
    text: String,
    suffix: String,
    onChange: (String) -> Unit
) {
    val fontLabel = FontFamily(Font(R.font.source_serif_pro_regular))

    Column {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = fontLabel,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            BasicTextField(
                value = text,
                onValueChange = onChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    fontFamily = SourceSans3,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.width(IntrinsicSize.Min)
            )

            Text(
                text = " $suffix",
                style = TextStyle(
                    fontFamily = SourceSans3,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
    }
}

@Composable
private fun GenderDropdownField(
    label: String,
    selectedGender: String,
    onGenderChange: (String) -> Unit
) {
    val fontLabel = FontFamily(Font(R.font.source_serif_pro_regular))
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = fontLabel,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedGender.isNotEmpty()) selectedGender else "Select Gender",
                    style = TextStyle(
                        fontFamily = SourceSans3,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                DropdownMenuItem(
                    text = { Text("Male", fontFamily = SourceSans3, color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onGenderChange("MALE")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Female", fontFamily = SourceSans3, color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onGenderChange("FEMALE")
                        expanded = false
                    }
                )
            }
        }

        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
    }
}

// 🔥 Helper untuk membuat File Gambar Sementara
fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
}