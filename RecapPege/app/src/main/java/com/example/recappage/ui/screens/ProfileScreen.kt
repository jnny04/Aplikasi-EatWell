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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.recappage.R
import com.example.recappage.ui.components.Component18
import com.example.recappage.ui.viewmodel.RegistrationViewModel
import com.example.recappage.ui.viewmodel.StreakViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    streakViewModel: StreakViewModel,
    regViewModel: RegistrationViewModel,
    modifier: Modifier = Modifier
) {
    val serif = FontFamily(Font(R.font.source_serif_pro_regular))
    val serifBold = FontFamily(Font(R.font.source_serif_pro_bold))

    // ✅ Load streak
    val streakDays by streakViewModel.streakDays.collectAsState()

    // ✅ Load Firestore data ONCE
    LaunchedEffect(Unit) {
        regViewModel.loadUserProfile()
    }

    // ✅ Loading indicator while fetching Firestore
    val isLoading = regViewModel.isLoading.value

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    val scroll = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // Header
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
            // ✅ UI loading
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
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

                // Top bar
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
                        .background(Color.White)
                ) {

                    // Background blur
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
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
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { scaleX = 1.2f; scaleY = 1.2f }
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    // Avatar
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFFE0E0E0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(106.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (imageUri != null) {
                                    AsyncImage(
                                        model = imageUri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(100.dp).clip(CircleShape)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF9E9E9E),
                                        modifier = Modifier.size(100.dp)
                                    )
                                }
                            }
                        }

                        Image(
                            painter = painterResource(id = R.drawable.camera_profile),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp)
                                .offset(x = (-4).dp, y = (-4).dp)
                                .clickable { pickImage.launch("image/*") }
                        )
                    }

                    // Username
                    Text(
                        text = regViewModel.username.value,
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontFamily = serifBold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 32.dp, bottom = 50.dp)
                    )

                    // Streak
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 28.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.streak),
                            contentDescription = null,
                            modifier = Modifier.size(150.dp)
                        )
                        Text(
                            text = "$streakDays days",
                            color = Color(0xFF555555),
                            fontSize = 20.sp,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = (-30).dp)
                        )
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

                    EditableField("Email", regViewModel.email.value) { regViewModel.email.value = it }
                    EditableField("Gender", regViewModel.gender.value ?: "") { regViewModel.gender.value = it }
                    EditableField("Age", regViewModel.age.value) { regViewModel.age.value = it }
                    EditableField("Height", regViewModel.height.value) { regViewModel.height.value = it }
                    EditableField("Weight", regViewModel.weight.value) { regViewModel.weight.value = it }
                }

                Spacer(Modifier.height(20.dp))

                // ===========================
                //   SAVE BUTTON
                // ===========================
                Button(
                    onClick = {
                        regViewModel.updateUserProfile(
                            onSuccess = { println("Profile updated") },
                            onFailure = { println("Error: $it") }
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


@Composable
private fun EditableField(
    label: String,
    text: String,
    onChange: (String) -> Unit
) {
    val font = FontFamily(Font(R.font.source_serif_pro_regular))

    Column {
        Text(
            text = label,
            color = Color(0xFF2E7D32),
            fontFamily = font,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        BasicTextField(
            value = text,
            onValueChange = onChange,
            textStyle = TextStyle(
                fontFamily = font,
                fontSize = 14.sp,
                color = Color(0xFF444444)
            ),
            modifier = Modifier
                .padding(vertical = 6.dp)
                .fillMaxWidth()
        )

        Divider(color = Color(0xFFE0E0E0))
    }
}
