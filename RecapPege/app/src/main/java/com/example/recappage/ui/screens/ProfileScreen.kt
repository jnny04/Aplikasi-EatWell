package com.example.recappage.ui.screens

import android.net.Uri
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.recappage.R
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.viewmodel.StreakViewModel
import com.example.recappage.ui.theme.SourceSans3

@Composable
fun ProfileScreen(
    navController: NavHostController,
    streakViewModel: StreakViewModel,
    regViewModel: RegistrationViewModel,
    modifier: Modifier = Modifier
) {
    val serifBold = FontFamily(Font(R.font.source_serif_pro_bold))
    val context = LocalContext.current

    // Load data
    val streakDays by streakViewModel.streakDays.collectAsState()
    LaunchedEffect(Unit) {
        regViewModel.loadUserProfile()
    }
    val isLoading = regViewModel.isLoading.value

    // Image Picker Logic
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }
    val loadedUrl = regViewModel.profileImageUrl.value

    val scroll = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // ✅ Dinamis
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
                CircularProgressIndicator(color = Color(0xFF5CA135))
            }
        } else {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp, start = 16.dp, end = 16.dp, bottom = 90.dp)
                    .verticalScroll(scroll),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Top Bar
                Box(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Edit Profile",
                        color = Color(0xFF5CA135),
                        fontSize = 20.sp,
                        fontFamily = serifBold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    TextButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text("×", fontSize = 22.sp, color = Color(0xFF555555))
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
                        .background(MaterialTheme.colorScheme.surface) // ✅ Dinamis
                ) {

                    // Background Blur Logic
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

                    // Avatar
                    Box(modifier = Modifier.align(Alignment.CenterStart).padding(start = 24.dp)) {
                        Box(
                            modifier = Modifier.size(110.dp).clip(CircleShape).border(2.dp, Color(0xFFE0E0E0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier.size(106.dp).clip(CircleShape).border(3.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (imageUri != null) {
                                    AsyncImage(model = imageUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(100.dp).clip(CircleShape))
                                } else if (loadedUrl != null) {
                                    AsyncImage(model = loadedUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(100.dp).clip(CircleShape))
                                } else {
                                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(100.dp))
                                }
                            }
                        }
                        Image(
                            painter = painterResource(id = R.drawable.camera_profile),
                            contentDescription = "Change Picture",
                            modifier = Modifier.align(Alignment.BottomEnd).size(36.dp).offset(x = (-4).dp, y = (-4).dp).clickable { pickImage.launch("image/*") }
                        )
                    }

                    // Username
                    Text(
                        text = regViewModel.username.value,
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontFamily = serifBold,
                        modifier = Modifier.align(Alignment.BottomStart).padding(start = 32.dp, bottom = 50.dp)
                    )

                    // Streak
                    Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 28.dp)) {
                        Image(painter = painterResource(id = R.drawable.streak), contentDescription = null, modifier = Modifier.size(150.dp))
                        Text(text = "$streakDays days", color = Color(0xFF555555), fontSize = 20.sp, modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-30).dp))
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
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    // 1. Email (Input Biasa)
                    EditableField("Email", regViewModel.email.value) { regViewModel.email.value = it }

                    // 2. Gender (Dropdown: Male/Female)
                    GenderDropdownField("Gender", regViewModel.gender.value ?: "") { regViewModel.gender.value = it }

                    // 3. Age (Suffix: years, Number Only)
                    SuffixEditableField("Age", regViewModel.age.value, "years") { regViewModel.age.value = it }

                    // 4. Height (Suffix: cm, Number Only)
                    SuffixEditableField("Height", regViewModel.height.value, "cm") { regViewModel.height.value = it }

                    // 5. Weight (Suffix: kg, Number Only)
                    SuffixEditableField("Weight", regViewModel.weight.value, "kg") { regViewModel.weight.value = it }
                }

                Spacer(Modifier.height(20.dp))

                // ===========================
                //   SAVE BUTTON
                // ===========================
                Button(
                    onClick = {
                        regViewModel.updateUserProfile(
                            context = context,
                            imageUri = imageUri,
                            onSuccess = {
                                println("Profile updated successfully!")
                                imageUri = null
                            },
                            onFailure = { errorMsg ->
                                println("Error: $errorMsg")
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CA135))
                ) {
                    Text("Save Changes", color = Color.White, fontSize = 16.sp)
                }
            }
        }

        Component18(
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}

// -----------------------------------------------------------------
// 1. KOMPONEN BIASA (Untuk Email) - Font Source Sans 3
// -----------------------------------------------------------------
@Composable
private fun EditableField(
    label: String,
    text: String,
    onChange: (String) -> Unit
) {
    val fontLabel = FontFamily(Font(R.font.source_serif_pro_regular))
    // Tidak perlu 'val fontInput' manual lagi

    Column {
        Text(
            text = label,
            color = Color(0xFF2E7D32),
            fontFamily = fontLabel,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        BasicTextField(
            value = text,
            onValueChange = onChange,
            textStyle = TextStyle(
                fontFamily = SourceSans3, // ✅ Pakai langsung
                fontSize = 16.sp,
                color = Color(0xFF444444)
            ),
            modifier = Modifier
                .padding(vertical = 6.dp)
                .fillMaxWidth()
        )

        Divider(color = Color(0xFFE0E0E0))
    }
}

// -----------------------------------------------------------------
// 2. KOMPONEN SUFFIX (Untuk Age, Height, Weight) - "cm", "kg", "years"
// -----------------------------------------------------------------
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
            color = Color(0xFF2E7D32),
            fontFamily = fontLabel,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        // Wadah Row agar Text Field dan Suffix bersebelahan
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // 1. Input Field: Hapus fillMaxWidth, ganti jadi wrapContentWidth
            // Agar lebarnya hanya SELEBAR ANGKA yang diketik
            BasicTextField(
                value = text,
                onValueChange = onChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    fontFamily = SourceSans3,
                    fontSize = 16.sp,
                    color = Color(0xFF444444)
                ),
                // ✅ PENTING: IntrinsicSize.Min atau wrapContent agar tidak serakah tempat
                modifier = Modifier.width(IntrinsicSize.Min)
            )

            // 2. Suffix: Langsung di sebelahnya (1 spasi)
            Text(
                text = " $suffix", // Spasi manual di sini
                style = TextStyle(
                    fontFamily = SourceSans3,
                    fontSize = 16.sp,
                    color = Color(0xFF444444)
                )
            )
        }

        Divider(color = Color(0xFFE0E0E0))
    }
}

// -----------------------------------------------------------------
// 3. KOMPONEN GENDER (Dropdown: Male / Female)
// -----------------------------------------------------------------
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
            color = Color(0xFF2E7D32),
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
                        fontFamily = SourceSans3, // ✅ Pakai langsung
                        fontSize = 16.sp,
                        color = Color(0xFF444444)
                    )
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color(0xFF444444)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                DropdownMenuItem(
                    text = { Text("Male", fontFamily = SourceSans3) }, // ✅ Pakai langsung
                    onClick = {
                        onGenderChange("MALE")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Female", fontFamily = SourceSans3) }, // ✅ Pakai langsung
                    onClick = {
                        onGenderChange("FEMALE")
                        expanded = false
                    }
                )
            }
        }

        Divider(color = Color(0xFFE0E0E0))
    }
}